package com.emtech.v2.ISO8583Finacle;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
  private static final Map<String, HikariDataSource> dataSourceMap = new HashMap<>();
  private static final Props pr = new Props();

  static {
    try {
      initializeDataSource("S");
    } catch (ClassNotFoundException e) {
      System.err.println(e.getLocalizedMessage());
    }
  }
  private static void initializeDataSource(String dbtype) throws ClassNotFoundException {
    HikariConfig config = new HikariConfig();
    String key = "VcbKey0123456789"; // 128-bit key
    String initVector = "VcbInitVector012"; // 16 bytes IV
    try {

      Class.forName(pr.getDBProperty().getProperty("database.driver"));// Explicitly load the MySQL driver

      String host, uname, port, pass, sid;

      if (dbtype.equalsIgnoreCase("S")) {
        host = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.host"));
        uname = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.user"));
        port = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.port"));
        pass = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.pass"));
        sid = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.sid"));
      } else {
        host = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.hostproxy"));
        uname = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.userproxy"));
        port = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.portproxy"));
        pass = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.passproxy"));
        sid = Encryptor.decrypt(key, initVector, pr.getDBProperty().getProperty("database.sidproxy"));
      }

      String url = pr.getDBProperty().getProperty("database.url") + host + ":" + port + "/" + sid;
      config.setJdbcUrl(url);
      config.setUsername(uname);
      config.setPassword(pass);
      config.setMaximumPoolSize(20); // Adjust pool size as needed
      config.setConnectionTimeout(30000); // 30 seconds

      // Optional HikariCP configurations for performance optimization
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

      // Create and add the data source to the map
      HikariDataSource dataSource = new HikariDataSource(config);
      dataSourceMap.put(dbtype, dataSource);

    } catch (Exception e) {
      System.err.println(e.getLocalizedMessage());;
    }
  }
  public static Connection getDbConnection(String dbtype) throws SQLException {
    HikariDataSource dataSource = dataSourceMap.get(dbtype);
    if (dataSource == null) {
      throw new SQLException("No data source found for dbtype: " + dbtype);
    }
    return dataSource.getConnection();
  }

  public static void closeConn(Connection con) {
    try {
      if (con != null) {
        con.close();
      }
    } catch (SQLException ex) {
      System.out.println("Error closing connection: " + ex.getMessage());
    }
  }
}
