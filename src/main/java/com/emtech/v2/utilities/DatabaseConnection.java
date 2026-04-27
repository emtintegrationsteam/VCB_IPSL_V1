package com.emtech.v2.utilities;

import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.Encryptor;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
  static Configurations cn = new Configurations();
  
  private static String key = cn.getProperties().getProperty("enc.key");
  
  private static String initVector = cn.getProperties().getProperty("enc.initVector");
  
  public static Connection dbConnection() {
    Connection conn = null;
    try {
      String c_lass = Encryptor.decrypt(key, initVector, cn.getProperties().getProperty("db.class")).trim();
      Class.forName(c_lass);
      String serverName = Encryptor.decrypt(key, initVector, cn.getProperties().getProperty("db.ip").trim());
      String portNumber = Encryptor.decrypt(key, initVector, cn.getProperties().getProperty("db.port").trim());
      String sid = Encryptor.decrypt(key, initVector, cn.getProperties().getProperty("db.database").trim());
      String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
      String username = Encryptor.decrypt(key, initVector, cn.getProperties().getProperty("db.username").trim());
      String password = Encryptor.decrypt(key, initVector, cn.getProperties().getProperty("db.password").trim());
      conn = DriverManager.getConnection(url, username, password);
    } catch (ClassNotFoundException|java.sql.SQLException e) {
      e.printStackTrace();
    } 
    if (conn != null)
      return conn; 
    return null;
  }
  
  public static void closeConn(Connection con) {
    try {
      if (con != null)
        con.close(); 
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    } 
  }
}
