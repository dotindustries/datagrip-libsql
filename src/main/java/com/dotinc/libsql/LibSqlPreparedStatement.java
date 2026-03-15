package com.dotinc.libsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LibSqlPreparedStatement extends LibSqlStatement implements PreparedStatement {

    private final String sql;
    private final Map<Integer, Object> parameters;

    public LibSqlPreparedStatement(LibSqlConnection connection, String sql) {
        super(connection);
        this.sql = sql;
        this.parameters = new TreeMap<>();
    }

    @Override
    public boolean execute() throws SQLException {
        checkClosed();
        JsonObject result = connection.executePipeline(sql, getOrderedParams());

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
    public ResultSet executeQuery() throws SQLException {
        checkClosed();
        execute();
        if (lastResultSet == null) {
            lastResultSet = new LibSqlResultSet(new JsonArray(), new JsonArray());
        }
        return lastResultSet;
    }

    @Override
    public int executeUpdate() throws SQLException {
        checkClosed();
        JsonObject result = connection.executePipeline(sql, getOrderedParams());
        int affectedRows = result.has("affected_row_count") && !result.get("affected_row_count").isJsonNull()
                ? result.get("affected_row_count").getAsInt()
                : 0;
        updateCount = affectedRows;
        lastResultSet = null;
        return affectedRows;
    }

    // --- Parameter setters ---

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, null);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, x ? 1 : 0);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, (int) x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, (int) x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        checkClosed();
        if (x == null) {
            parameters.put(parameterIndex, null);
        } else {
            parameters.put(parameterIndex, x.toPlainString());
        }
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        checkClosed();
        if (x == null) {
            parameters.put(parameterIndex, null);
        } else {
            parameters.put(parameterIndex, x.toString());
        }
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        checkClosed();
        if (x == null) {
            parameters.put(parameterIndex, null);
        } else {
            parameters.put(parameterIndex, x.toString());
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        checkClosed();
        if (x == null) {
            parameters.put(parameterIndex, null);
        } else {
            parameters.put(parameterIndex, x.toString());
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        setDate(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        setTimestamp(parameterIndex, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        checkClosed();
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        setObject(parameterIndex, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        setObject(parameterIndex, x);
    }

    @Override
    public void clearParameters() throws SQLException {
        checkClosed();
        parameters.clear();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        setNull(parameterIndex, sqlType);
    }

    // --- Metadata ---

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException("getParameterMetaData is not supported by libSQL driver");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        // Not known before execution
        return null;
    }

    // --- Batch ---

    @Override
    public void addBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("addBatch is not supported by libSQL driver");
    }

    // --- Unsupported setters ---

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setAsciiStream is not supported by libSQL driver");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setUnicodeStream is not supported by libSQL driver");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setBinaryStream is not supported by libSQL driver");
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setRef is not supported by libSQL driver");
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setBlob is not supported by libSQL driver");
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setClob is not supported by libSQL driver");
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setArray is not supported by libSQL driver");
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setURL is not supported by libSQL driver");
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setRowId is not supported by libSQL driver");
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        // Treat as regular string
        setString(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNCharacterStream is not supported by libSQL driver");
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNClob is not supported by libSQL driver");
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setClob with Reader is not supported by libSQL driver");
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setBlob with InputStream is not supported by libSQL driver");
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNClob with Reader is not supported by libSQL driver");
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("setSQLXML is not supported by libSQL driver");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setAsciiStream is not supported by libSQL driver");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setBinaryStream is not supported by libSQL driver");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setCharacterStream is not supported by libSQL driver");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("setCharacterStream is not supported by libSQL driver");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setAsciiStream is not supported by libSQL driver");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("setBinaryStream is not supported by libSQL driver");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("setCharacterStream is not supported by libSQL driver");
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNCharacterStream is not supported by libSQL driver");
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("setClob with Reader is not supported by libSQL driver");
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("setBlob with InputStream is not supported by libSQL driver");
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNClob with Reader is not supported by libSQL driver");
    }

    // --- Private helpers ---

    private List<Object> getOrderedParams() {
        if (parameters.isEmpty()) {
            return List.of();
        }
        List<Object> ordered = new ArrayList<>();
        for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
            // Fill gaps with null if parameter indices are not contiguous
            while (ordered.size() < entry.getKey() - 1) {
                ordered.add(null);
            }
            ordered.add(entry.getValue());
        }
        return ordered;
    }
}
