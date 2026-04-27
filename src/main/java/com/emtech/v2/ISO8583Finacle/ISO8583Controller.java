package com.emtech.v2.ISO8583Finacle;


import com.emtech.v2.ISO8583Finacle.PrincipalTransferResponse;
import com.emtech.v2.ISO8583Finacle.Transfer;
import java.io.IOException;
import java.sql.SQLException;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/"})
public class ISO8583Controller {
  Transfer tr = new Transfer();
  
  @RequestMapping({"stf/{account}/{amount}/{narration}/{ref}"})
  public String sendTransferToFinacle(@PathVariable("account") String account, @PathVariable("amount") String amount, @PathVariable("narration") String narration, @PathVariable("ref") String ref) throws SQLException, ISOException, IOException {
    String response = "";
    try {
      GenericPackager packager = new GenericPackager("basic_vcb.xml");
      this.tr.channel = (ISOChannel)new ASCIIChannel("172.16.210.102", 61146, (ISOPackager)packager);
      PrincipalTransferResponse pr = new PrincipalTransferResponse();
      pr = this.tr.sendPrincipalAmount(amount, account, narration, ref);
      response = pr.getCode();
    } catch (ISOException e) {
      System.out.println(e.getLocalizedMessage());
      return e.getLocalizedMessage();
    } catch (SQLException e) {
      System.out.println(e.getLocalizedMessage());
      return e.getLocalizedMessage();
    } 
    return response;
  }
  
  @RequestMapping({"sctf/{account}/{amount}/{narration}/{sendername}"})
  public String CreditCustomerAC(@PathVariable("account") String account, @PathVariable("amount") String amount, @PathVariable("narration") String narration, @PathVariable("sendername") String sender) throws SQLException, ISOException, IOException {
    String response = "";
    try {
      GenericPackager packager = new GenericPackager("/opt/basic_vcb.xml");
      this.tr.channel = (ISOChannel)new ASCIIChannel("172.16.210.102", 61146, (ISOPackager)packager);
      PrincipalTransferResponse pr = new PrincipalTransferResponse();
      pr = this.tr.creditCustomerAccount(amount, account, narration, sender);
      response = pr.getCode();
    } catch (ISOException e) {
      System.out.println(e.getLocalizedMessage());
      return e.getLocalizedMessage();
    } catch (SQLException e) {
      System.out.println(e.getLocalizedMessage());
      return e.getLocalizedMessage();
    } 
    return response;
  }
  
  @RequestMapping({"rf/{account}/{amount}/{stan}"})
  public String ReverseNow(@PathVariable("account") String account, @PathVariable("amount") String amount, @PathVariable("stan") String stan) throws SQLException, ISOException, IOException {
    String response = "";
    try {
      GenericPackager packager = new GenericPackager("basic_vcb.xml");
      this.tr.channel = (ISOChannel)new ASCIIChannel("172.16.210.102", 61146, (ISOPackager)packager);
      response = this.tr.doFinacleReversal(amount, account, stan);
    } catch (ISOException e) {
      System.out.println(e.getLocalizedMessage());
      return e.getLocalizedMessage();
    } catch (SQLException e) {
      System.out.println(e.getLocalizedMessage());
      return e.getLocalizedMessage();
    } 
    return response;
  }
}
