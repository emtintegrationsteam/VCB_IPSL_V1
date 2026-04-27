package com.emtech.v2.hakikisha;

import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.utilities.AddCertificates;
import com.emtech.v2.utilities.Configurations;
import iso.std.iso._20022.tech.xsd.acmt_023_001.Document;
import iso.std.iso._20022.tech.xsd.acmt_023_001.IdentificationVerification2;
import iso.std.iso._20022.tech.xsd.acmt_024_001.VerificationReport2;
import okhttp3.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

public class acmt23 {
  static Configurations cn = new Configurations();
  
  static String acmt23url = cn.getProperties().getProperty("pesa.url.acmt23");
  
  static String outputacmt23 = cn.getProperties().getProperty("pesa.output.acmt23");
  
  static String key = cn.getProperties().getProperty("pesa.sign.keys");
  
  static String seal = cn.getProperties().getProperty("pesa.sign.seal");
  
  static String bcodes = cn.getProperties().getProperty("pesa.connected.banks");
  
  static String insertquery = cn.getProperties().getProperty("pesa.query.insert.hakikisha.account");
  
  static String updatequery = cn.getProperties().getProperty("pesa.query.update.hakikisha.account");
  
  static String reqlogfile = cn.getProperties().getProperty("pesa.logs.acmt23.txt");
  
  AddCertificates ac = new AddCertificates();
  
  public HakikishaResponse sendACMT23(OkHttpClient oclient, String bankcode, String accntno) throws Exception {
    GenEnveloped ge = new GenEnveloped();
    Random rn = new Random();
    String unique = String.valueOf(rn.nextInt(999999001) + 2);
    String random = String.valueOf(rn.nextInt(999999001) + 2);
    FileWriter writer = new FileWriter(reqlogfile, true);
    BufferedWriter bufferedWriter = new BufferedWriter(writer);
    OkHttpClient client = this.ac.addCertificates(oclient);
    HakikishaResponse hr = new HakikishaResponse();
    String date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
    String verid = "ACT";
    String[] bankcodes = bcodes.split(",");
    System.out.println("VALID CODES :: " + Arrays.toString((Object[])bankcodes));
    boolean isConnected = Stream.<String>of(bankcodes).anyMatch(x -> x.equals(bankcode));
    if (isConnected) {
      try {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:acmt.023.001.02\">\n    <IdVrfctnReq>\n\t\n        <Assgnmt>\n\t\t\n\t\t\t\n            <MsgId>" + unique + "</MsgId>\n\t\t\t\n\t\t\t\n            <CreDtTm>" + date + "</CreDtTm>\n\t\t\t\n\t\t\t\n            <FrstAgt>\n                <FinInstnId>\n                    <Othr>\n\t\t\t\t\t\t\n                        <Id>0054</Id> \n                    </Othr>\n                </FinInstnId>\n            </FrstAgt>\n\t\t\t\n\t\t\t\n            <Assgnr>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n\t\t\t\t\t\t\t\n                            <Id>0054</Id> \n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </Assgnr>\n\t\t\t\n\t\t\t\n            <Assgne>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n\t\t\t\t\t\t\t<!-- IPS PIC -->\n                            <Id>9999</Id> \n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </Assgne>\n\t\t\t\n        </Assgnmt>\n\t\t\n        <Vrfctn>\n\t\t\n\t\t\t\n            <Id>" + random + "</Id>\n\t\t\t\n\t\t\t\n            <PtyAndAcctId>\n\t\t\t\t\n\t\t\t\t\n                <Acct>\n                    <Othr>\n\t\t\t\t\t\t\n\t\t\t\t\t\t<Id>" + accntno + "</Id> \n\t\t\t\t\t\t\n\t\t\t\t\t\t\n\t\t\t\t\t\t<SchmeNm> \n\t\t\t\t\t\t\t<Prtry>" + verid + "</Prtry>\n\t\t\t\t\t\t</SchmeNm>\n                    </Othr>\n                </Acct>\n\t\t\t\t\n\t\t\t\t\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n\t\t\t\t\t\t\t\n                            <Id>" + bankcode + "</Id> \n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </PtyAndAcctId>\n\t\t\t\n        </Vrfctn>\n\t\t\n    </IdVrfctnReq>\n\t\n</Document>";
        JAXBContext jbc = JAXBContext.newInstance(new Class[] { iso.std.iso._20022.tech.xsd.acmt_023_001.Document.class });
        Unmarshaller um = jbc.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        Source src = new StreamSource(reader);
        JAXBElement<Document> rt = um.unmarshal(src, iso.std.iso._20022.tech.xsd.acmt_023_001.Document.class);
        Document a23 = (Document)rt.getValue();
        String MsgId = a23.getIdVrfctnReq().getAssgnmt().getMsgId();
        String CreDtTm = String.valueOf(a23.getIdVrfctnReq().getAssgnmt().getCreDtTm());
        String OriginalPIC = a23.getIdVrfctnReq().getAssgnmt().getFrstAgt().getFinInstnId().getOthr().getId();
        String IPSPIC = a23.getIdVrfctnReq().getAssgnmt().getAssgne().getAgt().getFinInstnId().getOthr().getId();
        String VrfctnId = ((IdentificationVerification2)a23.getIdVrfctnReq().getVrfctn().get(0)).getId();
        String AcctToVerify = ((IdentificationVerification2)a23.getIdVrfctnReq().getVrfctn().get(0)).getPtyAndAcctId().getAcct().getOthr().getId();
        String BeneficiaryBank = ((IdentificationVerification2)a23.getIdVrfctnReq().getVrfctn().get(0)).getPtyAndAcctId().getAgt().getFinInstnId().getOthr().getId();
        boolean VrfctnStatus = false;
        String VrfctnCode = "NA";
        String AccountName = "NA";
        String data = MsgId + "," + MsgId + "," + CreDtTm + "," + OriginalPIC + "," + IPSPIC + "," + VrfctnId + "," + AcctToVerify + "," + BeneficiaryBank + "," + VrfctnStatus + "," + VrfctnCode;
        DbMethods.dbWork(insertquery, 10, data);
        String signed = ge.genEnveloped(xml, outputacmt23, key, seal);
        bufferedWriter.newLine();
        bufferedWriter.write("****************************************************************************");
        bufferedWriter.newLine();
        bufferedWriter.write("ACMT 023 ACCOUNT HAKIKISHA REQUEST FOR ACCOUNT NO : " + accntno + " SENT AT " + date);
        bufferedWriter.newLine();
        bufferedWriter.write(signed);
        bufferedWriter.newLine();
        bufferedWriter.close();
        MediaType mediaType = MediaType.parse("application/xml;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, signed);
        Request request = (new Request.Builder()).url(acmt23url).post(body).addHeader("Content-Type", "application/xml;charset=UTF-8").build();
        Response response = client.newCall(request).execute();
        try {
          if (!response.isSuccessful()) {
            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++)
              System.out.println(responseHeaders.name(i) + ": " + responseHeaders.name(i)); 
            throw new IOException("Error " + response);
          } 
          String xmlacmt24 = response.body().string();
          FileWriter wt = new FileWriter(reqlogfile, true);
          BufferedWriter bw = new BufferedWriter(wt);
          bw.newLine();
          bw.write("****************************************************************************");
          bw.newLine();
          bw.write("ACMT 024 ACCOUNT HAKIKISHA RESPONSE FOR ACCOUNT NO : " + accntno + " RECEIVED AT " + date);
          bw.newLine();
          bw.write(xmlacmt24);
          bw.newLine();
          bw.close();
          JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { iso.std.iso._20022.tech.xsd.acmt_024_001.Document.class });
          Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
          StringReader XmlreaderObj = new StringReader(xmlacmt24);
          Source source = new StreamSource(XmlreaderObj);
          JAXBElement<iso.std.iso._20022.tech.xsd.acmt_024_001.Document> root = unmarshaller.unmarshal(source, iso.std.iso._20022.tech.xsd.acmt_024_001.Document.class);
          iso.std.iso._20022.tech.xsd.acmt_024_001.Document a24 = (iso.std.iso._20022.tech.xsd.acmt_024_001.Document)root.getValue();
          boolean status = ((VerificationReport2)a24.getIdVrfctnRpt().getRpt().get(0)).isVrfctn();
          String isss = a24.getIdVrfctnRpt().getAssgnmt().getFrstAgt().getFinInstnId().getOthr().getId();
          String OrinalMsId = ((VerificationReport2)a24.getIdVrfctnRpt().getRpt().get(0)).getOrgnlId();
          System.out.println("Status :: " + status);
          String code = "";
          String customername = "";
          if (!status) {
            code = ((VerificationReport2)a24.getIdVrfctnRpt().getRpt().get(0)).getRsn().getCd();
            customername = "NA";
          } else {
            code = "NA";
            customername = ((VerificationReport2)a24.getIdVrfctnRpt().getRpt().get(0)).getUpdtdPtyAndAcctId().getPty().getNm();
          } 
          String updatedata = "" + status + "," + status + "," + code + "," + customername;
          DbMethods.dbWork(updatequery, 4, updatedata);
          hr.setCode(code);
          hr.setStatus(status);
          hr.setName(customername);
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
    } else {
      System.out.println("Bank :: " + bankcode + " not connected to New Switch");
      hr.setCode("NCONNECTED");
      hr.setStatus(false);
      hr.setName("NCONNECTED");
    } 
    return hr;
  }
}
