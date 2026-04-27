package com.emtech.v2.tsq;

import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.credittransfer.ToolKit;
import com.emtech.v2.hakikisha.GenEnveloped;
import com.emtech.v2.utilities.AddCertificates;
import com.emtech.v2.utilities.Configurations;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class pacs028 {
  static Configurations cn = new Configurations();
  
  static String pacs028url = cn.getProperties().getProperty("pesa.url.pacs028");
  
  static String outputpacs028 = cn.getProperties().getProperty("pesa.output.pacs028");
  
  static String key = cn.getProperties().getProperty("pesa.sign.keys");
  
  static String seal = cn.getProperties().getProperty("pesa.sign.seal");
  
  static String reqlogfile = cn.getProperties().getProperty("pesa.logs.pacs028.txt");
  
  ToolKit tk = new ToolKit();
  
  public void sendPACS028(OkHttpClient oclient, String MsgId) throws Exception {
    FileWriter writer = new FileWriter(reqlogfile, true);
    BufferedWriter bufferedWriter = new BufferedWriter(writer);
    GenEnveloped ge = new GenEnveloped();
    Random rn = new Random();
    String unique = String.valueOf(rn.nextInt(999999001) + 2);
    String originatorbankcode = "0054";
    String OrgnlCreDtTm = "";
    String OrgnlEndToEndId = "";
    String IntrBkSttlmAmtCcy = "";
    String IntrBkSttlmAmt = "";
    String DbtrNm = "";
    String DbtrPhneNb = "";
    String DbtrAcctId = "";
    String DbtrAgtId = "";
    String CdtrAgtId = "";
    String AccptncDtTm = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
    String StsReqId = this.tk.generateString();
    OrgnlCreDtTm = DbMethods.getValue("SELECT CreDtTm FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    OrgnlEndToEndId = DbMethods.getValue("SELECT EndToEndId FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    IntrBkSttlmAmtCcy = DbMethods.getValue("SELECT Currency FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    IntrBkSttlmAmt = DbMethods.getValue("SELECT TranAmount FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    DbtrNm = DbMethods.getValue("SELECT DebtorName FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    DbtrPhneNb = DbMethods.getValue("SELECT DebtorPhoneNo FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    DbtrAcctId = DbMethods.getValue("SELECT DebitAccount FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    DbtrAgtId = DbMethods.getValue("SELECT OriginatorPIC FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    CdtrAgtId = DbMethods.getValue("SELECT BeneficiaryBank FROM CreditTransfer WHERE MsgId=?", 1, 1, MsgId);
    AddCertificates ac = new AddCertificates();
    String date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
    OkHttpClient client = ac.addCertificates(oclient);
    String xml = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.028.001.04\">\n\t<FIToFIPmtStsReq>\n\t\t<GrpHdr>\n\t\t\t<MsgId>" + unique + "</MsgId>\n\t\t\t<CreDtTm>" + date + "</CreDtTm>\n\t\t\t<InstgAgt>\n\t\t\t\t<FinInstnId>\n\t\t\t\t\t<Othr>\n\t\t\t\t\t\t<Id>" + originatorbankcode + "</Id>\n\t\t\t\t\t</Othr>\n\t\t\t\t</FinInstnId>\n\t\t\t</InstgAgt>\n\t\t\t<InstdAgt>\n\t\t\t\t<FinInstnId>\n\t\t\t\t\t<Othr>\n\t\t\t\t\t\t<Id>9999</Id>\n\t\t\t\t\t</Othr>\n\t\t\t\t</FinInstnId>\n\t\t\t</InstdAgt>\n\t\t</GrpHdr>\n\t\t<OrgnlGrpInf>\n\t\t\t<OrgnlMsgId>" + MsgId + "</OrgnlMsgId>\n\t\t\t<OrgnlMsgNmId>pacs.008</OrgnlMsgNmId>\n\t\t\t<OrgnlCreDtTm>" + OrgnlCreDtTm + "</OrgnlCreDtTm>\n\t\t</OrgnlGrpInf>\n\t\t<TxInf>\n\t\t\t<StsReqId>" + StsReqId + "</StsReqId>\n\t\t\t<OrgnlEndToEndId>" + OrgnlEndToEndId + "</OrgnlEndToEndId>\n\t\t\t<AccptncDtTm>" + AccptncDtTm + "</AccptncDtTm>\n\t\t\t<OrgnlTxRef>\n\t\t\t\t<IntrBkSttlmAmt Ccy=\"" + IntrBkSttlmAmtCcy + "\">" + IntrBkSttlmAmt + "</IntrBkSttlmAmt>\n\t\t\t\t<Dbtr>\n\t\t\t\t\t<Pty>\n\t\t\t\t\t\t<Nm>" + DbtrNm + "</Nm>\n\t\t\t\t\t\t<CtctDtls>\n\t\t\t\t\t\t\t<PhneNb>" + DbtrPhneNb + "</PhneNb>\n\t\t\t\t\t\t</CtctDtls>\n\t\t\t\t\t</Pty>\n\t\t\t\t</Dbtr>\n\t\t\t\t<DbtrAcct>\n\t\t\t\t\t<Id>\n\t\t\t\t\t\t<Othr>\n\t\t\t\t\t\t\t<Id>" + DbtrAcctId + "</Id>\n\t\t\t\t\t\t</Othr>\n\t\t\t\t\t</Id>\n\t\t\t\t</DbtrAcct>\n\t\t\t\t<DbtrAgt>\n\t\t\t\t\t<FinInstnId>\n\t\t\t\t\t\t<Othr>\n\t\t\t\t\t\t\t<Id>" + DbtrAgtId + "</Id>\n\t\t\t\t\t\t</Othr>\n\t\t\t\t\t</FinInstnId>\n\t\t\t\t</DbtrAgt>\n\t\t\t\t<CdtrAgt>\n\t\t\t\t\t<FinInstnId>\n\t\t\t\t\t\t<Othr>\n\t\t\t\t\t\t\t<Id>" + CdtrAgtId + "</Id>\n\t\t\t\t\t\t</Othr>\n\t\t\t\t\t</FinInstnId>\n\t\t\t\t</CdtrAgt>\n\t\t\t</OrgnlTxRef>\n\t\t</TxInf>\n\t</FIToFIPmtStsReq>\n</Document>";
    try {
      String signed = ge.genEnveloped(xml, outputpacs028, key, seal);
      bufferedWriter.newLine();
      bufferedWriter.write("****************************************************************************");
      bufferedWriter.newLine();
      bufferedWriter.write("PACS 028 TSQ REQUEST FOR MESSGAGE ID " + MsgId + " SENT @ " + date);
      bufferedWriter.newLine();
      bufferedWriter.write(signed);
      bufferedWriter.newLine();
      bufferedWriter.close();
      MediaType mediaType = MediaType.parse("application/xml;charset=UTF-8");
      RequestBody body = RequestBody.create(mediaType, signed);
      Request request = (new Request.Builder()).url(pacs028url).post(body).addHeader("Content-Type", "application/xml;charset=UTF-8").build();
      Response response = client.newCall(request).execute();
      try {
        if (!response.isSuccessful()) {
          Headers responseHeaders = response.headers();
          for (int i = 0; i < responseHeaders.size(); i++)
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.name(i)); 
          throw new IOException("Error " + response);
        } 
        System.out.println("TSQ Sent!");
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
      System.out.println(e);
    } 
  }
}
