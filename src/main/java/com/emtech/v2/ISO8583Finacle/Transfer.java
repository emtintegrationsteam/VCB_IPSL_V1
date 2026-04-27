package com.emtech.v2.ISO8583Finacle;

import com.emtech.v2.ISO8583Finacle.ChargesTransferResponse;
import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.ISO8583Finacle.EdutyTransferResponse;
import com.emtech.v2.ISO8583Finacle.PrincipalTransferResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOResponseListener;

public class Transfer implements Runnable, ISOResponseListener {
  public ISOChannel channel;
  
  String response;
  
  public static String formatMoney(String str) {
    String str2 = null;
    if (str.matches("^\\d+\\.\\d+")) {
      BigDecimal bd = new BigDecimal(str);
      bd = bd.setScale(2, 6);
      BigDecimal mult = new BigDecimal("100");
      bd = bd.multiply(mult);
      System.out.println(bd);
      str = bd.toString().substring(0, bd.toString().indexOf("."));
      System.out.println("Formatted Amount : " + str);
      str2 = String.format("%16s", new Object[] { String.valueOf(str) }).replace(' ', '0');
    } else {
      int val = Integer.parseInt(str);
      str2 = String.format("%14s", new Object[] { String.valueOf(val) }).replace(' ', '0');
      str2 = String.format("%-16s", new Object[] { String.valueOf(str2) }).replace(' ', '0');
    } 
    return str2;
  }
  
  public String formatField4(String str) {
    return formatMoney(str);
  }
  
  public String formatField56(String str, String time) {
    int val = Integer.parseInt(str);
    String code = "1200";
    String StanF = String.format("%012d", new Object[] { Integer.valueOf(val) });
    String trailer = "09006414567";
    String field56 = code + code + StanF + time;
    return field56;
  }
  
  private String formatField43(String tranpart) {
    return tranpart.substring(0, Math.min(tranpart.length(), 50));
  }
  
  public void run() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void responseReceived(ISOMsg isomsg, Object o) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void expired(Object o) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  String sqCahrge = "select amount from charges_configuration where low_amt <= ? and high_amt >= ?";
  
  public String getCharges(String amount) {
    try {
      String sqCharge = "select amount from charges_configuration where low_amt <= ? and high_amt >= ?";
      String insq = amount + "," + amount;
      String resps = DbMethods.getValue(this.sqCahrge, 1, 2, insq);
      return resps;
    } catch (NumberFormatException e) {
      System.out.println(e.getMessage());
      return null;
    } 
  }
  
  public PrincipalTransferResponse creditCustomerAccount(String amount, String acc, String narration, String senderName) throws SQLException, IOException {
    PrincipalTransferResponse tr = new PrincipalTransferResponse();
    String date = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
    String date2 = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
    ISOMsg msg = null;
    try {
      String settlementAcct = DbMethods.getParamValue("KITS.SETTLEMENT.ACCOUNT");
      String settlementSol = DbMethods.getParamValue("KITS.SETTLEMENT.SOL");
      Random randomGenerator = new Random();
      int randomInt = randomGenerator.nextInt(1000000);
      String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(randomInt) });
      ISOMsg m = new ISOMsg();
      tr.setStan(formatedInt);
      m.setMTI("1200");
      m.set(2, formatedInt);
      m.set(3, "400000");
      m.set(4, formatField4(amount));
      m.set(11, formatedInt);
      m.set(12, date);
      m.set(17, date2);
      m.set(24, "200");
      m.set(43, formatField43("PESALINK: " + formatedInt + " - " + senderName + " - " + narration));
      m.set(49, "KES");
      m.set(123, "CMN");
      m.set(32, "54");
      m.set(102, "54              " + settlementSol + "  " + settlementAcct);
      m.set(103, "54              " + settlementSol + "  " + acc);
      if (!this.channel.isConnected()) {
        this.channel.connect();
        this.channel.send(m);
      } 
      msg = this.channel.receive();
      msg.dump(System.out, "");
      this.response = msg.getString(39);
      tr.setCode(this.response);
      this.channel.disconnect();
      System.out.println("Response for Credit Transfer - (Credit) is:" + this.response);
    } catch (IOException ex) {
      System.out.println(ex.getLocalizedMessage());
      this.channel.disconnect();
      return tr;
    } catch (ISOException ex) {
      System.out.println(ex.getLocalizedMessage());
      Logger.getLogger(com.emtech.v2.ISO8583Finacle.Transfer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      this.channel.disconnect();
      return tr;
    } 
    return tr;
  }
  
  public PrincipalTransferResponse sendPrincipalAmount(String amount, String acc, String narration, String refNum) throws SQLException, IOException {
    PrincipalTransferResponse tr = new PrincipalTransferResponse();
    ISOMsg msg = null;
    try {
      String settlementAcct = DbMethods.getParamValue("KITS.SETTLEMENT.ACCOUNT");
      String settlementSol = DbMethods.getParamValue("KITS.SETTLEMENT.SOL");
      String date = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      String date2 = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
      Random randomGenerator = new Random();
      int randomInt = randomGenerator.nextInt(1000000);
      String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(randomInt) });
      ISOMsg m = new ISOMsg();
      tr.setStan(formatedInt);
      m.setMTI("1200");
      m.set(2, refNum);
      m.set(3, "400000");
      m.set(4, formatField4(amount));
      m.set(11, formatedInt);
      m.set(12, date);
      m.set(17, date2);
      m.set(24, "200");
      m.set(43, formatField43("PESALINK REF : " + refNum + " - " + narration));
      m.set(49, "KES");
      m.set(123, "CMN");
      m.set(32, "54");
      m.set(102, "54              " + settlementSol + "  " + acc);
      m.set(103, "54              " + settlementSol + "  " + settlementAcct);
      if (!this.channel.isConnected()) {
        this.channel.connect();
        this.channel.send(m);
      } 
      msg = this.channel.receive();
      msg.dump(System.out, "");
      this.response = msg.getString(39);
      tr.setCode(this.response);
      this.channel.disconnect();
      System.out.println("Response for Credit transfer (Debit) is:" + this.response);
    } catch (IOException ex) {
      this.channel.disconnect();
      return tr;
    } catch (ISOException ex) {
      Logger.getLogger(com.emtech.v2.ISO8583Finacle.Transfer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      this.channel.disconnect();
      return tr;
    } 
    return tr;
  }
  
  public ChargesTransferResponse sendChargeAmount(String amount, String acc, String refnum) throws SQLException, IOException {
    ISOMsg msg = null;
    ChargesTransferResponse tr = new ChargesTransferResponse();
    try {
      String settlementSol = DbMethods.getParamValue("KITS.SETTLEMENT.SOL");
      String chargeAcct = DbMethods.getParamValue("KITS.CHARGE.ACCOUNT");
      String date = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      String date2 = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
      Random randomGenerator = new Random();
      int randomInt = randomGenerator.nextInt(1000000);
      String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(randomInt) });
      ISOMsg m = new ISOMsg();
      tr.setStan(formatedInt);
      m.setMTI("1200");
      m.set(2, acc);
      m.set(3, "400000");
      m.set(4, formatField4(amount));
      m.set(11, formatedInt);
      m.set(12, date);
      m.set(17, date2);
      m.set(24, "200");
      m.set(43, formatField43("PESALINK REF : " + refnum + " - CHARGES."));
      m.set(49, "KES");
      m.set(123, "CMN");
      m.set(32, "54");
      m.set(102, "54              " + settlementSol + "  " + acc);
      m.set(103, "54              " + settlementSol + "  " + chargeAcct);
      if (!this.channel.isConnected()) {
        this.channel.connect();
        this.channel.send(m);
      } 
      msg = this.channel.receive();
      msg.dump(System.out, "");
      this.response = msg.getString(39);
      tr.setCode(this.response);
      this.channel.disconnect();
      System.out.println("Response for Charges Transaction  is : " + this.response);
    } catch (IOException ex) {
      this.channel.disconnect();
      System.out.println(ex.getLocalizedMessage());
      return tr;
    } catch (ISOException ex) {
      this.channel.disconnect();
      Logger.getLogger(com.emtech.v2.ISO8583Finacle.Transfer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      return tr;
    } 
    return tr;
  }
  
  public EdutyTransferResponse sendExciseDutyAmount(String amount, String acc, String refNum) throws SQLException, IOException {
    ISOMsg msg = null;
    EdutyTransferResponse tr = new EdutyTransferResponse();
    try {
      String settlementSol = DbMethods.getParamValue("KITS.SETTLEMENT.SOL");
      String EdutyAcct = DbMethods.getParamValue("KITS.EDUTY.ACCOUNT");
      String date = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      String date2 = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
      Random randomGenerator = new Random();
      int randomInt = randomGenerator.nextInt(1000000);
      String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(randomInt) });
      ISOMsg m = new ISOMsg();
      tr.setStan(formatedInt);
      m.setMTI("1200");
      m.set(2, acc);
      m.set(3, "400000");
      m.set(4, formatField4(amount));
      m.set(11, formatedInt);
      m.set(12, date);
      m.set(17, date2);
      m.set(24, "200");
      m.set(43, formatField43("PESALINK REF : " + refNum + " - EXCISE DUTY"));
      m.set(49, "KES");
      m.set(123, "CMN");
      m.set(32, "54");
      m.set(102, "54              " + settlementSol + acc);
      m.set(103, "54              " + settlementSol + "  " + EdutyAcct);
      if (!this.channel.isConnected()) {
        this.channel.connect();
        this.channel.send(m);
      } 
      msg = this.channel.receive();
      msg.dump(System.out, "");
      this.response = msg.getString(39);
      tr.setCode(this.response);
      this.channel.disconnect();
      System.out.println("Response for Excise Duty is : " + this.response);
    } catch (IOException ex) {
      System.out.println(ex.getLocalizedMessage());
      this.channel.disconnect();
      return tr;
    } catch (ISOException ex) {
      Logger.getLogger(com.emtech.v2.ISO8583Finacle.Transfer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      this.channel.disconnect();
      return tr;
    } 
    return tr;
  }
  
  public String doFinacleReversal(String amount, String acc, String txnStan) throws SQLException {
    try {
      System.out.println("The stan Generated is:" + txnStan);
      String settlementAcct = DbMethods.getParamValue("KITS.SETTLEMENT.ACCOUNT");
      String settlementSol = DbMethods.getParamValue("KITS.SETTLEMENT.SOL");
      String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(Integer.parseInt(txnStan)) });
      String date = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      String date2 = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
      ISOMsg m = new ISOMsg();
      m.setMTI("1420");
      m.set(2, acc);
      m.set(3, "400000");
      System.out.println("amount to be reversed is : " + amount);
      m.set(4, formatField4(amount));
      m.set(11, formatedInt);
      m.set(12, date);
      m.set(17, date2);
      m.set(24, "400");
      m.set(43, formatField43("PESALINK REF : REVERSE - " + formatedInt));
      m.set(49, "KES");
      m.set(56, "1200000000" + txnStan + "2020100901010109200509110");
      m.set(123, "CMN");
      m.set(32, "54");
      m.set(102, "54              " + settlementSol + acc);
      m.set(103, "54              " + settlementSol + "  " + settlementAcct);
      if (!this.channel.isConnected())
        this.channel.connect(); 
      this.channel.send(m);
      System.out.println("Sent message is for reversal...." + m.toString());
      ISOMsg msg = this.channel.receive();
      this.response = msg.getString(39);
      System.out.println(msg);
      System.out.println("Response for reversal is:" + this.response);
      this.channel.disconnect();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ISOException ex) {
      Logger.getLogger(com.emtech.v2.ISO8583Finacle.Transfer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
    } 
    return this.response;
  }
  
  public String ReversePricipalAmount(String amount, String acc, String txnStan, String RefNum) throws SQLException, IOException {
    try {
      String settlementAcct = DbMethods.getParamValue("KITS.SETTLEMENT.ACCOUNT");
      String settlementSol = DbMethods.getParamValue("KITS.SETTLEMENT.SOL");
      String date = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      String date2 = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
      Random randomGenerator = new Random();
      int randomInt = randomGenerator.nextInt(1000000);
      String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(randomInt) });
      ISOMsg msg = null;
      ISOMsg m = new ISOMsg();
      m.setMTI("1200");
      m.set(2, RefNum);
      m.set(3, "400000");
      m.set(4, formatField4(amount));
      m.set(11, formatedInt);
      m.set(12, date);
      m.set(17, date2);
      m.set(24, "200");
      m.set(43, formatField43("PESALINK REF : " + RefNum + " P.AMT REVERSAL"));
      m.set(49, "KES");
      m.set(123, "CMN");
      m.set(32, "54");
      m.set(102, "54              " + settlementSol + "  " + settlementAcct);
      m.set(103, "54              " + settlementSol + "  " + acc);
      if (!this.channel.isConnected()) {
        this.channel.connect();
        this.channel.send(m);
      } 
      msg = this.channel.receive();
      msg.dump(System.out, "");
      this.response = msg.getString(39);
      this.channel.disconnect();
      System.out.println("Response for Principal Amount Reversal is : " + this.response);
    } catch (IOException ex) {
      this.channel.disconnect();
      System.out.println("Principal Amount Reversal Err. " + ex.getLocalizedMessage());
    } catch (ISOException ex) {
      Logger.getLogger(com.emtech.v2.ISO8583Finacle.Transfer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      System.out.println("Principal Amount Reversal Err. " + ex.getLocalizedMessage());
      this.channel.disconnect();
      return this.response;
    } 
    return this.response;
  }
  
  public String ReverseChargeAmount(String amount, String acc, String txnStan, String RefNum) throws SQLException, IOException {
    try {
      System.out.println("Charges Amount to be reversed is : " + amount + " for RefNumber - " + RefNum);
      String settlementAcct = DbMethods.getParamValue("KITS.CHARGE.ACCOUNT");
      String settlementSol = DbMethods.getParamValue("KITS.SETTLEMENT.SOL");
      String date = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      String date2 = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
      Random randomGenerator = new Random();
      int randomInt = randomGenerator.nextInt(1000000);
      String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(randomInt) });
      ISOMsg msg = null;
      ISOMsg m = new ISOMsg();
      m.setMTI("1200");
      m.set(2, RefNum);
      m.set(3, "400000");
      m.set(4, formatField4(amount));
      m.set(11, formatedInt);
      m.set(12, date);
      m.set(17, date2);
      m.set(24, "200");
      m.set(43, formatField43("PESALINK REF : " + RefNum + " CHARGES REVERSAL"));
      m.set(49, "KES");
      m.set(123, "CMN");
      m.set(32, "54");
      m.set(102, "54              " + settlementSol + "  " + settlementAcct);
      m.set(103, "54              " + settlementSol + "  " + acc);
      if (!this.channel.isConnected()) {
        this.channel.connect();
        this.channel.send(m);
      } 
      msg = this.channel.receive();
      msg.dump(System.out, "");
      this.response = msg.getString(39);
      this.channel.disconnect();
      System.out.println("Response for Charge Amount Reversal is : " + this.response);
    } catch (IOException ex) {
      this.channel.disconnect();
      System.out.println("Charge Amount Reversal Err. " + ex.getLocalizedMessage());
    } catch (ISOException ex) {
      Logger.getLogger(com.emtech.v2.ISO8583Finacle.Transfer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      System.out.println("Charge Amount Reversal Err. " + ex.getLocalizedMessage());
      this.channel.disconnect();
      return this.response;
    } 
    return this.response;
  }
  
  public String ReverseEdutyAmount(String amount, String acc, String txnStan, String RefNum) throws SQLException, IOException {
    try {
      System.out.println("E-Duty Amount to be reversed is : " + amount + " for RefNumber - " + RefNum);
      String settlementAcct = DbMethods.getParamValue("KITS.EDUTY.ACCOUNT");
      String settlementSol = DbMethods.getParamValue("KITS.SETTLEMENT.SOL");
      String date = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      String date2 = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
      Random randomGenerator = new Random();
      int randomInt = randomGenerator.nextInt(1000000);
      String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(randomInt) });
      ISOMsg msg = null;
      ISOMsg m = new ISOMsg();
      m.setMTI("1200");
      m.set(2, RefNum);
      m.set(3, "400000");
      m.set(4, formatField4(amount));
      m.set(11, formatedInt);
      m.set(12, date);
      m.set(17, date2);
      m.set(24, "200");
      m.set(43, formatField43("PESALINK REF : " + RefNum + " E-DUTY REVERSAL"));
      m.set(49, "KES");
      m.set(123, "CMN");
      m.set(32, "54");
      m.set(102, "54              " + settlementSol + "  " + settlementAcct);
      m.set(103, "54              " + settlementSol + "  " + acc);
      if (!this.channel.isConnected()) {
        this.channel.connect();
        this.channel.send(m);
      } 
      msg = this.channel.receive();
      msg.dump(System.out, "");
      this.response = msg.getString(39);
      this.channel.disconnect();
      System.out.println("Response for E-Duty Amount Reversal is : " + this.response);
    } catch (IOException ex) {
      this.channel.disconnect();
      System.out.println("E-Duty Amount Reversal Err. " + ex.getLocalizedMessage());
    } catch (ISOException ex) {
      Logger.getLogger(com.emtech.v2.ISO8583Finacle.Transfer.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      System.out.println("E-Duty Amount Reversal Err. " + ex.getLocalizedMessage());
      this.channel.disconnect();
      return this.response;
    } 
    return this.response;
  }
}
