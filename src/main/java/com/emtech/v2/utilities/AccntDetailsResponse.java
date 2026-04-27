package com.emtech.v2.utilities;

public class AccntDetailsResponse {
  private String accountname;
  
  private String acountnumber;
  
  private boolean status;
  
  public String getAccountname() {
    return this.accountname;
  }
  
  public void setAccountname(String accountname) {
    this.accountname = accountname;
  }
  
  public String getAcountnumber() {
    return this.acountnumber;
  }
  
  public void setAcountnumber(String acountnumber) {
    this.acountnumber = acountnumber;
  }
  
  public boolean isStatus() {
    return this.status;
  }
  
  public void setStatus(boolean status) {
    this.status = status;
  }
}
