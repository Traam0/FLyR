package repositories;

import core.database.ConnectionPool;
import core.database.PooledConnection;
import core.database.Repository;
import mvvm.models.Client;
import mvvm.models.Role;
import mvvm.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRepository implements Repository<Client, Integer> {

    private final ConnectionPool connectionPool;
    private final Logger logger;

    public ClientRepository(ConnectionPool connectionPool, Logger logger) {
        this.connectionPool = connectionPool;
        this.logger = logger;
    }

    public Optional<Client> findClient(int userId) {
        var sql = """
                SELECT * FROM client
                WHERE user_id = ?;
                """;
        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        return Optional.of(new Client(
                                result.getInt("id"),
                                result.getString("first_name"),
                                result.getString("last_name"),
                                result.getString("email"),
                                result.getString("phone"),
                                result.getString("passport_number"),
                                result.getTimestamp("birth_date").toLocalDateTime().toLocalDate(),
                                result.getInt("user_id")
                        ));
                    }
                    logger.log(Level.WARNING, "User not found. no next()");
                }
            }
        } catch (Exception e) {
            this.logger.severe(String.format("Error while selecting where username:  %s", e.getMessage()));
        }
        return Optional.empty();
    }

    @Override
    public List<Client> selectAll() {
        return List.of();
    }

    @Override
    public Optional<Client> selectWhereId(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Client> selectWhereEq(String field, Object value) {
        return List.of();
    }

    @Override
    public List<Client> selectWhereIn(String field, Object... value) {
        return List.of();
    }

    @Override
    public boolean create(Client value) {
        return false;
    }

    @Override
    public Client update(Client client) {
        return null;
    }

    @Override
    public boolean delete(Client client) {
        return false;
    }
}
