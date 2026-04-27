package com.emtech.v2.ISO8583Finacle;
public class EdutyTransferResponse {
  private String code;
  
  private String Stan;
  
  public String getCode() {
    return this.code;
  }
  
  public EdutyTransferResponse() {}
  
  public void setCode(String code) {
    this.code = code;
  }
  
  public String getStan() {
    return this.Stan;
  }
  
  public void setStan(String stan) {
    this.Stan = stan;
  }
  
  public EdutyTransferResponse(String code, String stan) {
    this.code = code;
    this.Stan = stan;
  }
}
