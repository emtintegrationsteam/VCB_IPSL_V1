package com.emtech.v2.phonehakikisha;
import java.util.LinkedHashMap;

public class PhoneHakikishaResponse {
  private LinkedHashMap<String, String> bankname;
  
  private String customername;
  
  private boolean status;
  
  public PhoneHakikishaResponse(LinkedHashMap<String, String> bankname, String customername, boolean status) {
    this.bankname = bankname;
    this.customername = customername;
    this.status = status;
  }
  
  public LinkedHashMap<String, String> getBankname() {
    return this.bankname;
  }
  
  public void setBankname(LinkedHashMap<String, String> bankname) {
    this.bankname = bankname;
  }
  
  public String getCustomername() {
    return this.customername;
  }
  
  public void setCustomername(String customername) {
    this.customername = customername;
  }
  
  public boolean isStatus() {
    return this.status;
  }
  
  public void setStatus(boolean status) {
    this.status = status;
  }
  
  public PhoneHakikishaResponse() {}
}
