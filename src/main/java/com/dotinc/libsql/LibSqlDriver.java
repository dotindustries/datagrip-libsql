package com.dotinc.libsql;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class LibSqlDriver implements Driver {

    private static final String URL_PREFIX = "jdbc:libsql:";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;

    static {
        try {
            DriverManager.registerDriver(new LibSqlDriver());
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        String baseUrl = url.substring(URL_PREFIX.length());

        // Step 1: Always extract and strip query params from URL
        String urlAuthToken = null;
        int queryStart = baseUrl.indexOf('?');
        if (queryStart >= 0) {
            String query = baseUrl.substring(queryStart + 1);
            baseUrl = baseUrl.substring(0, queryStart);
            for (String param : query.split("&")) {
                String[] kv = param.split("=", 2);
                if (kv.length == 2 && ("authToken".equals(kv[0]) || "token".equals(kv[0]))) {
                    urlAuthToken = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                }
            }
        }

        // Strip trailing slash
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // Step 2: Resolve auth token — properties first, then URL param
        String authToken = null;
        if (info != null) {
            for (String key : new String[]{"password", "Password", "authToken", "auth_token", "token"}) {
                String val = info.getProperty(key);
                if (val != null && !val.isEmpty()) {
                    authToken = val;
                    break;
                }
            }
        }
        if (authToken == null && urlAuthToken != null && !urlAuthToken.isEmpty()) {
            authToken = urlAuthToken;
        }

        return new LibSqlConnection(baseUrl, authToken);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false;
        }
        return url.startsWith(URL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        DriverPropertyInfo tokenProp = new DriverPropertyInfo("authToken", null);
        tokenProp.description = "Authentication token for libSQL/Turso";
        tokenProp.required = false;

        DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", null);
        passwordProp.description = "Authentication token (alias for authToken)";
        passwordProp.required = false;

        return new DriverPropertyInfo[]{tokenProp, passwordProp};
    }

    @Override
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("java.util.logging is not used by LibSqlDriver");
    }
}
