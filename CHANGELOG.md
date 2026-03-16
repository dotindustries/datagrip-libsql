# Changelog

## 0.17.0

First functional release of the libSQL / Turso JDBC driver plugin for DataGrip.

### Features
- Native JDBC driver connecting to libSQL and Turso databases via the HTTP pipeline API
- Full schema introspection: tables, columns, primary keys, indexes, foreign keys appear in the database tree
- Proper query lifecycle with clean `ResultSet.close()` (no hanging spinners)
- Auto-registered in DataGrip's driver list via `driversConfig` extension point
- URL templates for Turso Cloud (`jdbc:libsql:https://{host}`) and local (`jdbc:libsql:{host}:{port}`)
- Auth token passed securely via DataGrip's Password field, URL-decoded for JWT compatibility
- Single fat JAR with Gson shaded in for DataGrip's isolated driver classloader
- Turso brand icon (aqua logomark)
- DBMS reported as "libSQL" with SQLite dialect for SQL completion
- Comprehensive error messages for auth failures (401/403) surfaced to the UI
- Debug logging to `$TMPDIR/libsql-driver.log`

### Supported DataGrip versions
- 2024.3+ (build 243+)

## 0.1.0

Initial skeleton with JDBC driver classes. Not functional as a DataGrip plugin.
