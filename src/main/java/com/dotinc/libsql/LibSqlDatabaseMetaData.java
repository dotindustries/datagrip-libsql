package com.dotinc.libsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibSqlDatabaseMetaData implements DatabaseMetaData {

    private final LibSqlConnection connection;

    public LibSqlDatabaseMetaData(LibSqlConnection connection) {
        this.connection = connection;
    }

    // ---- Identification ----

    @Override
    public String getDatabaseProductName() throws SQLException {
        return "SQLite";
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return "3.45.0";
    }

    @Override
    public String getDriverName() throws SQLException {
        return "libSQL JDBC Driver";
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return "0.1.0";
    }

    @Override
    public int getDriverMajorVersion() {
        return 0;
    }

    @Override
    public int getDriverMinorVersion() {
        return 1;
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return 3;
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return 45;
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 4;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 2;
    }

    @Override
    public String getURL() throws SQLException {
        return connection.getBaseUrl();
    }

    @Override
    public String getUserName() throws SQLException {
        return "";
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    // ---- SQL syntax / identifiers ----

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        return "";
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return "";
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return "schema";
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return "database";
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return "procedure";
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return "abs,max,min,round";
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return "length,lower,upper,substr,trim,replace";
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return "typeof,last_insert_rowid,changes,total_changes";
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return "date,time,datetime,julianday,strftime";
    }

    // ---- Limits ----

    @Override
    public int getMaxConnections() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_SERIALIZABLE;
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return sqlStateSQL;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    // ---- Feature support flags ----

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return level == Connection.TRANSACTION_SERIALIZABLE;
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        return type == ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        return type == ResultSet.TYPE_FORWARD_ONLY && concurrency == ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return holdability == ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return true;
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return true;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return false;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return false;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        return false;
    }

    // ---- Schema introspection (CRITICAL for DataGrip) ----

    @Override
    public ResultSet getSchemas() throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_CATALOG", "TEXT"}
        );
        List<List<Object>> rows = new ArrayList<>();
        List<Object> row = new ArrayList<>();
        row.add("main");
        row.add(null);
        rows.add(row);
        return buildResultSet(colDefs, rows);
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        if (schemaPattern != null && !schemaPattern.equals("%") && !schemaPattern.equalsIgnoreCase("main")) {
            List<String[]> colDefs = Arrays.asList(
                    new String[]{"TABLE_SCHEM", "TEXT"},
                    new String[]{"TABLE_CATALOG", "TEXT"}
            );
            return buildResultSet(colDefs, new ArrayList<>());
        }
        return getSchemas();
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        List<String[]> colDefs = List.<String[]>of(
                new String[]{"TABLE_CAT", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        List<String[]> colDefs = List.<String[]>of(
                new String[]{"TABLE_TYPE", "TEXT"}
        );
        List<List<Object>> rows = new ArrayList<>();
        List<Object> tableRow = new ArrayList<>();
        tableRow.add("TABLE");
        rows.add(tableRow);
        List<Object> viewRow = new ArrayList<>();
        viewRow.add("VIEW");
        rows.add(viewRow);
        return buildResultSet(colDefs, rows);
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_CAT", "TEXT"},
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_NAME", "TEXT"},
                new String[]{"TABLE_TYPE", "TEXT"},
                new String[]{"REMARKS", "TEXT"}
        );
        List<List<Object>> resultRows = new ArrayList<>();

        Statement stmt = connection.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(
                    "SELECT name, type FROM sqlite_master WHERE type IN ('table','view') ORDER BY name"
            );
            try {
                while (rs.next()) {
                    String name = rs.getString(1);
                    String sqliteType = rs.getString(2);

                    if (tableNamePattern != null && !tableNamePattern.equals("%")) {
                        if (!matchesPattern(name, tableNamePattern)) {
                            continue;
                        }
                    }

                    String tableType = "table".equalsIgnoreCase(sqliteType) ? "TABLE" : "VIEW";

                    if (types != null) {
                        boolean found = false;
                        for (String t : types) {
                            if (t.equalsIgnoreCase(tableType)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            continue;
                        }
                    }

                    List<Object> row = new ArrayList<>();
                    row.add(null);       // TABLE_CAT
                    row.add("main");     // TABLE_SCHEM
                    row.add(name);       // TABLE_NAME
                    row.add(tableType);  // TABLE_TYPE
                    row.add(null);       // REMARKS
                    resultRows.add(row);
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

        return buildResultSet(colDefs, resultRows);
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_CAT", "TEXT"},
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_NAME", "TEXT"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"COLUMN_SIZE", "INTEGER"},
                new String[]{"BUFFER_LENGTH", "INTEGER"},
                new String[]{"DECIMAL_DIGITS", "INTEGER"},
                new String[]{"NUM_PREC_RADIX", "INTEGER"},
                new String[]{"NULLABLE", "INTEGER"},
                new String[]{"REMARKS", "TEXT"},
                new String[]{"COLUMN_DEF", "TEXT"},
                new String[]{"SQL_DATA_TYPE", "INTEGER"},
                new String[]{"SQL_DATETIME_SUB", "INTEGER"},
                new String[]{"CHAR_OCTET_LENGTH", "INTEGER"},
                new String[]{"ORDINAL_POSITION", "INTEGER"},
                new String[]{"IS_NULLABLE", "TEXT"},
                new String[]{"SCOPE_CATALOG", "TEXT"},
                new String[]{"SCOPE_SCHEMA", "TEXT"},
                new String[]{"SCOPE_TABLE", "TEXT"},
                new String[]{"SOURCE_DATA_TYPE", "INTEGER"},
                new String[]{"IS_AUTOINCREMENT", "TEXT"},
                new String[]{"IS_GENERATEDCOLUMN", "TEXT"}
        );
        List<List<Object>> resultRows = new ArrayList<>();

        List<String> tableNames = getTableNames(tableNamePattern);

        for (String tableName : tableNames) {
            Statement stmt = connection.createStatement();
            try {
                ResultSet rs = stmt.executeQuery("PRAGMA table_info(\"" + tableName.replace("\"", "\"\"") + "\")");
                try {
                    while (rs.next()) {
                        String colName = rs.getString("name");
                        String colType = rs.getString("type");
                        int notNull = rs.getInt("notnull");
                        String dfltValue = rs.getString("dflt_value");
                        int pk = rs.getInt("pk");
                        int cid = rs.getInt("cid");

                        if (columnNamePattern != null && !columnNamePattern.equals("%")) {
                            if (!matchesPattern(colName, columnNamePattern)) {
                                continue;
                            }
                        }

                        String typeName = colType != null && !colType.isEmpty() ? colType.toUpperCase() : "TEXT";
                        int sqlType = LibSqlResultSetMetaData.mapDeclTypeToSqlType(typeName);

                        List<Object> row = new ArrayList<>();
                        row.add(null);                                    // TABLE_CAT
                        row.add("main");                                  // TABLE_SCHEM
                        row.add(tableName);                               // TABLE_NAME
                        row.add(colName);                                 // COLUMN_NAME
                        row.add(sqlType);                                 // DATA_TYPE
                        row.add(typeName);                                // TYPE_NAME
                        row.add(getColumnSize(typeName));                 // COLUMN_SIZE
                        row.add(null);                                    // BUFFER_LENGTH
                        row.add(getDecimalDigits(typeName));              // DECIMAL_DIGITS
                        row.add(10);                                      // NUM_PREC_RADIX
                        row.add(notNull == 0 ? columnNullable : columnNoNulls); // NULLABLE
                        row.add(null);                                    // REMARKS
                        row.add(dfltValue);                               // COLUMN_DEF
                        row.add(null);                                    // SQL_DATA_TYPE
                        row.add(null);                                    // SQL_DATETIME_SUB
                        row.add(255);                                     // CHAR_OCTET_LENGTH
                        row.add(cid + 1);                                 // ORDINAL_POSITION
                        row.add(notNull == 0 ? "YES" : "NO");            // IS_NULLABLE
                        row.add(null);                                    // SCOPE_CATALOG
                        row.add(null);                                    // SCOPE_SCHEMA
                        row.add(null);                                    // SCOPE_TABLE
                        row.add(null);                                    // SOURCE_DATA_TYPE
                        row.add(pk > 0 && "INTEGER".equalsIgnoreCase(typeName) ? "YES" : "NO"); // IS_AUTOINCREMENT
                        row.add("NO");                                    // IS_GENERATEDCOLUMN
                        resultRows.add(row);
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        }

        return buildResultSet(colDefs, resultRows);
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_CAT", "TEXT"},
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_NAME", "TEXT"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"KEY_SEQ", "INTEGER"},
                new String[]{"PK_NAME", "TEXT"}
        );
        List<List<Object>> resultRows = new ArrayList<>();

        if (table == null) {
            return buildResultSet(colDefs, resultRows);
        }

        Statement stmt = connection.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(\"" + table.replace("\"", "\"\"") + "\")");
            try {
                while (rs.next()) {
                    int pk = rs.getInt("pk");
                    if (pk > 0) {
                        List<Object> row = new ArrayList<>();
                        row.add(null);                  // TABLE_CAT
                        row.add("main");                // TABLE_SCHEM
                        row.add(table);                 // TABLE_NAME
                        row.add(rs.getString("name"));  // COLUMN_NAME
                        row.add(pk);                    // KEY_SEQ
                        row.add(null);                  // PK_NAME
                        resultRows.add(row);
                    }
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

        return buildResultSet(colDefs, resultRows);
    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_CAT", "TEXT"},
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_NAME", "TEXT"},
                new String[]{"NON_UNIQUE", "INTEGER"},
                new String[]{"INDEX_QUALIFIER", "TEXT"},
                new String[]{"INDEX_NAME", "TEXT"},
                new String[]{"TYPE", "INTEGER"},
                new String[]{"ORDINAL_POSITION", "INTEGER"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"ASC_OR_DESC", "TEXT"},
                new String[]{"CARDINALITY", "INTEGER"},
                new String[]{"PAGES", "INTEGER"},
                new String[]{"FILTER_CONDITION", "TEXT"}
        );
        List<List<Object>> resultRows = new ArrayList<>();

        if (table == null) {
            return buildResultSet(colDefs, resultRows);
        }

        Statement stmt = connection.createStatement();
        try {
            ResultSet indexList = stmt.executeQuery("PRAGMA index_list(\"" + table.replace("\"", "\"\"") + "\")");
            try {
                while (indexList.next()) {
                    String indexName = indexList.getString("name");
                    boolean isUnique = indexList.getInt("unique") == 1;

                    if (unique && !isUnique) {
                        continue;
                    }

                    Statement infoStmt = connection.createStatement();
                    try {
                        ResultSet indexInfo = infoStmt.executeQuery("PRAGMA index_info(\"" + indexName.replace("\"", "\"\"") + "\")");
                        try {
                            while (indexInfo.next()) {
                                List<Object> row = new ArrayList<>();
                                row.add(null);                                          // TABLE_CAT
                                row.add("main");                                        // TABLE_SCHEM
                                row.add(table);                                         // TABLE_NAME
                                row.add(isUnique ? 0 : 1);                              // NON_UNIQUE
                                row.add(null);                                          // INDEX_QUALIFIER
                                row.add(indexName);                                     // INDEX_NAME
                                row.add(tableIndexOther);                               // TYPE
                                row.add(indexInfo.getInt("seqno") + 1);                 // ORDINAL_POSITION
                                row.add(indexInfo.getString("name"));                   // COLUMN_NAME
                                row.add("A");                                           // ASC_OR_DESC
                                row.add(0);                                             // CARDINALITY
                                row.add(0);                                             // PAGES
                                row.add(null);                                          // FILTER_CONDITION
                                resultRows.add(row);
                            }
                        } finally {
                            indexInfo.close();
                        }
                    } finally {
                        infoStmt.close();
                    }
                }
            } finally {
                indexList.close();
            }
        } finally {
            stmt.close();
        }

        return buildResultSet(colDefs, resultRows);
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        return getForeignKeys(null, null, table);
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        List<String[]> colDefs = getForeignKeyColumnDefs();
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable,
                                       String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        return getForeignKeys(parentTable, null, foreignTable);
    }

    private ResultSet getForeignKeys(String parentTable, String parentSchema, String fkTable) throws SQLException {
        List<String[]> colDefs = getForeignKeyColumnDefs();
        List<List<Object>> resultRows = new ArrayList<>();

        if (fkTable == null) {
            return buildResultSet(colDefs, resultRows);
        }

        Statement stmt = connection.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_list(\"" + fkTable.replace("\"", "\"\"") + "\")");
            try {
                while (rs.next()) {
                    String refTable = rs.getString("table");

                    if (parentTable != null && !parentTable.equalsIgnoreCase(refTable)) {
                        continue;
                    }

                    List<Object> row = new ArrayList<>();
                    row.add(null);                    // PKTABLE_CAT
                    row.add("main");                  // PKTABLE_SCHEM
                    row.add(refTable);                // PKTABLE_NAME
                    row.add(rs.getString("to"));      // PKCOLUMN_NAME
                    row.add(null);                    // FKTABLE_CAT
                    row.add("main");                  // FKTABLE_SCHEM
                    row.add(fkTable);                 // FKTABLE_NAME
                    row.add(rs.getString("from"));    // FKCOLUMN_NAME
                    row.add(rs.getInt("seq") + 1);    // KEY_SEQ
                    row.add(importedKeyNoAction);     // UPDATE_RULE
                    row.add(importedKeyNoAction);     // DELETE_RULE
                    row.add(null);                    // FK_NAME
                    row.add(null);                    // PK_NAME
                    row.add(importedKeyNotDeferrable); // DEFERRABILITY
                    resultRows.add(row);
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

        return buildResultSet(colDefs, resultRows);
    }

    private List<String[]> getForeignKeyColumnDefs() {
        return Arrays.asList(
                new String[]{"PKTABLE_CAT", "TEXT"},
                new String[]{"PKTABLE_SCHEM", "TEXT"},
                new String[]{"PKTABLE_NAME", "TEXT"},
                new String[]{"PKCOLUMN_NAME", "TEXT"},
                new String[]{"FKTABLE_CAT", "TEXT"},
                new String[]{"FKTABLE_SCHEM", "TEXT"},
                new String[]{"FKTABLE_NAME", "TEXT"},
                new String[]{"FKCOLUMN_NAME", "TEXT"},
                new String[]{"KEY_SEQ", "INTEGER"},
                new String[]{"UPDATE_RULE", "INTEGER"},
                new String[]{"DELETE_RULE", "INTEGER"},
                new String[]{"FK_NAME", "TEXT"},
                new String[]{"PK_NAME", "TEXT"},
                new String[]{"DEFERRABILITY", "INTEGER"}
        );
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"PRECISION", "INTEGER"},
                new String[]{"LITERAL_PREFIX", "TEXT"},
                new String[]{"LITERAL_SUFFIX", "TEXT"},
                new String[]{"CREATE_PARAMS", "TEXT"},
                new String[]{"NULLABLE", "INTEGER"},
                new String[]{"CASE_SENSITIVE", "INTEGER"},
                new String[]{"SEARCHABLE", "INTEGER"},
                new String[]{"UNSIGNED_ATTRIBUTE", "INTEGER"},
                new String[]{"FIXED_PREC_SCALE", "INTEGER"},
                new String[]{"AUTO_INCREMENT", "INTEGER"},
                new String[]{"LOCAL_TYPE_NAME", "TEXT"},
                new String[]{"MINIMUM_SCALE", "INTEGER"},
                new String[]{"MAXIMUM_SCALE", "INTEGER"},
                new String[]{"SQL_DATA_TYPE", "INTEGER"},
                new String[]{"SQL_DATETIME_SUB", "INTEGER"},
                new String[]{"NUM_PREC_RADIX", "INTEGER"}
        );
        List<List<Object>> rows = new ArrayList<>();

        rows.add(makeTypeInfoRow("TEXT", Types.VARCHAR, 2147483647, "'", "'", true, typeSearchable));
        rows.add(makeTypeInfoRow("INTEGER", Types.INTEGER, 19, null, null, false, typeSearchable));
        rows.add(makeTypeInfoRow("REAL", Types.DOUBLE, 15, null, null, false, typeSearchable));
        rows.add(makeTypeInfoRow("BLOB", Types.BLOB, 2147483647, "X'", "'", false, typeSearchable));
        rows.add(makeTypeInfoRow("NUMERIC", Types.DOUBLE, 15, null, null, false, typeSearchable));

        return buildResultSet(colDefs, rows);
    }

    private List<Object> makeTypeInfoRow(String typeName, int dataType, int precision,
                                          String prefix, String suffix, boolean caseSensitive, int searchable) {
        List<Object> row = new ArrayList<>();
        row.add(typeName);       // TYPE_NAME
        row.add(dataType);       // DATA_TYPE
        row.add(precision);      // PRECISION
        row.add(prefix);         // LITERAL_PREFIX
        row.add(suffix);         // LITERAL_SUFFIX
        row.add(null);           // CREATE_PARAMS
        row.add(typeNullable);   // NULLABLE
        row.add(caseSensitive ? 1 : 0); // CASE_SENSITIVE
        row.add(searchable);     // SEARCHABLE
        row.add(0);              // UNSIGNED_ATTRIBUTE
        row.add(0);              // FIXED_PREC_SCALE
        row.add(0);              // AUTO_INCREMENT
        row.add(typeName);       // LOCAL_TYPE_NAME
        row.add(0);              // MINIMUM_SCALE
        row.add(0);              // MAXIMUM_SCALE
        row.add(null);           // SQL_DATA_TYPE
        row.add(null);           // SQL_DATETIME_SUB
        row.add(10);             // NUM_PREC_RADIX
        return row;
    }

    // ---- Methods that return empty ResultSets ----

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"PROCEDURE_CAT", "TEXT"},
                new String[]{"PROCEDURE_SCHEM", "TEXT"},
                new String[]{"PROCEDURE_NAME", "TEXT"},
                new String[]{"RESERVED1", "TEXT"},
                new String[]{"RESERVED2", "TEXT"},
                new String[]{"RESERVED3", "TEXT"},
                new String[]{"REMARKS", "TEXT"},
                new String[]{"PROCEDURE_TYPE", "INTEGER"},
                new String[]{"SPECIFIC_NAME", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"PROCEDURE_CAT", "TEXT"},
                new String[]{"PROCEDURE_SCHEM", "TEXT"},
                new String[]{"PROCEDURE_NAME", "TEXT"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"COLUMN_TYPE", "INTEGER"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"PRECISION", "INTEGER"},
                new String[]{"LENGTH", "INTEGER"},
                new String[]{"SCALE", "INTEGER"},
                new String[]{"RADIX", "INTEGER"},
                new String[]{"NULLABLE", "INTEGER"},
                new String[]{"REMARKS", "TEXT"},
                new String[]{"COLUMN_DEF", "TEXT"},
                new String[]{"SQL_DATA_TYPE", "INTEGER"},
                new String[]{"SQL_DATETIME_SUB", "INTEGER"},
                new String[]{"CHAR_OCTET_LENGTH", "INTEGER"},
                new String[]{"ORDINAL_POSITION", "INTEGER"},
                new String[]{"IS_NULLABLE", "TEXT"},
                new String[]{"SPECIFIC_NAME", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_CAT", "TEXT"},
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_NAME", "TEXT"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"GRANTOR", "TEXT"},
                new String[]{"GRANTEE", "TEXT"},
                new String[]{"PRIVILEGE", "TEXT"},
                new String[]{"IS_GRANTABLE", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_CAT", "TEXT"},
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_NAME", "TEXT"},
                new String[]{"GRANTOR", "TEXT"},
                new String[]{"GRANTEE", "TEXT"},
                new String[]{"PRIVILEGE", "TEXT"},
                new String[]{"IS_GRANTABLE", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"SCOPE", "INTEGER"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"COLUMN_SIZE", "INTEGER"},
                new String[]{"BUFFER_LENGTH", "INTEGER"},
                new String[]{"DECIMAL_DIGITS", "INTEGER"},
                new String[]{"PSEUDO_COLUMN", "INTEGER"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"SCOPE", "INTEGER"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"COLUMN_SIZE", "INTEGER"},
                new String[]{"BUFFER_LENGTH", "INTEGER"},
                new String[]{"DECIMAL_DIGITS", "INTEGER"},
                new String[]{"PSEUDO_COLUMN", "INTEGER"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TYPE_CAT", "TEXT"},
                new String[]{"TYPE_SCHEM", "TEXT"},
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"CLASS_NAME", "TEXT"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"REMARKS", "TEXT"},
                new String[]{"BASE_TYPE", "INTEGER"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TYPE_CAT", "TEXT"},
                new String[]{"TYPE_SCHEM", "TEXT"},
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"SUPERTYPE_CAT", "TEXT"},
                new String[]{"SUPERTYPE_SCHEM", "TEXT"},
                new String[]{"SUPERTYPE_NAME", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_CAT", "TEXT"},
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_NAME", "TEXT"},
                new String[]{"SUPERTABLE_NAME", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TYPE_CAT", "TEXT"},
                new String[]{"TYPE_SCHEM", "TEXT"},
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"ATTR_NAME", "TEXT"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"ATTR_TYPE_NAME", "TEXT"},
                new String[]{"ATTR_SIZE", "INTEGER"},
                new String[]{"DECIMAL_DIGITS", "INTEGER"},
                new String[]{"NUM_PREC_RADIX", "INTEGER"},
                new String[]{"NULLABLE", "INTEGER"},
                new String[]{"REMARKS", "TEXT"},
                new String[]{"ATTR_DEF", "TEXT"},
                new String[]{"SQL_DATA_TYPE", "INTEGER"},
                new String[]{"SQL_DATETIME_SUB", "INTEGER"},
                new String[]{"CHAR_OCTET_LENGTH", "INTEGER"},
                new String[]{"ORDINAL_POSITION", "INTEGER"},
                new String[]{"IS_NULLABLE", "TEXT"},
                new String[]{"SCOPE_CATALOG", "TEXT"},
                new String[]{"SCOPE_SCHEMA", "TEXT"},
                new String[]{"SCOPE_TABLE", "TEXT"},
                new String[]{"SOURCE_DATA_TYPE", "INTEGER"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"NAME", "TEXT"},
                new String[]{"MAX_LEN", "INTEGER"},
                new String[]{"DEFAULT_VALUE", "TEXT"},
                new String[]{"DESCRIPTION", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"FUNCTION_CAT", "TEXT"},
                new String[]{"FUNCTION_SCHEM", "TEXT"},
                new String[]{"FUNCTION_NAME", "TEXT"},
                new String[]{"REMARKS", "TEXT"},
                new String[]{"FUNCTION_TYPE", "INTEGER"},
                new String[]{"SPECIFIC_NAME", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"FUNCTION_CAT", "TEXT"},
                new String[]{"FUNCTION_SCHEM", "TEXT"},
                new String[]{"FUNCTION_NAME", "TEXT"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"COLUMN_TYPE", "INTEGER"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"TYPE_NAME", "TEXT"},
                new String[]{"PRECISION", "INTEGER"},
                new String[]{"LENGTH", "INTEGER"},
                new String[]{"SCALE", "INTEGER"},
                new String[]{"RADIX", "INTEGER"},
                new String[]{"NULLABLE", "INTEGER"},
                new String[]{"REMARKS", "TEXT"},
                new String[]{"CHAR_OCTET_LENGTH", "INTEGER"},
                new String[]{"ORDINAL_POSITION", "INTEGER"},
                new String[]{"IS_NULLABLE", "TEXT"},
                new String[]{"SPECIFIC_NAME", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        List<String[]> colDefs = Arrays.asList(
                new String[]{"TABLE_CAT", "TEXT"},
                new String[]{"TABLE_SCHEM", "TEXT"},
                new String[]{"TABLE_NAME", "TEXT"},
                new String[]{"COLUMN_NAME", "TEXT"},
                new String[]{"DATA_TYPE", "INTEGER"},
                new String[]{"COLUMN_SIZE", "INTEGER"},
                new String[]{"DECIMAL_DIGITS", "INTEGER"},
                new String[]{"NUM_PREC_RADIX", "INTEGER"},
                new String[]{"COLUMN_USAGE", "TEXT"},
                new String[]{"REMARKS", "TEXT"},
                new String[]{"CHAR_OCTET_LENGTH", "INTEGER"},
                new String[]{"IS_NULLABLE", "TEXT"}
        );
        return buildResultSet(colDefs, new ArrayList<>());
    }

    // ---- Connection ----

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    // ---- Wrapper ----

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

    // ---- Helper: build ResultSet from in-memory data ----

    private LibSqlResultSet buildResultSet(List<String[]> colDefs, List<List<Object>> dataRows) {
        JsonObject result = new JsonObject();

        JsonArray cols = new JsonArray();
        for (String[] colDef : colDefs) {
            JsonObject col = new JsonObject();
            col.addProperty("name", colDef[0]);
            col.addProperty("decltype", colDef[1]);
            cols.add(col);
        }
        result.add("cols", cols);

        JsonArray rows = new JsonArray();
        for (List<Object> dataRow : dataRows) {
            JsonArray row = new JsonArray();
            for (Object value : dataRow) {
                JsonObject cell = new JsonObject();
                if (value == null) {
                    cell.addProperty("type", "null");
                } else if (value instanceof Integer) {
                    cell.addProperty("type", "integer");
                    cell.addProperty("value", String.valueOf(value));
                } else if (value instanceof Long) {
                    cell.addProperty("type", "integer");
                    cell.addProperty("value", String.valueOf(value));
                } else if (value instanceof Double) {
                    cell.addProperty("type", "float");
                    cell.addProperty("value", String.valueOf(value));
                } else if (value instanceof Float) {
                    cell.addProperty("type", "float");
                    cell.addProperty("value", String.valueOf(value));
                } else {
                    cell.addProperty("type", "text");
                    cell.addProperty("value", value.toString());
                }
                row.add(cell);
            }
            rows.add(row);
        }
        result.add("rows", rows);
        result.addProperty("affected_row_count", 0);
        result.add("last_insert_rowid", JsonNull.INSTANCE);

        return new LibSqlResultSet(result);
    }

    // ---- Internal helpers ----

    private List<String> getTableNames(String tableNamePattern) throws SQLException {
        List<String> names = new ArrayList<>();
        Statement stmt = connection.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type IN ('table','view') ORDER BY name"
            );
            try {
                while (rs.next()) {
                    String name = rs.getString(1);
                    if (tableNamePattern == null || tableNamePattern.equals("%") || matchesPattern(name, tableNamePattern)) {
                        names.add(name);
                    }
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
        return names;
    }

    private static boolean matchesPattern(String value, String pattern) {
        if (pattern == null || pattern.equals("%")) {
            return true;
        }
        String regex = pattern
                .replace("_", ".")
                .replace("%", ".*");
        return value.matches("(?i)" + regex);
    }

    private static int getColumnSize(String typeName) {
        if (typeName == null) {
            return 255;
        }
        switch (typeName.toUpperCase()) {
            case "INTEGER":
            case "INT":
            case "BIGINT":
                return 19;
            case "REAL":
            case "FLOAT":
            case "DOUBLE":
            case "DOUBLE PRECISION":
                return 15;
            case "BOOLEAN":
                return 1;
            default:
                return 255;
        }
    }

    private static int getDecimalDigits(String typeName) {
        if (typeName == null) {
            return 0;
        }
        switch (typeName.toUpperCase()) {
            case "REAL":
            case "FLOAT":
            case "DOUBLE":
            case "DOUBLE PRECISION":
                return 15;
            default:
                return 0;
        }
    }
}
