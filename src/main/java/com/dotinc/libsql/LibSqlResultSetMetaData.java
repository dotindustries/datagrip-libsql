package com.dotinc.libsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class LibSqlResultSetMetaData implements ResultSetMetaData {

    private final JsonArray cols;

    public LibSqlResultSetMetaData(JsonArray cols) {
        this.cols = cols;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return cols.size();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        checkColumn(column);
        JsonObject col = cols.get(column - 1).getAsJsonObject();
        return col.get("name").getAsString();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getColumnName(column);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        checkColumn(column);
        JsonObject col = cols.get(column - 1).getAsJsonObject();
        JsonElement decltype = col.get("decltype");
        if (decltype == null || decltype.isJsonNull() || decltype.getAsString().isEmpty()) {
            return "TEXT";
        }
        return decltype.getAsString().toUpperCase();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        String typeName = getColumnTypeName(column).toUpperCase();
        return mapDeclTypeToSqlType(typeName);
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        int sqlType = getColumnType(column);
        switch (sqlType) {
            case Types.INTEGER:
                return Long.class.getName();
            case Types.DOUBLE:
                return Double.class.getName();
            case Types.BLOB:
                return byte[].class.getName();
            case Types.BOOLEAN:
                return Boolean.class.getName();
            default:
                return String.class.getName();
        }
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return columnNullable;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 255;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return "";
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return "main";
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return "";
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        int sqlType = getColumnType(column);
        return sqlType == Types.INTEGER || sqlType == Types.DOUBLE;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        int sqlType = getColumnType(column);
        return sqlType == Types.VARCHAR;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(getClass())) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap to " + iface.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(getClass());
    }

    private void checkColumn(int column) throws SQLException {
        if (column < 1 || column > cols.size()) {
            throw new SQLException("Column index out of range: " + column + ", columns: " + cols.size());
        }
    }

    static int mapDeclTypeToSqlType(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            return Types.VARCHAR;
        }
        switch (typeName) {
            case "INTEGER":
            case "INT":
            case "TINYINT":
            case "SMALLINT":
            case "MEDIUMINT":
            case "BIGINT":
            case "UNSIGNED BIG INT":
            case "INT2":
            case "INT8":
                return Types.INTEGER;
            case "REAL":
            case "FLOAT":
            case "DOUBLE":
            case "DOUBLE PRECISION":
            case "NUMERIC":
            case "DECIMAL":
                return Types.DOUBLE;
            case "BLOB":
                return Types.BLOB;
            case "BOOLEAN":
                return Types.BOOLEAN;
            case "TEXT":
            case "VARCHAR":
            case "CHAR":
            case "CHARACTER":
            case "VARYING CHARACTER":
            case "NCHAR":
            case "NATIVE CHARACTER":
            case "NVARCHAR":
            case "CLOB":
            default:
                return Types.VARCHAR;
        }
    }
}
