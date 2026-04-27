package com.emtech.v2.bulkprocessing;
import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.ISO8583Finacle.Transfer;
import com.emtech.v2.bulkprocessing.BulkResponse;
import com.emtech.v2.bulkprocessing.Pesalink;
import com.emtech.v2.bulkprocessing.TransactionList;
import com.emtech.v2.bulkprocessing.generated.CdtTrfTxInf;
import com.emtech.v2.credittransfer.ToolKit;
import com.emtech.v2.hakikisha.AccountDetails;
import com.emtech.v2.hakikisha.GenEnveloped;
import com.emtech.v2.phonehakikisha.LookUP;
import com.emtech.v2.phonehakikisha.PhoneHakikishaResponse;
import com.emtech.v2.utilities.AccntDetailsResponse;
import com.emtech.v2.utilities.AddCertificates;
import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.DatabaseMethods;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkPacs008 {
  private static final Logger log = LoggerFactory.getLogger(BulkPacs008.class);
  
  static Configurations cn = new Configurations();
  
  static String pacs008Bulkurl = cn.getProperties().getProperty("pesaBulk.url.pacs008");
  
  static String pacs008url = cn.getProperties().getProperty("pesa.url.pacs008");
  
  static String outputpacs008 = cn.getProperties().getProperty("pesa.output.pacs008");
  
  static String key = cn.getProperties().getProperty("pesa.sign.keys");
  
  static String seal = cn.getProperties().getProperty("pesa.sign.seal");
  
  static String insertquery = cn.getProperties().getProperty("pesa.query.insert.pacs");
  
  String select_phoneno = cn.getProperties().getProperty("stmt.api.sql.phoneno").trim();
  
  AddCertificates ac = new AddCertificates();
  
  ToolKit tk = new ToolKit();
  
  AccountDetails ad = new AccountDetails();
  
  PhoneHakikishaResponse phr = new PhoneHakikishaResponse();
  
  LookUP lu = new LookUP();
  
  Transfer tr = new Transfer();
  
  static String updatequery = "";
  
  public BulkResponse sendBulkPACS008(OkHttpClient oclient, List<Pesalink> ps) throws Exception {
    BulkResponse br = new BulkResponse();
    log.info("Processing Bulk - " + new Date());
    OkHttpClient client = this.ac.addCertificates(oclient);
    log.info("Certificates added.. ");
    GenEnveloped ge = new GenEnveloped();
    StringBuilder sb = new StringBuilder();
    String transactions = "";
    String MessageId = ((Pesalink)ps.get(0)).getOriginalMessageId();
    String date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
    String trantype = "P2PT";
    String SttlmMtd = "CLRG";
    String ClrSys = "IPS";
    String LclInstrm = "INST";
    String CtgyPurp = "IBNK";
    String originatorbankcode = "0054";
    String FinInstnId = "9999";
    for (Pesalink p : ps) {
      log.info("Original message id {}", p.getOriginalMessageId());
      log.info("EndtoEndId {}", p.getEndToEndId());
      AccntDetailsResponse res = this.ad.getAccountDetails(p.getDebitAccount().trim());
      String debtorname = res.getAccountname();
      log.info("Debtor Account Name: {}", debtorname);
      if (containsSpecialCharacters(debtorname)) {
        log.info("Account Name Contains Special characters ");
        debtorname = URLEncoder.encode(debtorname, StandardCharsets.UTF_8);
      } 
      String PhneNb = DatabaseMethods.selectValues(this.select_phoneno, 1, 1, p.getDebitAccount());
      System.out.println("Phone from crm " + PhneNb);
      if (PhneNb.isEmpty() || PhneNb == null)
        PhneNb = "254709876000"; 
      if (!PhneNb.contains("+"))
        PhneNb = "+" + PhneNb; 
      PhneNb = PhneNb.replace("(", "");
      PhneNb = PhneNb.replace(")", "");
      PhneNb = this.tk.insertString(PhneNb, "-", 3);
      String benbank = p.getBeneficiaryBankCode().trim();
      log.info(benbank);
      String ChrgBr = "SLEV";
      String crname = "";
      String endtoendid = p.getEndToEndId();
      String TranType = "";
      String CreditorName = "";
      log.info("End To End ID - " + endtoendid);
      log.info("Recipient Type - " + p.getRecipientType());
      transactions = "<CdtTrfTxInf><PmtId><EndToEndId>" + endtoendid + "</EndToEndId></PmtId><IntrBkSttlmAmt Ccy=\"KES\">" + p.getAmount() + "</IntrBkSttlmAmt>\n      <AccptncDtTm>" + date + "</AccptncDtTm><ChrgBr>" + ChrgBr + "</ChrgBr><Dbtr><Nm>" + debtorname + "</Nm><Id>\n        <OrgId><Othr><Id>" + originatorbankcode + "</Id></Othr></OrgId></Id><CtctDtls><PhneNb>" + PhneNb + "</PhneNb></CtctDtls></Dbtr>\n      <DbtrAcct><Id><Othr><Id>" + p.getDebitAccount().trim() + "</Id></Othr></Id><Nm>" + debtorname + "</Nm></DbtrAcct>\n      <DbtrAgt><FinInstnId><Othr><Id>" + originatorbankcode + "</Id></Othr></FinInstnId></DbtrAgt>\n      <CdtrAgt><FinInstnId><Othr><Id>" + benbank.trim() + "</Id></Othr></FinInstnId></CdtrAgt><Cdtr />\n      <CdtrAcct><Id><Othr><Id>" + p.getRecipientAccount().trim() + "</Id></Othr></Id><Nm>" + p.getRecipientName() + "</Nm></CdtrAcct>\n      <Purp><Prtry>260</Prtry></Purp><RmtInf><Ustrd>" + p.getNarration().trim() + "</Ustrd></RmtInf>\n</CdtTrfTxInf>";
      sb.append(transactions);
      sb.append("\n");
    } 
    transactions = sb.toString();
    try {
      String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09\">\n  <FIToFICstmrCdtTrf>\n    <GrpHdr>\n      <MsgId>" + MessageId + "</MsgId>\n      <CreDtTm>" + date + "</CreDtTm>\n      <NbOfTxs>" + ps.size() + "</NbOfTxs>\n      <TtlIntrBkSttlmAmt Ccy=\"KES\">" + getTotalAmount(ps) + "</TtlIntrBkSttlmAmt>\n      <SttlmInf>\n        <SttlmMtd>" + SttlmMtd + "</SttlmMtd>\n        <ClrSys>\n          <Prtry>" + ClrSys + "</Prtry>\n        </ClrSys>\n      </SttlmInf>\n      <PmtTpInf>\n        <SvcLvl>\n          <Prtry>" + trantype + "</Prtry>\n        </SvcLvl>\n        <LclInstrm>\n          <Cd>" + LclInstrm + "</Cd>\n        </LclInstrm>\n        <CtgyPurp>\n          <Prtry>" + CtgyPurp + "</Prtry>\n        </CtgyPurp>\n      </PmtTpInf>\n      <InstgAgt>\n        <FinInstnId>\n          <Othr>\n            <Id>" + originatorbankcode + "</Id>\n          </Othr>\n        </FinInstnId>\n      </InstgAgt>\n      <InstdAgt>\n        <FinInstnId>\n          <Othr>\n            <Id>" + FinInstnId + "</Id>\n          </Othr>\n        </FinInstnId>\n      </InstdAgt>\n    </GrpHdr>\n" + transactions + "  </FIToFICstmrCdtTrf>\n</Document>";
      String signed = ge.genEnveloped(xml, outputpacs008, key, seal);
      MediaType mediaType = MediaType.parse("application/xml;charset=UTF-8");
      RequestBody body = RequestBody.create(mediaType, signed);
      System.out.println("signed request \n" + signed);
      Request request = (new Request.Builder()).url(pacs008Bulkurl).post(body).addHeader("Content-Type", "application/xml;charset=UTF-8").build();
      Response response = client.newCall(request).execute();
      try {
        System.out.println("BUlk Response " + response);
        if (!response.isSuccessful()) {
          Headers responseHeaders = response.headers();
          for (int i = 0; i < responseHeaders.size(); i++)
            log.info("headers-> {} ", responseHeaders.name(i) + ": " + responseHeaders.name(i)); 
          br.setMessageId(MessageId);
          br.setEndToEndId(MessageId);
          br.setMessage("Error Occurred when posting to Pesalink");
          br.setStatus("ERROR");
        } else {
          String CreditorName = "NA";
          String Pacs002Code = "NA";
          String PrincipalAmtFinResponse = "NA";
          String ChargeAmtFinResponse = "NA";
          String EdutyAmtFinResponse = "NA";
          String CreditTranFinResponse = "NA";
          String direction = "Outgoing";
          String CreditorPhoneNo = "NA";
          String PrincipalStan = "NA";
          String ChargesStan = "NA";
          String EdutyStan = "-";
          String TranType = "A2A";
          String trans = "<TransactionList>" + transactions.trim() + "</TransactionList>";
          try {
            JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { TransactionList.class });
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TransactionList transactionList = (TransactionList)jaxbUnmarshaller.unmarshal(new StringReader(trans));
            List<CdtTrfTxInf> ts = transactionList.getTransactions();
            log.info("------------Inserting data to DB----------------");
            for (CdtTrfTxInf t : ts) {
              String refNum = this.tk.generateString().toUpperCase();
              String ChargeAmount = this.tr.getCharges(t.getIntrBkSttlmAmt().getValue());
              String edp = DbMethods.getParamValue("KITS.EDUTY.PCNT");
              double double_edp = Double.parseDouble(edp);
              double chrg_amt = Double.parseDouble(ChargeAmount);
              Double excDutyAmt = Double.valueOf(chrg_amt * double_edp / 100.0D);
              String ExciseDuty = String.valueOf(excDutyAmt);
              String data = MessageId + "," + MessageId + "," + date + "," + ps.size() + "," + SttlmMtd + "," + ClrSys + "," + trantype + "," + LclInstrm + "," + CtgyPurp + "," + originatorbankcode + "," + FinInstnId + "," + t.getPmtId().getEndToEndId() + ",KES," + t.getIntrBkSttlmAmt().getValue() + "," + t.getAccptncDtTm() + "," + t.getChrgBr() + "," + t.getDbtr().getNm() + "," + t.getDbtr().getCtctDtls().getPhneNb() + "," + t.getDbtrAcct().getId().getOthr().getId() + "," + t.getCdtrAcct().getId().getOthr().getId() + "," + CreditorName + "," + t.getCdtrAgt().getFinInstnId().getOthr().getId() + "," + t.getRmtInf().getUstrd() + "," + ChargeAmount + "," + ExciseDuty + "," + Pacs002Code + "," + PrincipalAmtFinResponse + "," + ChargeAmtFinResponse + "," + EdutyAmtFinResponse + "," + CreditTranFinResponse + "," + TranType + "," + CreditorPhoneNo + "," + direction + "," + refNum + "," + PrincipalStan + "," + ChargesStan;
              System.out.println("Data to be inserted for outgoing tran");
              System.out.println(data);
              DbMethods.dbWork(insertquery, 36, data);
              log.info("\n\n...............................................................................................................\nOutgoing Transaction (Account to Phone) : Message ID :: " + MessageId + "\n...............................................................................................................\n\n");
            } 
            log.info("------------Done----------------");
          } catch (JAXBException e) {
            e.printStackTrace();
          } 
        } 
        System.out.println("BUlk Response " + response);
        br.setMessageId(MessageId);
        br.setEndToEndId(MessageId);
        br.setMessage("Accepted Successfully For Processing");
        br.setStatus("0");
        if (response != null)
          response.close(); 
      } catch (Throwable throwable) {
        if (response != null)
          try {
            response.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
      br.setMessage(e.getMessage());
      br.setStatus("ERROR");
      e.printStackTrace();
    } 
    return br;
  }
  
  public BulkResponse sendPACS008(OkHttpClient oclient, Pesalink pesalink) throws Exception {
    BulkResponse br = new BulkResponse();
    GenEnveloped ge = new GenEnveloped();
    AccntDetailsResponse res = new AccntDetailsResponse();
    res = this.ad.getAccountDetails(pesalink.getDebitAccount());
    String debtorname = res.getAccountname();
    System.out.println("Sender Name { " + debtorname + " }");
    String PhneNb = DatabaseMethods.selectValues(this.select_phoneno, 1, 1, pesalink.getDebitAccount());
    if (!PhneNb.contains("+"))
      PhneNb = "+" + PhneNb; 
    PhneNb = PhneNb.replace("(", "");
    PhneNb = PhneNb.replace(")", "");
    PhneNb = this.tk.insertString(PhneNb, "-", 3);
    System.out.println("Sender Phone No { " + PhneNb + " }");
    Random rn = new Random();
    String trantype = "P2PT";
    String SttlmMtd = "CLRG";
    String ClrSys = "IPS";
    String LclInstrm = "INST";
    String CtgyPurp = "IBNK";
    String originatorbankcode = "0054";
    String ChrgBr = "SLEV";
    String date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
    OkHttpClient client = this.ac.addCertificates(oclient);
    String xml = "";
    String endtoendid = pesalink.getEndToEndId();
    xml = "<?xml version='1.0' encoding='UTF-8'?>\n<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09\">\n    <FIToFICstmrCdtTrf>\n        <GrpHdr>\n            <MsgId>" + pesalink.getMessageId() + "</MsgId>\n            <CreDtTm>" + date + "</CreDtTm>\n            <NbOfTxs>1</NbOfTxs>\n            <SttlmInf>\n                <SttlmMtd>" + SttlmMtd + "</SttlmMtd>\n                <ClrSys>\n                    <Prtry>" + ClrSys + "</Prtry>\n                </ClrSys>\n            </SttlmInf>\n            <PmtTpInf>\n                <SvcLvl>\n                    <Prtry>" + trantype + "</Prtry>\n                </SvcLvl>\n                <LclInstrm>\n                    <Cd>" + LclInstrm + "</Cd>\n                </LclInstrm>\n                <CtgyPurp>\n                    <Prtry>" + CtgyPurp + "</Prtry>\n                </CtgyPurp>\n            </PmtTpInf>\n            <InstgAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + originatorbankcode + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </InstgAgt>\n            <InstdAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>9999</Id>\n                    </Othr>\n                </FinInstnId>\n            </InstdAgt>\n        </GrpHdr>\n        <CdtTrfTxInf>\n            <PmtId>\n                <EndToEndId>" + endtoendid + "</EndToEndId>\n            </PmtId>\n            <IntrBkSttlmAmt Ccy=\"KES\">" + pesalink.getAmount() + "</IntrBkSttlmAmt>\n            <AccptncDtTm>" + date + "</AccptncDtTm>\n            <ChrgBr>" + ChrgBr + "</ChrgBr>\n            <Dbtr>\n                <Nm>" + debtorname + "</Nm>\n                <CtctDtls>\n                    <PhneNb>" + PhneNb + "</PhneNb>\n                </CtctDtls>\n            </Dbtr>\n            <DbtrAcct>\n                <Id>\n                    <Othr>\n                        <Id>" + pesalink.getDebitAccount() + "</Id>\n                    </Othr>\n                </Id>\n            </DbtrAcct>\n            <DbtrAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + originatorbankcode + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </DbtrAgt>\n            <CdtrAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + pesalink.getBeneficiaryBankCode() + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </CdtrAgt>\n            <Cdtr>\n            </Cdtr>\n            <CdtrAcct>\n                <Id>\n                    <Othr>\n                        <Id>" + pesalink.getRecipientAccount() + "</Id>\n                    </Othr>\n                </Id>\n            </CdtrAcct>\n            <Purp>\n                <Prtry>001</Prtry>\n            </Purp>\n            <RmtInf>\n                <Ustrd>" + pesalink.getNarration() + "</Ustrd>\n            </RmtInf>\n        </CdtTrfTxInf>\n    </FIToFICstmrCdtTrf>\n</Document>";
    try {
      String signed = ge.genEnveloped(xml, outputpacs008, key, seal);
      System.out.println("Pacs 008 to be sent");
      System.out.println(signed);
      MediaType mediaType = MediaType.parse("application/xml;charset=UTF-8");
      RequestBody body = RequestBody.create(mediaType, signed);
      Request request = (new Request.Builder()).url(pacs008url).post(body).addHeader("Content-Type", "application/xml;charset=UTF-8").build();
      Response response = client.newCall(request).execute();
      if (!response.isSuccessful()) {
        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++)
          System.out.println(responseHeaders.name(i) + ": " + responseHeaders.name(i)); 
        throw new IOException("Error " + response);
      } 
      System.out.println("BUlk Response " + response);
      br.setMessageId(pesalink.getOriginalMessageId());
      br.setEndToEndId(pesalink.getEndToEndId());
      br.setMessage("Accepted Successfully For Processing");
      br.setStatus("0");
    } catch (Exception e) {
      log.error(e.getLocalizedMessage());
      br.setMessage(e.getMessage());
      br.setStatus("ERROR");
      e.printStackTrace();
    } 
    return br;
  }
  
  public Double getTotalAmount(List<Pesalink> bulkTransactions) {
    double totalAmount = 0.0D;
    for (Pesalink p : bulkTransactions)
      totalAmount += p.getAmount().doubleValue(); 
    return Double.valueOf(totalAmount);
  }
  
  public static boolean containsSpecialCharacters(String input) {
    String pattern = "[^a-zA-Z0-9]";
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(input);
    return m.find();
  }
}
