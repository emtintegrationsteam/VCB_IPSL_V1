package com.emtech.v2.phonehakikisha;

import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.phonehakikisha.PhoneHakikishaResponse;
import com.emtech.v2.phonehakikisha.SSLUtilities;
import com.emtech.v2.utilities.Configurations;
import java.net.Authenticator;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.net.ssl.*;

import ru.cwt.mgw.lookupdb.ws.LookupDbFault_Exception;
import ru.cwt.mgw.lookupdb.ws.LookupDbWS;
import ru.cwt.mgw.lookupdb.ws.LookupDbWSService;
import ru.cwt.mgw.lookupdb.ws.OnlineCustomerBank;
import ru.cwt.mgw.lookupdb.ws.OnlineCustomerBankListRequest;
import ru.cwt.mgw.lookupdb.ws.OnlineCustomerBankListResult;

public class LookUP {
  static Configurations cn = new Configurations();
  
  static String wsdluname = cn.getProperties().getProperty("pesa.wsdl.username");
  
  static String wsdlpassword = cn.getProperties().getProperty("pesa.wsdl.password");
  
  static String reguname = cn.getProperties().getProperty("pesa.wsdl.reg.username");
  
  static String regpassword = cn.getProperties().getProperty("pesa.wsdl.reg.password");
  
  static String insertquery = cn.getProperties().getProperty("pesa.query.insert.hakikisha.phone");

  static {
    String username = wsdluname;
    String password = wsdlpassword;

    java.net.Authenticator.setDefault(new java.net.Authenticator() {
      @Override
      protected java.net.PasswordAuthentication getPasswordAuthentication() {
        return new java.net.PasswordAuthentication(username, password.toCharArray());
      }
    });
  }


  private OnlineCustomerBankListResult getCustomerBankList(OnlineCustomerBankListRequest req) throws LookupDbFault_Exception {
    SSLUtilities.trustAllHostnames();
    disableVerification();
    LookupDbWSService service = new LookupDbWSService();
    LookupDbWS port = service.getLookupDbWSPort();
    return port.getCustomerBankList(req);
  }
  
  public List customerBankListWebService(String phonenumber) {
    SSLUtilities.trustAllHostnames();
    disableVerification();
    String bankcode = "";
    List<String> list = new ArrayList<>();
    try {
      String uname = reguname;
      String password = regpassword;
      OnlineCustomerBankListRequest req1 = new OnlineCustomerBankListRequest();
      req1.setLogin(uname);
      req1.setMsisdn(phonenumber);
      req1.setPassword(password);
      OnlineCustomerBankListResult y = getCustomerBankList(req1);
      if (y.equals(null)) {
        list.add("Phone Number Not yet Registered!");
      } else {
        List<OnlineCustomerBank> bl = y.getBankList().getBank();
        for (int i = 0; i < bl.size(); i++) {
          String l = "00" + ((OnlineCustomerBank)bl.get(i)).getSortCode().substring(3, 5) + " : " + ((OnlineCustomerBank)bl.get(i)).getBankName() + " : " + y.getDestName();
          list.add(l);
        } 
      } 
      return list;
    } catch (Exception e) {
      list.add("Failed : " + e.getLocalizedMessage());
      return list;
    } 
  }
  
  public PhoneHakikishaResponse bankCustListService(String phoneNo) {
    SSLUtilities.trustAllHostnames();
    disableVerification();
    PhoneHakikishaResponse ph = new PhoneHakikishaResponse();
    String bankcode = "";
    try {
      String uname = reguname;
      String password = regpassword;
      OnlineCustomerBankListRequest req1 = new OnlineCustomerBankListRequest();
      req1.setLogin(uname);
      req1.setMsisdn(phoneNo);
      req1.setPassword(password);
      OnlineCustomerBankListResult y = getCustomerBankList(req1);
      if (y.equals(null)) {
        ph.setBankname(null);
        ph.setCustomername("NOT FOUND");
        ph.setStatus(false);
      } else {
        List<OnlineCustomerBank> bl = y.getBankList().getBank();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        String cname = y.getDestName();
        for (int i = 0; i < bl.size(); i++) {
          bankcode = ((OnlineCustomerBank)bl.get(i)).getSortCode();
          bankcode = "00" + bankcode.substring(3, 5);
          map.put(bankcode, ((OnlineCustomerBank)bl.get(i)).getBankName());
        } 
        ph.setBankname(map);
        ph.setCustomername(cname);
        ph.setStatus(true);
      } 
      String data = phoneNo + "," + phoneNo + "," + ph.getBankname() + "," + ph.isStatus();
      DbMethods.dbWork(insertquery, 4, data);
      return ph;
    } catch (LookupDbFault_Exception ex) {
      ph.setBankname(null);
      ph.setCustomername("NOT FOUND");
      ph.setStatus(false);
      String data = phoneNo + "," + phoneNo + "," + ph.getBankname() + "," + ph.isStatus();
      DbMethods.dbWork(insertquery, 4, data);
      return ph;
    } 
  }

  public void disableVerification() {
    try {
      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
      }
      };
      // Install the all-trusting trust manager
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };

      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
  }
}
