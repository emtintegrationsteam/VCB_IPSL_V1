package com.emtech.v2.hakikisha;

import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.hakikisha.AccountDetails;
import com.emtech.v2.hakikisha.GenEnveloped;
import com.emtech.v2.utilities.AccntDetailsResponse;
import com.emtech.v2.utilities.AddCertificates;
import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.DatabaseMethods;
import iso.std.iso._20022.tech.xsd.acmt_023_001.Document;
import iso.std.iso._20022.tech.xsd.acmt_023_001.IdentificationVerification2;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class acmt24 {
    private static final Logger log = LoggerFactory.getLogger(com.emtech.v2.hakikisha.acmt24.class);

    static Configurations cn = new Configurations();

    static String key = cn.getProperties().getProperty("pesa.sign.keys");

    static String seal = cn.getProperties().getProperty("pesa.sign.seal");

    static String acmt24url = cn.getProperties().getProperty("pesa.url.acmt24");

    static String outputacmt24 = cn.getProperties().getProperty("pesa.output.acmt24");

    static String insertquery = cn.getProperties().getProperty("pesa.query.insert.hakikisha.account");

    static String updatequery = cn.getProperties().getProperty("pesa.query.update.hakikisha.account");

    String dormancyuery = cn.getProperties().getProperty("pesa.query.checkdormancy").trim();

    String statusquery = cn.getProperties().getProperty("pesa.query.checkopenstatus").trim();

    static String reqlogfile = cn.getProperties().getProperty("pesa.logs.acmt24.txt");

    AddCertificates ac = new AddCertificates();

    public void sendACMT24(OkHttpClient oclient, String xml) throws Exception {
        JAXBContext jbc = JAXBContext.newInstance(new Class[]{Document.class});
        Unmarshaller um = jbc.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        Source src = new StreamSource(reader);
        JAXBElement<Document> rt = um.unmarshal(src, Document.class);
        Document a23 = rt.getValue();
        String MsgId = a23.getIdVrfctnReq().getAssgnmt().getMsgId();
        String CreDtTm = String.valueOf(a23.getIdVrfctnReq().getAssgnmt().getCreDtTm());
        String OriginalPIC = a23.getIdVrfctnReq().getAssgnmt().getFrstAgt().getFinInstnId().getOthr().getId();
        String IPSPIC = a23.getIdVrfctnReq().getAssgnmt().getAssgne().getAgt().getFinInstnId().getOthr().getId();
        String VrfctnId = a23.getIdVrfctnReq().getVrfctn().get(0).getId();
        String AcctToVerify = a23.getIdVrfctnReq().getVrfctn().get(0).getPtyAndAcctId().getAcct().getOthr().getId();
        String BeneficiaryBank = a23.getIdVrfctnReq().getVrfctn().get(0).getPtyAndAcctId().getAgt().getFinInstnId().getOthr().getId();
        boolean VrfctnStatus = false;
        String VrfctnCode = "NA";
        String AccountName = "NA";
        String data = MsgId + "," + MsgId + "," + CreDtTm + "," + OriginalPIC + "," + IPSPIC + "," + VrfctnId + "," + AcctToVerify + "," + BeneficiaryBank + "," + VrfctnStatus + "," + VrfctnCode;
        DbMethods.dbWork(insertquery, 10, data);
        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{Document.class});
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader XmlreaderObj = new StringReader(xml);
        Source source = new StreamSource(XmlreaderObj);
        JAXBElement<Document> root = unmarshaller.unmarshal(source, Document.class);
        Document a3 = root.getValue();
        String messageid = a3.getIdVrfctnReq().getAssgnmt().getMsgId();
        String CreditTime = a3.getIdVrfctnReq().getAssgnmt().getCreDtTm().toString();
        String originatorbank = a3.getIdVrfctnReq().getAssgnmt().getFrstAgt().getFinInstnId().getOthr().getId();
        String originalmsgid = a3.getIdVrfctnReq().getVrfctn().get(0).getId();
        String accountno = a3.getIdVrfctnReq().getVrfctn().get(0).getPtyAndAcctId().getAcct().getOthr().getId();
        GenEnveloped ge = new GenEnveloped();
        OkHttpClient client = this.ac.addCertificates(oclient);
        Random rn = new Random();
        String unique = String.valueOf(rn.nextInt(999000001) + 2);
        String date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
        String xmlacmt24 = "";
        String rejectcode = "AC01";
        AccountDetails ad = new AccountDetails();
        AccntDetailsResponse res = ad.getAccountDetails(accountno);
        log.info("----:{} Account Name : {} ", accountno, res.getAccountname());
        boolean Vrfctn = false;
        if (!res.isStatus()) {
            xmlacmt24 = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:acmt.024.001.02\">\n    <IdVrfctnRpt>\n        <Assgnmt>\n            <MsgId>" + unique + "</MsgId>\n            <CreDtTm>" + date + "</CreDtTm>\n            <FrstAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>0054</Id>\n                    </Othr>\n                </FinInstnId>\n            </FrstAgt>\n            <Assgnr>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>0054</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </Assgnr>\n            <Assgne>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>9999</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </Assgne>\n        </Assgnmt>\n        <OrgnlAssgnmt>\n            <MsgId>" + messageid + "</MsgId>\n            <CreDtTm>" + CreditTime + "</CreDtTm>\n            <FrstAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + originatorbank + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </FrstAgt>\n        </OrgnlAssgnmt>\n        <Rpt>\n            <OrgnlId>" + originalmsgid + "</OrgnlId>\n            <Vrfctn>" + Vrfctn + "</Vrfctn>\n       <Rsn> \n            <Cd>" + rejectcode + "</Cd>\n        </Rsn>            <OrgnlPtyAndAcctId>\n                <Acct>\n                    <Othr>\n                        <Id>" + accountno + "</Id>\n                    </Othr>\n                </Acct>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>0054</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </OrgnlPtyAndAcctId>\n        </Rpt>\n    </IdVrfctnRpt>\n</Document>";
        } else {
            String accounts = accountno + "," + accountno;
            String dormancy = DatabaseMethods.selectValues(this.dormancyuery, 1, 2, accounts).trim();
            String status = DatabaseMethods.selectValues(this.statusquery, 1, 1, accountno).trim();
            if (dormancy.trim().equalsIgnoreCase("A") && status.trim().equalsIgnoreCase("N")) {
                String accountname = res.getAccountname();
                Vrfctn = true;
                xmlacmt24 = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:acmt.024.001.02\">\n    <IdVrfctnRpt>\n        <Assgnmt>\n            <MsgId>" + unique + "</MsgId>\n            <CreDtTm>" + date + "</CreDtTm>\n            <FrstAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>0054</Id>\n                    </Othr>\n                </FinInstnId>\n            </FrstAgt>\n            <Assgnr>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>0054</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </Assgnr>\n            <Assgne>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>9999</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </Assgne>\n        </Assgnmt>\n        <OrgnlAssgnmt>\n            <MsgId>" + messageid + "</MsgId>\n            <CreDtTm>" + CreditTime + "</CreDtTm>\n            <FrstAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + originatorbank + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </FrstAgt>\n        </OrgnlAssgnmt>\n        <Rpt>\n            <OrgnlId>" + originalmsgid + "</OrgnlId>\n            <Vrfctn>" + Vrfctn + "</Vrfctn>\n            <OrgnlPtyAndAcctId>\n                <Acct>\n                    <Othr>\n                        <Id>" + accountno + "</Id>\n                    </Othr>\n                </Acct>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>0054</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </OrgnlPtyAndAcctId>\n            <UpdtdPtyAndAcctId>\n                <Pty>\n                    <Nm>" + accountname + "</Nm>\n                </Pty>\n                <Acct>\n                    <Othr>\n                        <Id>" + accountno + "</Id>\n                    </Othr>\n                </Acct>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>0054</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </UpdtdPtyAndAcctId>\n        </Rpt>\n    </IdVrfctnRpt>\n</Document>";
            } else {
                if (dormancy.trim().equalsIgnoreCase("D") || status.trim().equalsIgnoreCase("Y"))
                    rejectcode = "AC04";
                xmlacmt24 = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:acmt.024.001.02\">\n    <IdVrfctnRpt>\n        <Assgnmt>\n            <MsgId>" + unique + "</MsgId>\n            <CreDtTm>" + date + "</CreDtTm>\n            <FrstAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>0054</Id>\n                    </Othr>\n                </FinInstnId>\n            </FrstAgt>\n            <Assgnr>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>0054</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </Assgnr>\n            <Assgne>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>9999</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </Assgne>\n        </Assgnmt>\n        <OrgnlAssgnmt>\n            <MsgId>" + messageid + "</MsgId>\n            <CreDtTm>" + CreditTime + "</CreDtTm>\n            <FrstAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + originatorbank + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </FrstAgt>\n        </OrgnlAssgnmt>\n        <Rpt>\n            <OrgnlId>" + originalmsgid + "</OrgnlId>\n            <Vrfctn>" + Vrfctn + "</Vrfctn>\n       <Rsn> \n            <Cd>" + rejectcode + "</Cd>\n        </Rsn>            <OrgnlPtyAndAcctId>\n                <Acct>\n                    <Othr>\n                        <Id>" + accountno + "</Id>\n                    </Othr>\n                </Acct>\n                <Agt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>0054</Id>\n                        </Othr>\n                    </FinInstnId>\n                </Agt>\n            </OrgnlPtyAndAcctId>\n        </Rpt>\n    </IdVrfctnRpt>\n</Document>";
            }
        }
        try {
            String signed = ge.genEnveloped(xmlacmt24, outputacmt24, key, seal);
            MediaType mediaType = MediaType.parse("application/xml;charset=UTF-8");
            RequestBody body = RequestBody.create(mediaType, signed);
            Request request = (new Request.Builder()).url(acmt24url).post(body).addHeader("Content-Type", "application/xml;charset=UTF-8").build();
            Response response = client.newCall(request).execute();
            try {
                if (!response.isSuccessful()) {
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++)
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.name(i));
                    throw new IOException("Error " + response);
                }
                String accountName =
                        res.getAccountname() == null ? "" : res.getAccountname();
                String updatedata = ""+res.isStatus()+","+rejectcode+","+res.getAccountname()+","+originalmsgid;
                DbMethods.dbWork(updatequery,4,updatedata);
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
