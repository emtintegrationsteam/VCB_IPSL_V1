package com.emtech.v2.ISO8583Finacle;

import com.emtech.v2.ISO8583Finacle.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ru.cwt.mgw.lookupdb.ws.LookupDbFault_Exception;
import ru.cwt.mgw.lookupdb.ws.LookupDbWS;
import ru.cwt.mgw.lookupdb.ws.LookupDbWSService;
import ru.cwt.mgw.lookupdb.ws.OnlineCustomerBankListRequest;
import ru.cwt.mgw.lookupdb.ws.OnlineCustomerBankListResult;

public class DbMethods {
  public static int dbWork(String sql, int params, String args) {
    try {
      Connection conn = DBConnection.getDbConnection("S");
      try {
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
          if (params > 0) {
            String[] vals = args.split("\\s*,\\s*");
            for (int g = 0; g < params; g++)
              ps.setString(g + 1, vals[g]); 
          } 
          int n = ps.executeUpdate();
          if (n > 0) {
            conn.setAutoCommit(false);
            conn.commit();
            conn.setAutoCommit(true);
            boolean bool = true;
            if (ps != null)
              ps.close(); 
            if (conn != null)
              conn.close(); 

          } 
          if (ps != null)
            ps.close(); 
        } catch (Throwable throwable) {
          if (ps != null)
            try {
              ps.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
        if (conn != null)
          conn.close(); 
      } catch (Throwable throwable) {
        if (conn != null)
          try {
            conn.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (SQLException ex) {
      System.out.println("Error" + ex.getMessage());
    } 
    return 0;
  }
  
  public static boolean findDuplicate(String sql, int params, String args) {
    try {
      Connection conn = DBConnection.getDbConnection("S");
      try {
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
          if (params > 0) {
            String[] vals = args.split("\\s*,\\s*");
            int g = 0;
            while (g < params) {
              ps.setString(g + 1, vals[g]);
              g++;
            } 
          } 
          ResultSet r = ps.executeQuery();
          if (r.next()) {
            boolean bool = true;
            if (ps != null)
              ps.close(); 
            if (conn != null)
              conn.close(); 
            return bool;
          } 
          DBConnection.closeConn(conn);
          if (ps != null)
            ps.close(); 
        } catch (Throwable throwable) {
          if (ps != null)
            try {
              ps.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
        if (conn != null)
          conn.close(); 
      } catch (Throwable throwable) {
        if (conn != null)
          try {
            conn.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
      return false;
    } 
    return false;
  }
  
  public static String getValue(String sql, int w, int params, String args) {
    String k = "";
    try {
      Connection conn = DBConnection.getDbConnection("S");
      try {
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
          if (params > 0) {
            String[] vals = args.split("\\s*,\\s*");
            int g = 0;
            while (g < params) {
              ps.setString(g + 1, vals[g]);
              g++;
            } 
          } 
          ResultSet r = ps.executeQuery();
          while (r.next()) {
            if (w == 1) {
              k = r.getString(1);
              continue;
            } 
            String b = r.getString(1);
            for (int q = 2; q <= w; ) {
              b = b + "," + b;
              q++;
            } 
            k = b;
          } 
          DBConnection.closeConn(conn);
          if (ps != null)
            ps.close(); 
        } catch (Throwable throwable) {
          if (ps != null)
            try {
              ps.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
        if (conn != null)
          conn.close(); 
      } catch (Throwable throwable) {
        if (conn != null)
          try {
            conn.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
      return "";
    } 
    return k;
  }
  
  public static List bankList() {
    String sql = "select bank_name, sortCode from kba_bank_list";
    DBConnection dbconn = new DBConnection();
    List<String> arr = new ArrayList();
    try {
      Connection conn = DBConnection.getDbConnection("S");
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet r = ps.executeQuery();
      while (r.next())
        arr.add(r.getString(1).trim() + " : " + r.getString(1).trim()); 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    } 
    return arr;
  }
  
  public static List branchCodes() {
    String sql = "select branch_code, branch_desc from branch_codes";
    DBConnection dbconn = new DBConnection();
    List<String> arr = new ArrayList();
    try {
      Connection conn = DBConnection.getDbConnection("S");
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet r = ps.executeQuery();
      while (r.next())
        arr.add(r.getString(1).trim() + " : " + r.getString(1).trim()); 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    } 
    return arr;
  }
  
  public static List unverifiedTransactions(String uname) {
    String sql = "select sno, amount,sender_acnt,recipient_acnt,processing_code, sortcode from unverified_sent_messages where rcre_user_id <> ?";
    DBConnection dbconn = new DBConnection();
    List<String> arr = new ArrayList();
    try {
      Connection conn = DBConnection.getDbConnection("S");
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, uname);
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        String in = r.getString(1) + "," + r.getString(1) + "," + r.getDouble(2) + "," + r.getString(3) + "," + r.getString(4) + "," + r.getString(5);
        arr.add(in);
      } 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    } 
    return arr;
  }
  
  public static List unverifiedRegistration(String uname) {
    String sql = "select lookup_id, account_no,card_no,phone_no,card_expry_date,account_name,document_type,document_number,rcre_time from unverified_registration where rcre_user <> ?";
    DBConnection dbconn = new DBConnection();
    List<String> arr = new ArrayList();
    try {
      Connection conn = DBConnection.getDbConnection("S");
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, uname);
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        String in = r.getString(1) + "," + r.getString(1) + "," + r.getString(2) + "," + r.getString(3) + "," + r.getString(4) + "," + r.getString(5) + "," + r.getString(6) + "," + r.getString(7);
        arr.add(in);
      } 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    } 
    return arr;
  }
  
  public static List parametersList() {
    String sql = "select param from seed_params";
    DBConnection dbconn = new DBConnection();
    List<String> arr = new ArrayList();
    try {
      Connection conn = DBConnection.getDbConnection("S");
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet r = ps.executeQuery();
      while (r.next())
        arr.add(r.getString(1)); 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    } 
    return arr;
  }
  
  public static OnlineCustomerBankListResult.BankList bankCustListService(String phoneNo) {
    try {
      String uname = getParamValue("KITS.REG.USERNAME");
      String password = getParamValue("KITS.REG.PASSWD");
      OnlineCustomerBankListRequest req1 = new OnlineCustomerBankListRequest();
      req1.setLogin(uname);
      req1.setMsisdn(phoneNo);
      req1.setPassword(password);
      OnlineCustomerBankListResult y = getCustomerBankList(req1);
      System.out.println(y);
      return y.getBankList();
    } catch (LookupDbFault_Exception ex) {
      System.out.println(ex.getMessage());
      return null;
    } 
  }
  
  private static OnlineCustomerBankListResult getCustomerBankList(OnlineCustomerBankListRequest req) throws LookupDbFault_Exception {
    LookupDbWSService service = new LookupDbWSService();
    LookupDbWS port = service.getLookupDbWSPort();
    return port.getCustomerBankList(req);
  }
  
  public static String getParamValue(String serialNumber) {
    String sql = "select value from seed_params where param = ?";
    return getValue(sql, 1, 1, serialNumber);
  }
  
  public static List chargeList() {
    String sql = "select sno,low_amt,high_amt,amount from charges_configuration";
    DBConnection dbconn = new DBConnection();
    List<ArrayList<String>> arr = new ArrayList();
    try {
      Connection conn = DBConnection.getDbConnection("S");
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        ArrayList<String> one = new ArrayList();
        one.add(r.getString(1).trim());
        one.add(r.getString(2).trim());
        one.add(r.getString(3).trim());
        one.add(r.getString(4).trim());
        arr.add(one);
      } 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    } 
    return arr;
  }
  
  public static List unverifiedUsers(String uname) {
    String sql = "select user_name,rcre_time,role_Id,rcre_user,Status from users_table where Status <> 'Y' and rcre_user <> ?";
    DBConnection dbconn = new DBConnection();
    String status = "Y";
    List<String> arr = new ArrayList();
    try {
      Connection conn = DBConnection.getDbConnection("S");
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, uname);
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        String in = r.getString(1) + "," + r.getString(1) + "," + r.getString(2) + "," + r.getString(3) + "," + r.getString(4);
        arr.add(in);
      } 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    } 
    return arr;
  }
  
  public static List unverifiedSeedParams(String uname) {
    String sql = "select param,value,lchg_user,lchg_date from seed_params_mod where Verify = 'M' and Status='M'";
    DBConnection dbconn = new DBConnection();
    String status = "Y";
    List<String> arr = new ArrayList();
    try {
      Connection conn = DBConnection.getDbConnection("S");
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet r = ps.executeQuery();
      while (r.next()) {
        String in = r.getString(1) + "," + r.getString(1) + "," + r.getString(2) + "," + r.getString(3);
        arr.add(in);
      } 
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    } 
    return arr;
  }
}
