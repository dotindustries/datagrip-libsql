package com.dotinc.libsql;

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
    private static final int MINOR_VERSION = 1;

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

        // Strip trailing slash if present
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String authToken = null;
        if (info != null) {
            authToken = info.getProperty("password");
            if (authToken == null) {
                authToken = info.getProperty("authToken");
            }
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
