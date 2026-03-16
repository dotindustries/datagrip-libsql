package com.dotinc.libsql;

import java.net.URI;
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
    private static final int MAJOR_VERSION = 0;
    private static final int MINOR_VERSION = 17;

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger("com.dotinc.libsql");

    static {
        try {
            // Set up file logging early so connect() calls are captured
            java.util.logging.FileHandler fh = new java.util.logging.FileHandler(
                System.getProperty("java.io.tmpdir") + "/libsql-driver.log", true);
            fh.setFormatter(new java.util.logging.SimpleFormatter());
            LOG.addHandler(fh);
            LOG.setLevel(java.util.logging.Level.ALL);
        } catch (Exception e) {
            // ignore
        }
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

        // Strip trailing slash if present
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        LOG.info("connect called with url=" + url);
        LOG.info("connect properties: " + (info != null ? info.toString() : "null"));

        String authToken = null;
        if (info != null) {
            // Try all common property names DataGrip might use
            for (String key : new String[]{"password", "Password", "authToken", "auth_token", "token"}) {
                authToken = info.getProperty(key);
                if (authToken != null && !authToken.isEmpty()) {
                    LOG.info("authToken found in property: " + key + " (" + authToken.length() + " chars)");
                    break;
                }
                authToken = null;
            }
        }

        // Fallback: extract authToken from URL query parameter ?authToken=xxx
        if (authToken == null) {
            try {
                URI uri = URI.create(baseUrl);
                String query = uri.getQuery();
                if (query != null) {
                    for (String param : query.split("&")) {
                        String[] kv = param.split("=", 2);
                        if (kv.length == 2 && ("authToken".equals(kv[0]) || "token".equals(kv[0]))) {
                            authToken = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                            // Strip query from baseUrl
                            baseUrl = baseUrl.substring(0, baseUrl.indexOf('?'));
                            LOG.info("authToken found in URL query param (" + authToken.length() + " chars)");
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                // URL parsing failed, ignore
            }
        }

        if (authToken == null) {
            LOG.warning("No auth token found in properties or URL. Connection will likely fail with 401.");
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
