package repositories;

import core.database.ConnectionPool;
import core.database.PooledConnection;
import mvvm.models.Flight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FlightsRepository {
    private final ConnectionPool connectionPool;
    private final Logger logger;

    public FlightsRepository(ConnectionPool connectionPool, Logger logger) {
        this.connectionPool = connectionPool;
        this.logger = logger;
    }


    public List<Flight> getAll() {
        List<Flight> flights = new ArrayList<>();
        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM flights");
            var result = preparedStatement.executeQuery();
            while (result.next()) {
                flights.add(new Flight(
                                result.getInt("id"),
                                result.getString("flight_number"),
                                result.getString("departure_city"),
                                result.getString("destination_city"),
                                result.getTimestamp("departureDateTime").toLocalDateTime(),
                                null
                        )
                );
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while borrowing connection: %s", e.getMessage()));
        }
        return flights;
    }
}
