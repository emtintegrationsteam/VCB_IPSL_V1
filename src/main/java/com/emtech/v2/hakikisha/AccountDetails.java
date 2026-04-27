package com.emtech.v2.hakikisha;



import com.emtech.v2.utilities.AccntDetailsResponse;
import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.DatabaseMethods;

public class AccountDetails {
  Configurations cn = new Configurations();
  
  private String key = this.cn.getProperties().getProperty("enc.key").trim();
  
  private String initVector = this.cn.getProperties().getProperty("enc.initVector").trim();
  
  String detailsQuery = this.cn.getProperties().getProperty("stmt.api.sql.accountdetails").trim();
  
  public AccntDetailsResponse getAccountDetails(String account) {
    AccntDetailsResponse res = new AccntDetailsResponse();
    String accntname = DatabaseMethods.selectValues(this.detailsQuery, 1, 1, account);
    res.setAcountnumber(account);
    res.setAccountname(accntname);
    if (accntname.equalsIgnoreCase("")) {
      res.setStatus(false);
    } else {
      res.setStatus(true);
    } 
    return res;
  }
}
