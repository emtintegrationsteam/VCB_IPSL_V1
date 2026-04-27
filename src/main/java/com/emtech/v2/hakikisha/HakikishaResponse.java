package com.emtech.v2.hakikisha;

public class HakikishaResponse {
  private boolean status;
  
  private String name;
  
  private String code;
  
  public HakikishaResponse(boolean status, String name, String code) {
    this.status = status;
    this.name = name;
    this.code = code;
  }
  
  public HakikishaResponse() {}
  
  public boolean isStatus() {
    return this.status;
  }
  
  public void setStatus(boolean status) {
    this.status = status;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getCode() {
    return this.code;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
}
