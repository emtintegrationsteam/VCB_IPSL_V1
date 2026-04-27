package com.emtech.v2.tsq;

public class TSQResponse {
  private String amount;
  
  private String currency;
  
  private String code;
  
  private String messageid;
  
  private String originatorbank;
  
  private String beneficiarybank;
  
  private String CreditAccount;
  
  private String debitaccount;
  
  public TSQResponse(String amount, String currency, String code, String messageid, String originatorbank, String beneficiarybank, String creditAccount, String debitaccount) {
    this.amount = amount;
    this.currency = currency;
    this.code = code;
    this.messageid = messageid;
    this.originatorbank = originatorbank;
    this.beneficiarybank = beneficiarybank;
    this.CreditAccount = creditAccount;
    this.debitaccount = debitaccount;
  }
  
  public String getMessageid() {
    return this.messageid;
  }
  
  public void setMessageid(String messageid) {
    this.messageid = messageid;
  }
  
  public String getOriginatorbank() {
    return this.originatorbank;
  }
  
  public void setOriginatorbank(String originatorbank) {
    this.originatorbank = originatorbank;
  }
  
  public String getBeneficiarybank() {
    return this.beneficiarybank;
  }
  
  public void setBeneficiarybank(String beneficiarybank) {
    this.beneficiarybank = beneficiarybank;
  }
  
  public String getCreditAccount() {
    return this.CreditAccount;
  }
  
  public void setCreditAccount(String creditAccount) {
    this.CreditAccount = creditAccount;
  }
  
  public TSQResponse() {}
  
  public String getAmount() {
    return this.amount;
  }
  
  public void setAmount(String amount) {
    this.amount = amount;
  }
  
  public String getCurrency() {
    return this.currency;
  }
  
  public void setCurrency(String currency) {
    this.currency = currency;
  }
  
  public String getCode() {
    return this.code;
  }
  
  public void setCode(String code) {
    this.code = code;
  }
  
  public String getDebitaccount() {
    return this.debitaccount;
  }
  
  public void setDebitaccount(String debitaccount) {
    this.debitaccount = debitaccount;
  }
}
