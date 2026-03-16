package com.dotinc.libsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class LibSqlStatement implements Statement {

    protected final LibSqlConnection connection;
    protected LibSqlResultSet lastResultSet;
    protected int updateCount;
    protected boolean closed;

    public LibSqlStatement(LibSqlConnection connection) {
        this.connection = connection;
        this.lastResultSet = null;
        this.updateCount = -1;
        this.closed = false;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        checkClosed();
        JsonObject result = connection.executePipeline(sql, null);

        JsonArray cols = result.getAsJsonArray("cols");
        JsonArray rows = result.getAsJsonArray("rows");

        boolean hasCols = cols != null && !cols.isEmpty();

        if (hasCols) {
            lastResultSet = new LibSqlResultSet(cols, rows);
            updateCount = -1;
        } else {
            lastResultSet = null;
            updateCount = result.has("affected_row_count") && !result.get("affected_row_count").isJsonNull()
                    ? result.get("affected_row_count").getAsInt()
                    : 0;
        }

        return hasCols;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        execute(sql);
        if (lastResultSet == null) {
            // Create an empty result set for queries that return no columns
            lastResultSet = new LibSqlResultSet(new JsonArray(), new JsonArray());
        }
        return lastResultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        checkClosed();
        JsonObject result = connection.executePipeline(sql, null);
        int affectedRows = result.has("affected_row_count") && !result.get("affected_row_count").isJsonNull()
                ? result.get("affected_row_count").getAsInt()
                : 0;
        updateCount = affectedRows;
        lastResultSet = null;
        return affectedRows;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        checkClosed();
        return lastResultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        checkClosed();
        return updateCount;
    }

    @Override
    public void close() throws SQLException {
        if (!closed) {
            closed = true;
            if (lastResultSet != null) {
                lastResultSet.close();
                lastResultSet = null;
            }
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public Connection getConnection() throws SQLException {
        checkClosed();
        return connection;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public int getMaxRows() throws SQLException {
        checkClosed();
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        checkClosed();
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public int getFetchSize() throws SQLException {
        checkClosed();
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public int getResultSetType() throws SQLException {
        checkClosed();
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        checkClosed();
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        checkClosed();
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        checkClosed();
        // Single result set per execute
        if (lastResultSet != null) {
            lastResultSet.close();
            lastResultSet = null;
        }
        return false;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        checkClosed();
        return getMoreResults();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        checkClosed();
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("setCursorName is not supported by libSQL driver");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        checkClosed();
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        checkClosed();
        // No-op — only FETCH_FORWARD is supported
    }

    @Override
    public void cancel() throws SQLException {
        throw new SQLFeatureNotSupportedException("cancel is not supported by libSQL driver");
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("addBatch is not supported by libSQL driver");
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("clearBatch is not supported by libSQL driver");
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("executeBatch is not supported by libSQL driver");
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return new LibSqlResultSet(new JsonArray(), new JsonArray());
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(sql);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return execute(sql);
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public boolean isPoolable() throws SQLException {
        checkClosed();
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        checkClosed();
        return false;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(getClass());
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(getClass())) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap to " + iface.getName());
    }

    protected void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("Statement is closed");
        }
    }
}
