package com.dotinc.libsql;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class LibSqlConnection implements Connection {

    private final String baseUrl;
    private final String authToken;
    private final HttpClient httpClient;
    private final Gson gson;
    private boolean closed;
    private boolean autoCommit;

    public LibSqlConnection(String baseUrl, String authToken) {
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.closed = false;
        this.autoCommit = true;
    }

    /**
     * Executes a single SQL statement via the libSQL HTTP pipeline API.
     * Returns the "result" JsonObject from the first pipeline result.
     */
    public JsonObject executePipeline(String sql, List<Object> args) throws SQLException {
        if (closed) {
            throw new SQLException("Connection is closed");
        }

        try {
            JsonObject stmt = new JsonObject();
            stmt.addProperty("sql", sql);

            JsonArray argsArray = new JsonArray();
            if (args != null) {
                for (Object arg : args) {
                    argsArray.add(toArgJson(arg));
                }
            }
            stmt.add("args", argsArray);

            JsonObject request = new JsonObject();
            request.addProperty("type", "execute");
            request.add("stmt", stmt);

            JsonArray requests = new JsonArray();
            requests.add(request);

            JsonObject body = new JsonObject();
            body.add("requests", requests);

            String jsonBody = gson.toJson(body);

            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v2/pipeline"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if (authToken != null && !authToken.isEmpty()) {
                httpRequestBuilder.header("Authorization", "Bearer " + authToken);
            }

            HttpResponse<String> response = httpClient.send(
                    httpRequestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new SQLException(
                        "HTTP error from libSQL pipeline: status=" + response.statusCode()
                                + ", body=" + response.body()
                );
            }

            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray results = responseJson.getAsJsonArray("results");

            if (results == null || results.isEmpty()) {
                throw new SQLException("Empty results from libSQL pipeline");
            }

            JsonObject firstResult = results.get(0).getAsJsonObject();
            String resultType = firstResult.get("type").getAsString();

            if ("error".equals(resultType)) {
                JsonObject error = firstResult.getAsJsonObject("error");
                String message = error.has("message") ? error.get("message").getAsString() : "Unknown error";
                throw new SQLException("libSQL error: " + message);
            }

            JsonObject responseObj = firstResult.getAsJsonObject("response");
            return responseObj.getAsJsonObject("result");

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Failed to execute pipeline request: " + e.getMessage(), e);
        }
    }

    private JsonObject toArgJson(Object value) {
        JsonObject arg = new JsonObject();

        if (value == null) {
            arg.addProperty("type", "null");
            return arg;
        }

        if (value instanceof Integer || value instanceof Long) {
            arg.addProperty("type", "integer");
            arg.addProperty("value", String.valueOf(value));
            return arg;
        }

        if (value instanceof Float || value instanceof Double) {
            arg.addProperty("type", "float");
            arg.addProperty("value", String.valueOf(value));
            return arg;
        }

        if (value instanceof byte[]) {
            arg.addProperty("type", "blob");
            arg.addProperty("value", Base64.getEncoder().encodeToString((byte[]) value));
            return arg;
        }

        // Default to text for String and anything else
        arg.addProperty("type", "text");
        arg.addProperty("value", String.valueOf(value));
        return arg;
    }

    // --- Implemented Connection methods ---

    @Override
    public Statement createStatement() throws SQLException {
        checkClosed();
        return new LibSqlStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkClosed();
        return new LibSqlPreparedStatement(this, sql);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        checkClosed();
        return new LibSqlDatabaseMetaData(this);
    }

    @Override
    public void close() throws SQLException {
        closed = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkClosed();
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        checkClosed();
        return autoCommit;
    }

    @Override
    public void commit() throws SQLException {
        checkClosed();
        // No-op for HTTP API (always autocommit)
    }

    @Override
    public void rollback() throws SQLException {
        checkClosed();
        // No-op for HTTP API (always autocommit)
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        if (closed) {
            return false;
        }
        try {
            executePipeline("SELECT 1", null);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public String getSchema() throws SQLException {
        checkClosed();
        return "main";
    }

    @Override
    public String getCatalog() throws SQLException {
        checkClosed();
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        checkClosed();
        return Collections.emptyMap();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        checkClosed();
        return Connection.TRANSACTION_SERIALIZABLE;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        checkClosed();
        return false;
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
    public int getHoldability() throws SQLException {
        checkClosed();
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkClosed();
        // No-op
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        checkClosed();
        // No-op — libSQL HTTP API is always serializable
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        checkClosed();
        // No-op
    }

    // --- Methods that throw SQLFeatureNotSupportedException ---

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("Callable statements are not supported by libSQL driver");
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("nativeSQL is not supported by libSQL driver");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException("Callable statements are not supported by libSQL driver");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("Callable statements are not supported by libSQL driver");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareStatement with autoGeneratedKeys is not supported by libSQL driver");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareStatement with columnIndexes is not supported by libSQL driver");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareStatement with columnNames is not supported by libSQL driver");
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("setTypeMap is not supported by libSQL driver");
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException("Savepoints are not supported by libSQL driver");
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("Savepoints are not supported by libSQL driver");
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException("Savepoints are not supported by libSQL driver");
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException("Savepoints are not supported by libSQL driver");
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createClob is not supported by libSQL driver");
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createBlob is not supported by libSQL driver");
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createNClob is not supported by libSQL driver");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException("createSQLXML is not supported by libSQL driver");
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new SQLClientInfoException("setClientInfo is not supported by libSQL driver", Collections.emptyMap());
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new SQLClientInfoException("setClientInfo is not supported by libSQL driver", Collections.emptyMap());
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("getClientInfo is not supported by libSQL driver");
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new SQLFeatureNotSupportedException("getClientInfo is not supported by libSQL driver");
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new SQLFeatureNotSupportedException("createArrayOf is not supported by libSQL driver");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new SQLFeatureNotSupportedException("createStruct is not supported by libSQL driver");
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNetworkTimeout is not supported by libSQL driver");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("getNetworkTimeout is not supported by libSQL driver");
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException("abort is not supported by libSQL driver");
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

    // --- Internal helpers ---

    private void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("Connection is closed");
        }
    }

    String getBaseUrl() {
        return baseUrl;
    }

    String getAuthToken() {
        return authToken;
    }
}
