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

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger("com.dotinc.libsql");

    static {
        try {
            java.util.logging.FileHandler fh = new java.util.logging.FileHandler(
                System.getProperty("java.io.tmpdir") + "/libsql-driver.log", true);
            fh.setFormatter(new java.util.logging.SimpleFormatter());
            LOG.addHandler(fh);
            LOG.setLevel(java.util.logging.Level.ALL);
        } catch (Exception e) {
            // ignore
        }
    }

    private final String baseUrl;
    private final String authToken;
    private final HttpClient httpClient;
    private final Gson gson;
    private boolean closed;
    private boolean autoCommit;

    public LibSqlConnection(String baseUrl, String authToken) {
        LOG.info("LibSqlConnection constructor called with baseUrl=" + baseUrl);
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
        LOG.info("executePipeline called with sql=" + sql);
        try {
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
                JsonObject result = responseObj.getAsJsonObject("result");
                LOG.info("executePipeline returning result for sql=" + sql);
                return result;

            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new SQLException("Failed to execute pipeline request: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            LOG.severe("executePipeline failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
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
        LOG.info("createStatement called");
        try {
            checkClosed();
            Statement result = new LibSqlStatement(this);
            LOG.info("createStatement returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("createStatement failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        LOG.info("prepareStatement called with sql=" + sql);
        try {
            checkClosed();
            PreparedStatement result = new LibSqlPreparedStatement(this, sql);
            LOG.info("prepareStatement returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("prepareStatement failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        LOG.info("getMetaData called");
        try {
            checkClosed();
            DatabaseMetaData result = new LibSqlDatabaseMetaData(this);
            LOG.info("getMetaData returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("getMetaData failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void close() throws SQLException {
        LOG.info("close called");
        try {
            closed = true;
        } catch (Exception e) {
            LOG.severe("close failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        LOG.info("isClosed called");
        try {
            boolean result = closed;
            LOG.info("isClosed returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("isClosed failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        LOG.info("setAutoCommit called with autoCommit=" + autoCommit);
        try {
            checkClosed();
            this.autoCommit = autoCommit;
        } catch (Exception e) {
            LOG.severe("setAutoCommit failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        LOG.info("getAutoCommit called");
        try {
            checkClosed();
            boolean result = autoCommit;
            LOG.info("getAutoCommit returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("getAutoCommit failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void commit() throws SQLException {
        LOG.info("commit called");
        try {
            checkClosed();
            // No-op for HTTP API (always autocommit)
        } catch (Exception e) {
            LOG.severe("commit failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void rollback() throws SQLException {
        LOG.info("rollback called");
        try {
            checkClosed();
            // No-op for HTTP API (always autocommit)
        } catch (Exception e) {
            LOG.severe("rollback failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        LOG.info("isValid called with timeout=" + timeout);
        if (closed) {
            LOG.info("isValid returning: false (closed)");
            return false;
        }
        try {
            executePipeline("SELECT 1", null);
            LOG.info("isValid returning: true");
            return true;
        } catch (SQLException e) {
            String msg = e.getMessage();
            LOG.severe("isValid failed: " + msg);
            // Surface auth errors clearly instead of silently returning false
            if (msg != null && (msg.contains("401") || msg.contains("nauthorized") || msg.contains("empty JWT"))) {
                throw new SQLException("Authentication failed. Check that the auth token is entered in the Password field of the Data Source (not the Driver). Server response: " + msg, e);
            }
            if (msg != null && (msg.contains("403") || msg.contains("orbidden"))) {
                throw new SQLException("Access denied. The auth token may be expired or lack permissions. Server response: " + msg, e);
            }
            // For other errors (network, DNS, etc.), also surface them
            throw new SQLException("Connection validation failed: " + msg, e);
        }
    }

    @Override
    public String getSchema() throws SQLException {
        LOG.info("getSchema called");
        try {
            checkClosed();
            String result = "main";
            LOG.info("getSchema returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("getSchema failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        LOG.info("getCatalog called");
        try {
            checkClosed();
            LOG.info("getCatalog returning: null");
            return null;
        } catch (Exception e) {
            LOG.severe("getCatalog failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        LOG.info("setSchema called with schema=" + schema);
        try {
            checkClosed();
            // No-op
        } catch (Exception e) {
            LOG.severe("setSchema failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        LOG.info("setCatalog called with catalog=" + catalog);
        try {
            checkClosed();
            // No-op
        } catch (Exception e) {
            LOG.severe("setCatalog failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        LOG.info("getTypeMap called");
        try {
            checkClosed();
            Map<String, Class<?>> result = Collections.emptyMap();
            LOG.info("getTypeMap returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("getTypeMap failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        LOG.info("getTransactionIsolation called");
        try {
            checkClosed();
            int result = Connection.TRANSACTION_SERIALIZABLE;
            LOG.info("getTransactionIsolation returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("getTransactionIsolation failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        LOG.info("isReadOnly called");
        try {
            checkClosed();
            boolean result = false;
            LOG.info("isReadOnly returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("isReadOnly failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        LOG.info("getWarnings called");
        try {
            checkClosed();
            LOG.info("getWarnings returning: null");
            return null;
        } catch (Exception e) {
            LOG.severe("getWarnings failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        LOG.info("clearWarnings called");
        try {
            checkClosed();
            // No-op
        } catch (Exception e) {
            LOG.severe("clearWarnings failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        LOG.info("getHoldability called");
        try {
            checkClosed();
            int result = ResultSet.CLOSE_CURSORS_AT_COMMIT;
            LOG.info("getHoldability returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("getHoldability failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        LOG.info("createStatement(int,int) called with resultSetType=" + resultSetType + ", resultSetConcurrency=" + resultSetConcurrency);
        try {
            Statement result = createStatement();
            LOG.info("createStatement(int,int) returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("createStatement(int,int) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        LOG.info("createStatement(int,int,int) called with resultSetType=" + resultSetType + ", resultSetConcurrency=" + resultSetConcurrency + ", resultSetHoldability=" + resultSetHoldability);
        try {
            Statement result = createStatement();
            LOG.info("createStatement(int,int,int) returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("createStatement(int,int,int) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        LOG.info("prepareStatement(String,int,int) called with sql=" + sql);
        try {
            PreparedStatement result = prepareStatement(sql);
            LOG.info("prepareStatement(String,int,int) returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("prepareStatement(String,int,int) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        LOG.info("prepareStatement(String,int,int,int) called with sql=" + sql);
        try {
            PreparedStatement result = prepareStatement(sql);
            LOG.info("prepareStatement(String,int,int,int) returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("prepareStatement(String,int,int,int) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        LOG.info("setReadOnly called with readOnly=" + readOnly);
        try {
            checkClosed();
            // No-op
        } catch (Exception e) {
            LOG.severe("setReadOnly failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        LOG.info("setTransactionIsolation called with level=" + level);
        try {
            checkClosed();
            // No-op — libSQL HTTP API is always serializable
        } catch (Exception e) {
            LOG.severe("setTransactionIsolation failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        LOG.info("setHoldability called with holdability=" + holdability);
        try {
            checkClosed();
            // No-op
        } catch (Exception e) {
            LOG.severe("setHoldability failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    // --- Methods that throw SQLFeatureNotSupportedException ---

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        LOG.info("prepareCall called with sql=" + sql);
        try {
            throw new SQLFeatureNotSupportedException("Callable statements are not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("prepareCall failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        LOG.info("nativeSQL called with sql=" + sql);
        try {
            String result = sql;
            LOG.info("nativeSQL returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("nativeSQL failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        LOG.info("prepareCall(String,int,int) called with sql=" + sql);
        try {
            throw new SQLFeatureNotSupportedException("Callable statements are not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("prepareCall(String,int,int) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        LOG.info("prepareCall(String,int,int,int) called with sql=" + sql);
        try {
            throw new SQLFeatureNotSupportedException("Callable statements are not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("prepareCall(String,int,int,int) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        LOG.info("prepareStatement(String,int) called with sql=" + sql + ", autoGeneratedKeys=" + autoGeneratedKeys);
        try {
            PreparedStatement result = prepareStatement(sql);
            LOG.info("prepareStatement(String,int) returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("prepareStatement(String,int) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        LOG.info("prepareStatement(String,int[]) called with sql=" + sql);
        try {
            PreparedStatement result = prepareStatement(sql);
            LOG.info("prepareStatement(String,int[]) returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("prepareStatement(String,int[]) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        LOG.info("prepareStatement(String,String[]) called with sql=" + sql);
        try {
            PreparedStatement result = prepareStatement(sql);
            LOG.info("prepareStatement(String,String[]) returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("prepareStatement(String,String[]) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        LOG.info("setTypeMap called");
        try {
            throw new SQLFeatureNotSupportedException("setTypeMap is not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("setTypeMap failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        LOG.info("setSavepoint called");
        try {
            throw new SQLFeatureNotSupportedException("Savepoints are not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("setSavepoint failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        LOG.info("setSavepoint called with name=" + name);
        try {
            throw new SQLFeatureNotSupportedException("Savepoints are not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("setSavepoint(String) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        LOG.info("rollback(Savepoint) called");
        try {
            throw new SQLFeatureNotSupportedException("Savepoints are not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("rollback(Savepoint) failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        LOG.info("releaseSavepoint called");
        try {
            throw new SQLFeatureNotSupportedException("Savepoints are not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("releaseSavepoint failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Clob createClob() throws SQLException {
        LOG.info("createClob called");
        try {
            throw new SQLFeatureNotSupportedException("createClob is not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("createClob failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Blob createBlob() throws SQLException {
        LOG.info("createBlob called");
        try {
            throw new SQLFeatureNotSupportedException("createBlob is not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("createBlob failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public NClob createNClob() throws SQLException {
        LOG.info("createNClob called");
        try {
            throw new SQLFeatureNotSupportedException("createNClob is not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("createNClob failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        LOG.info("createSQLXML called");
        try {
            throw new SQLFeatureNotSupportedException("createSQLXML is not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("createSQLXML failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        LOG.info("setClientInfo called with name=" + name + ", value=" + value);
        // no-op
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        LOG.info("setClientInfo(Properties) called");
        // no-op
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        LOG.info("getClientInfo called with name=" + name);
        try {
            LOG.info("getClientInfo returning: null");
            return null;
        } catch (Exception e) {
            LOG.severe("getClientInfo failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        LOG.info("getClientInfo called");
        try {
            Properties result = new Properties();
            LOG.info("getClientInfo returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("getClientInfo failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        LOG.info("createArrayOf called with typeName=" + typeName);
        try {
            throw new SQLFeatureNotSupportedException("createArrayOf is not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("createArrayOf failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        LOG.info("createStruct called with typeName=" + typeName);
        try {
            throw new SQLFeatureNotSupportedException("createStruct is not supported by libSQL driver");
        } catch (Exception e) {
            LOG.severe("createStruct failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        LOG.info("setNetworkTimeout called with milliseconds=" + milliseconds);
        try {
            // no-op
        } catch (Exception e) {
            LOG.severe("setNetworkTimeout failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        LOG.info("getNetworkTimeout called");
        try {
            int result = 0;
            LOG.info("getNetworkTimeout returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("getNetworkTimeout failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        LOG.info("abort called");
        try {
            closed = true;
        } catch (Exception e) {
            LOG.severe("abort failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        LOG.info("isWrapperFor called with iface=" + iface);
        try {
            boolean result = iface.isAssignableFrom(getClass());
            LOG.info("isWrapperFor returning: " + result);
            return result;
        } catch (Exception e) {
            LOG.severe("isWrapperFor failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        LOG.info("unwrap called with iface=" + iface);
        try {
            if (iface.isAssignableFrom(getClass())) {
                T result = iface.cast(this);
                LOG.info("unwrap returning: " + result);
                return result;
            }
            throw new SQLException("Cannot unwrap to " + iface.getName());
        } catch (Exception e) {
            LOG.severe("unwrap failed: " + e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
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
