package repositories;

import core.database.ConnectionPool;
import core.database.PooledConnection;
import core.database.Repository;
import mvvm.models.Role;
import mvvm.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository implements Repository<User, Integer> {
    private final ConnectionPool connectionPool;
    private final Logger logger;

    public UserRepository(ConnectionPool connectionPool, Logger logger) {
        this.connectionPool = connectionPool;
        this.logger = logger;
    }

    @Override
    public List<User> selectAll() {
        return List.of();
    }

    @Override
    public Optional<User> selectWhereId(Integer id) {
        return Optional.empty();
    }

    public Optional<User> selectWhereUsername(String username) {
        var sql = """
                SELECT * FROM user
                WHERE username = ?;
                """;
        try (PooledConnection pooledConnection = this.connectionPool.borrow()) {
            Connection connection = pooledConnection.get();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                try (ResultSet result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        return Optional.of(new User(
                                result.getInt("id"),
                                result.getString("username"),
                                result.getString("password"),
                                Role.valueOf(result.getString("role"))
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
    public List<User> selectWhereEq(String field, Object value) {
        return List.of();
    }

    @Override
    public List<User> selectWhereIn(String field, Object... value) {
        return List.of();
    }

    @Override
    public boolean create(User value) {
        return false;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public boolean delete(User user) {
        return false;
    }
}
