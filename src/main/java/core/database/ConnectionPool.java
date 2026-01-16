package core.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ConnectionPool {
    private final int MAX_CAPACITY;
    private final String connectionString;

    private final List<Connection> pool;
    private final List<Connection> usedConnections;

    public ConnectionPool(int maxCapacity, String connectionString) throws SQLException {
        if (maxCapacity <= 0) throw new IllegalArgumentException("pool capacity cannot be less or equal to 0.");
        this.MAX_CAPACITY = maxCapacity;

        this.connectionString = connectionString;

        this.pool = new ArrayList<>(maxCapacity);
        this.usedConnections = new ArrayList<>(maxCapacity);

        for (int i = 0; i < maxCapacity; i++) this.pool.add(this.createConnection());
    }

    public boolean hasConnectionAvailable() {
        return !this.pool.isEmpty();
    }


    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(this.connectionString);
    }

    public synchronized PooledConnection borrow() throws SQLException {
        // if not connectionAvailable (pool empty) and borrowed connection is less than pool capacity, then fill pool with a connection
        if (!this.hasConnectionAvailable() && this.usedConnections.size() < this.MAX_CAPACITY) {
            this.pool.add(this.createConnection());
        }

        //otherwise, the pool is empty meaning cant spear a connection, then wait for one to be released
        while (!this.hasConnectionAvailable()) {
            try {
                wait();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }

        Connection connection = this.pool.removeFirst();
        this.usedConnections.add(connection);
        return new PooledConnection(this, connection);
    }

    synchronized void yield(Connection connection) {
        if (connection == null) return;
        this.usedConnections.remove(connection);
        this.pool.add(connection);
        notifyAll();
    }
}