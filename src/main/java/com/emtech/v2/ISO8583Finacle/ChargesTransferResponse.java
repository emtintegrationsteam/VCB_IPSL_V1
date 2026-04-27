package com.emtech.v2.ISO8583Finacle;

public class ChargesTransferResponse {
  private String code;
  
  private String stan;
  
  public ChargesTransferResponse(String code, String stan) {
    this.code = code;
    this.stan = stan;
  }
  
  public ChargesTransferResponse() {}
  
  public String getCode() {
    return this.code;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  
  public String getStan() {
    return this.stan;
  }
  
  public void setStan(String stan) {
    this.stan = stan;
  }
}
