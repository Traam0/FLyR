package core.database;

import java.sql.Connection;

public final class PooledConnection implements AutoCloseable {
    private final ConnectionPool pool;
    private final Connection connection;
    private boolean isDisposed;

    PooledConnection(ConnectionPool connectionPool, Connection connection) {
        this.pool = connectionPool;
        this.connection = connection;
        this.isDisposed = false;
    }

    public Connection get() {
        return this.connection;
    }

    @Override
    public void close() throws Exception {
        if (this.isDisposed) this.pool.yield(this.connection);
        this.isDisposed = true;
    }
}