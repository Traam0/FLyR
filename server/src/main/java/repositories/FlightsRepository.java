package repositories;

import core.database.ConnectionPool;
import core.database.PooledConnection;
import core.database.Repository;
import mvvm.models.AirCraft;
import mvvm.models.Flight;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public final class FlightsRepository implements Repository<Flight, Integer> {
    private final ConnectionPool connectionPool;
    private final Logger logger;

    public FlightsRepository(ConnectionPool connectionPool, Logger logger) {
        this.connectionPool = connectionPool;
        this.logger = logger;
    }


    @Override
    public List<Flight> selectAll() {
        List<Flight> flights = new ArrayList<>();
        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM flight");
            var result = preparedStatement.executeQuery();
            while (result.next()) {
                flights.add(new Flight(
                                result.getInt("id"),
                                result.getString("flight_number"),
                                result.getString("departure_city"),
                                result.getString("destination_city"),
                                result.getTimestamp("departure_date_time").toLocalDateTime(),
                                null
                        )
                );
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while borrowing connection: %s", e.getMessage()));
        }
        return flights;
    }

    @Override
    public Optional<Flight> selectWhereId(Integer id) {
        var sql = """
                SELECT
                    f.id,
                    f.flight_number,
                    f.departure_city,
                    f.destination_city,
                    f.departure_date_time,
                    a.id AS aircraft_id,
                    a.code,
                    a.model,
                    a.total_capacity,
                    a.economy_capacity,
                    a.business_capacity
                FROM flight f
                INNER JOIN aircraft a ON f.aircraft_id = a.id
                WHERE f.id = ?
                """;
        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            var result = preparedStatement.executeQuery();
            if (result.next()) {
                return Optional.of(new Flight(
                        result.getInt("id"),
                        result.getString("flight_number"),
                        result.getString("departure_city"),
                        result.getString("destination_city"),
                        result.getTimestamp("departure_date_time").toLocalDateTime(),
                        new AirCraft(
                                result.getInt("aircraft_id"),
                                result.getString("code"),
                                result.getString("model"),
                                result.getInt("total_capacity"),
                                result.getInt("economy_capacity"),
                                result.getInt("business_capacity")
                        )
                ));
            }

        } catch (Exception e) {
            this.logger.severe(String.format("Error while borrowing connection: %s", e.getMessage()));
        }
        return Optional.empty();
    }

    public List<Flight> searchFlights(String departure, String arrival, String fromDate, int passengerCount) {
        List<Flight> flights = new ArrayList<>();
        LocalDateTime fromDateTime;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            fromDateTime = LocalDateTime.parse(fromDate + " 00:00", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            logger.severe("Invalid fromDate format: " + fromDate);
            return flights;
        }

        String sql = """
                    SELECT f.id, f.flight_number, f.departure_city, f.destination_city, f.departure_date_time, f.aircraft_id
                    FROM flight f
                    JOIN aircraft a ON f.aircraft_id = a.id
                    LEFT JOIN seat s ON s.aircraft_id = a.id AND s.seat_status = 'FREE'
                    WHERE f.departure_city = ?
                      AND f.destination_city = ?
                      AND f.departure_date_time >= ?
                    GROUP BY f.id
                    HAVING COUNT(s.id) >= ?
                    ORDER BY f.departure_date_time ASC
                """;

        try (PooledConnection pooledConnection = connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, departure);
                stmt.setString(2, arrival);
                stmt.setTimestamp(3, Timestamp.valueOf(fromDateTime));
                stmt.setInt(4, passengerCount);

                try (var rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        flights.add(new Flight(
                                rs.getInt("id"),
                                rs.getString("flight_number"),
                                rs.getString("departure_city"),
                                rs.getString("destination_city"),
                                rs.getTimestamp("departure_date_time").toLocalDateTime(),
                                null
                        ));
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("Error searching flights: " + e.getMessage());
        }

        return flights;
    }


    @Override
    public List<Flight> selectWhereEq(String field, Object value) {

        List<Flight> flights = new ArrayList<>();
        var sql = """
                SELECT
                     f.id,
                     f.flight_number,
                     f.departure_city,
                     f.destination_city,
                     f.departure_date_time,
                     a.id AS aircraft_id,
                     a.code,
                     a.model,
                     a.total_capacity,
                     a.economy_capacity,
                     a.business_capacity
                 FROM flight f
                 INNER JOIN aircraft a ON f.aircraft_id = a.id
                 WHERE f.%s = ?
                """;

        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (PreparedStatement preparedStatement = connection.prepareStatement(String.format(sql, field))) {
                preparedStatement.setObject(1, value);
                __(flights, preparedStatement);
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while selecting where %s = %s: %s",
                    field, value, e.getMessage()));
        }
        return flights;
    }

    @Override
    public List<Flight> selectWhereIn(String field, Object... values) {
        List<Flight> flights = new ArrayList<>();
        if (values.length == 0) return flights;

        // Build placeholders for IN clause
        String placeholders = String.join(",", java.util.Collections.nCopies(values.length, "?"));
        String sql = String.format("""
                SELECT 
                    f.id,
                    f.flight_number,
                    f.departure_city,
                    f.destination_city,
                    f.departure_date_time,
                    a.id AS aircraft_id,
                    a.code,
                    a.model,
                    a.total_capacity,
                    a.economy_capacity,
                    a.business_capacity
                FROM flight f
                INNER JOIN aircraft a ON f.aircraft_id = a.id
                WHERE f.%s IN (%s)
                """, field, placeholders);

        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (int i = 0; i < values.length; i++) {
                    preparedStatement.setObject(i + 1, values[i]);
                }
                __(flights, preparedStatement);
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while selecting where %s IN: %s",
                    field, e.getMessage()));
        }
        return flights;
    }

    @Override
    public boolean create(Flight value) {
        String sql = """
                INSERT INTO flight (flight_number, departure_city, destination_city, 
                                   departure_date_time, aircraft_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, value.getFlightNumber());
                preparedStatement.setString(2, value.getDepartureCity());
                preparedStatement.setString(3, value.getDestinationCity());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(value.getDepartureDateTime()));
                preparedStatement.setInt(5, value.getAirCraft().getId());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
//                    logger.info(String.format("Created flight: %s", value.getFlightNumber()));
                    return true;
                }
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while creating flight: %s", e.getMessage()));
        }
        return false;
    }

    @Override
    public Flight update(Flight value) {
        String sql = """
                UPDATE flight
                SET flight_number = ?,
                    departure_city = ?,
                    destination_city = ?,
                    departure_date_time = ?,
                    aircraft_id = ?
                WHERE id = ?
                """;

        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, value.getFlightNumber());
                preparedStatement.setString(2, value.getDepartureCity());
                preparedStatement.setString(3, value.getDestinationCity());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(value.getDepartureDateTime()));
                preparedStatement.setInt(5, value.getAirCraft().getId());
                preparedStatement.setInt(6, value.getId());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    logger.info(String.format("Updated flight ID: %d", value.getId()));
                    return value;
                }
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while updating flight: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public boolean delete(Flight value) {
        String sql = "DELETE FROM flight WHERE id = ?";

        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, value.getId());
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    logger.info(String.format("Deleted flight ID: %d", value.getId()));
                    return true;
                }
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while deleting flight: %s", e.getMessage()));
        }
        return false;
    }

    private void __(List<Flight> flights, PreparedStatement preparedStatement) throws SQLException {
        var result = preparedStatement.executeQuery();
        while (result.next()) {
            flights.add(new Flight(
                    result.getInt("id"),
                    result.getString("flight_number"),
                    result.getString("departure_city"),
                    result.getString("destination_city"),
                    result.getTimestamp("departure_date_time").toLocalDateTime(),
                    new AirCraft(
                            result.getInt("aircraft_id"),
                            result.getString("code"),
                            result.getString("model"),
                            result.getInt("total_capacity"),
                            result.getInt("economy_capacity"),
                            result.getInt("business_capacity")
                    )
            ));
        }
    }
}
