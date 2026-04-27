package com.emtech.v2.utilities;

import com.emtech.v2.utilities.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMethods {
  public static int DB(String sql, int params, String args) {
    DatabaseConnection dbconn = new DatabaseConnection();
    try {
      Connection conn = DatabaseConnection.dbConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      if (params > 0) {
        String[] vals = args.split("\\s*,\\s*");
        int l = vals.length;
        int g = 0;
        while (g < params) {
          ps.setString(g + 1, vals[g]);
          g++;
        } 
      } 
      int n = ps.executeUpdate();
      if (n > 0) {
        conn.setAutoCommit(false);
        conn.commit();
        conn.setAutoCommit(true);
        return 1;
      } 
      DatabaseConnection.closeConn(conn);
    } catch (SQLException ex) {
      System.out.println(ex.getLocalizedMessage());
    } 
    return 0;
  }
  
  public static boolean findDuplicates(String sql, int params, String args) {
    DatabaseConnection dbconn = new DatabaseConnection();
    try {
      Connection conn = DatabaseConnection.dbConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      if (params > 0) {
        String[] vals = args.split("\\s*,\\s*");
        int g = 0;
        while (g < params) {
          ps.setString(g + 1, vals[g]);
          g++;
        } 
      } 
      ResultSet r = ps.executeQuery();
      if (r.next())
        return true; 
      DatabaseConnection.closeConn(conn);
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
      return false;
    } 
    return false;
  }
  
  public static String selectValues(String sql, int w, int params, String args) {
    String k = "";
    DatabaseConnection dbconn = new DatabaseConnection();
    try {
      Connection conn = DatabaseConnection.dbConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      if (params > 0) {
        String[] values = args.split("\\s*,\\s*");
        int g = 0;
        while (g < params) {
          ps.setString(g + 1, values[g]);
          g++;
        } 
      } 
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        if (w == 1) {
          k = rs.getString(1);
          continue;
        } 
        String b = rs.getString(1);
        for (int q = 2; q <= w; ) {
          b = b + "," + b;
          q++;
        } 
        k = b;
      } 
      DatabaseConnection.closeConn(conn);
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
      return "";
    } 
    return k;
  }
}
