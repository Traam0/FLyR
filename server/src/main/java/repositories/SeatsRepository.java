package repositories;

import core.database.ConnectionPool;
import mvvm.models.Seat;
import mvvm.models.SeatClass;
import mvvm.models.SeatStatus;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SeatsRepository {
    private final ConnectionPool connectionPool;
    private final Logger logger;

    public SeatsRepository(ConnectionPool connectionPool, Logger logger) {
        this.connectionPool = connectionPool;
        this.logger = logger;
    }


    public List<Seat> selectFlightSeats(int flightId) {
        var seats = new ArrayList<Seat>();
        var sql = """
                SELECT
                    s.id AS seat_id,
                    s.seat_number,
                    s.seat_class,
                    s.seat_status
                FROM
                    seat s
                JOIN
                    aircraft a ON s.aircraft_id = a.id
                JOIN
                    flight f ON a.id = f.aircraft_id
                WHERE
                    f.id = ?;
                """;

        try (var pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, flightId);
                try (var result = statement.executeQuery()) {
                    while (result.next()) {
                        seats.add(new Seat(
                                result.getInt("seat_id"),
                                result.getString("seat_number"),
                                SeatClass.valueOf(result.getString("seat_class")),
                                SeatStatus.valueOf(result.getString("seat_status")),
                                null
                        ));
                    }
                }
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while borrowing connection: %s", e.getMessage()));

        }

        return seats;
    }
}
