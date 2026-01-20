package repositories;

import core.database.ConnectionPool;
import core.database.PooledConnection;
import core.database.Repository;
import mvvm.models.AirCraft;
import mvvm.models.Flight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class FlightsRepository implements Repository<Flight, Integer> {
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
        Optional<Flight> flight = Optional.empty();
        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT flight.*, aircraft.total_capacity, aircraft.model, aircraft.code, aircraft.business_capacity, aircraft.economy_capacity FROM flight inner join aircraft on aircraft.id = flight.id where flight.id = ? "
            );
            preparedStatement.setInt(1, id);
            var result = preparedStatement.executeQuery();
            if (result.next()) {
                flight = Optional.of(new Flight(
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
        return flight;
    }

    public List<Flight> searchFlights(String departure, String arrival, String fromDate, int passengerCount) {
        List<Flight> flights = new ArrayList<>();

        // Parse date (assuming "dd/MM/yyyy")
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
                        // For now we can leave aircraft as null, or you can join it if needed
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
        return List.of();
    }

    @Override
    public List<Flight> selectWhereIn(String field, Object... value) {
        return List.of();
    }

    @Override
    public boolean create(Flight value) {
        return false;
    }

    @Override
    public Flight update(Flight flight) {
        return null;
    }

    @Override
    public boolean delete(Flight flight) {
        return false;
    }
}
