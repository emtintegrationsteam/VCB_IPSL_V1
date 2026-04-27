package com.emtech.v2.credittransfer.account2account;

import com.emtech.v2.ISO8583Finacle.ChargesTransferResponse;
import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.ISO8583Finacle.EdutyTransferResponse;
import com.emtech.v2.ISO8583Finacle.PrincipalTransferResponse;
import com.emtech.v2.ISO8583Finacle.Transfer;
import com.emtech.v2.credittransfer.ToolKit;
import com.emtech.v2.hakikisha.AccountDetails;
import com.emtech.v2.hakikisha.GenEnveloped;
import com.emtech.v2.hakikisha.HakikishaResponse;
import com.emtech.v2.utilities.AccntDetailsResponse;
import com.emtech.v2.utilities.AddCertificates;
import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.DatabaseMethods;
import iso.std.iso._20022.tech.xsd.pacs_008_001.CreditTransferTransaction43;
import iso.std.iso._20022.tech.xsd.pacs_008_001.Document;
import iso.std.iso._20022.tech.xsd.pacs_008_001.ServiceLevel8Choice;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class pacs008 {
    private static final Logger log = LoggerFactory.getLogger(com.emtech.v2.credittransfer.account2account.pacs008.class);

    static Configurations cn = new Configurations();

    static String pacs008url = cn.getProperties().getProperty("pesa.url.pacs008");

    static String outputpacs008 = cn.getProperties().getProperty("pesa.output.pacs008");

    static String key = cn.getProperties().getProperty("pesa.sign.keys");

    static String seal = cn.getProperties().getProperty("pesa.sign.seal");

    static String insertquery = cn.getProperties().getProperty("pesa.query.insert.pacs");

    String select_phoneno = cn.getProperties().getProperty("stmt.api.sql.phoneno").trim();

    String host = cn.getProperties().getProperty("fin.host");

    int port = Integer.parseInt(cn.getProperties().getProperty("fin.port"));

    String xmlpackager = cn.getProperties().getProperty("fin.xml");

    static String reqlogfile = cn.getProperties().getProperty("pesa.logs.pacs008.acct2acct.txt");

    AddCertificates ac = new AddCertificates();

    ToolKit tk = new ToolKit();

    AccountDetails ad = new AccountDetails();

    Transfer tr = new Transfer();

    static String updatequery = "";

    public void sendPACS008(OkHttpClient oclient, String beneficiarybank, String amount, String debitaccount, String narration, String creditaccount, String refNum) throws Exception {
        GenEnveloped ge = new GenEnveloped();
        AccntDetailsResponse res = new AccntDetailsResponse();
        HakikishaResponse hr = new HakikishaResponse();
        res = this.ad.getAccountDetails(debitaccount);
        String debtorname = res.getAccountname();
        System.out.println("Sender Name { " + debtorname + " }");
        if (containsSpecialCharacters(debtorname)) {
            log.warn("Account Name Contains Special characters ");
            debtorname = URLEncoder.encode(debtorname, StandardCharsets.UTF_8);
            log.info("Encoded Ac Name {}", debtorname);
        }
        String PhneNb = DatabaseMethods.selectValues(this.select_phoneno, 1, 1, debitaccount);
        if (!PhneNb.contains("+"))
            PhneNb = "+" + PhneNb;
        PhneNb = PhneNb.replace("(", "");
        PhneNb = PhneNb.replace(")", "");
        PhneNb = this.tk.insertString(PhneNb, "-", 3);
        System.out.println("Sender Phone No { " + PhneNb + " }");
        Random rn = new Random();
        String unique = String.valueOf(rn.nextInt(999999001) + 2);
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
        String creditorname = hr.getName();
        String endtoendid = this.tk.generateEndToEndId(beneficiarybank);
        String CdtrRefInf = this.tk.generateString();
        xml = "<?xml version='1.0' encoding='UTF-8'?>\n<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09\">\n    <FIToFICstmrCdtTrf>\n        <GrpHdr>\n            <MsgId>" + unique + "</MsgId>\n            <CreDtTm>" + date + "</CreDtTm>\n            <NbOfTxs>1</NbOfTxs>\n            <SttlmInf>\n                <SttlmMtd>" + SttlmMtd + "</SttlmMtd>\n                <ClrSys>\n                    <Prtry>" + ClrSys + "</Prtry>\n                </ClrSys>\n            </SttlmInf>\n            <PmtTpInf>\n                <SvcLvl>\n                    <Prtry>" + trantype + "</Prtry>\n                </SvcLvl>\n                <LclInstrm>\n                    <Cd>" + LclInstrm + "</Cd>\n                </LclInstrm>\n                <CtgyPurp>\n                    <Prtry>" + CtgyPurp + "</Prtry>\n                </CtgyPurp>\n            </PmtTpInf>\n            <InstgAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + originatorbankcode + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </InstgAgt>\n            <InstdAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>9999</Id>\n                    </Othr>\n                </FinInstnId>\n            </InstdAgt>\n        </GrpHdr>\n        <CdtTrfTxInf>\n            <PmtId>\n                <EndToEndId>" + endtoendid + "</EndToEndId>\n            </PmtId>\n            <IntrBkSttlmAmt Ccy=\"KES\">" + amount + "</IntrBkSttlmAmt>\n            <AccptncDtTm>" + date + "</AccptncDtTm>\n            <ChrgBr>" + ChrgBr + "</ChrgBr>\n            <Dbtr>\n                <Nm>" + debtorname + "</Nm>\n                <CtctDtls>\n                    <PhneNb>" + PhneNb + "</PhneNb>\n                </CtctDtls>\n            </Dbtr>\n            <DbtrAcct>\n                <Id>\n                    <Othr>\n                        <Id>" + debitaccount + "</Id>\n                    </Othr>\n                </Id>\n            </DbtrAcct>\n            <DbtrAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + originatorbankcode + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </DbtrAgt>\n            <CdtrAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>" + beneficiarybank + "</Id>\n                    </Othr>\n                </FinInstnId>\n            </CdtrAgt>\n            <Cdtr>\n            </Cdtr>\n            <CdtrAcct>\n                <Id>\n                    <Othr>\n                        <Id>" + creditaccount + "</Id>\n                    </Othr>\n                </Id>\n            </CdtrAcct>\n            <Purp>\n                <Prtry>001</Prtry>\n            </Purp>\n            <RmtInf>\n                <Ustrd>" + narration + "</Ustrd>\n            </RmtInf>\n        </CdtTrfTxInf>\n    </FIToFICstmrCdtTrf>\n</Document>";
        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { Document.class });
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader XmlreaderObj = new StringReader(xml);
        Source source = new StreamSource(XmlreaderObj);
        JAXBElement<Document> root = unmarshaller.unmarshal(source, Document.class);
        Document p8 = (Document)root.getValue();
        String MsgId = p8.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId();
        String CredDtTm = String.valueOf(p8.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm());
        String NbOfTxs = p8.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs();
        String ClrSysPrtry = p8.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getClrSys().getPrtry();
        String SttlmMtd1 = p8.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getSttlmMtd().value();
        String PmtTpInfSvcLvl = ((ServiceLevel8Choice)p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getSvcLvl().get(0)).getPrtry();
        String LclInstrm1 = p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getLclInstrm().getCd();
        String CtgyPurp1 = p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getCtgyPurp().getPrtry();
        String OriginatorPIC = p8.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt().getFinInstnId().getOthr().getId();
        String IPSPIC = p8.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt().getFinInstnId().getOthr().getId();
        String EndToEndId = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getPmtId().getEndToEndId();
        String TranAmount = String.valueOf(((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getIntrBkSttlmAmt().getValue());
        String Currency = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getIntrBkSttlmAmt().getCcy();
        String AccptncDtTm = String.valueOf(((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getAccptncDtTm());
        String ChargeBearer = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getChrgBr().value();
        String DebtorName = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtr().getNm();
        String DebtorPhoneNo = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtr().getCtctDtls().getPhneNb();
        String DebitAccount = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtrAcct().getId().getOthr().getId();
        String CreditorName = "NA";
        String CreditorAccount = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getCdtrAcct().getId().getOthr().getId();
        String BeneficiaryBank = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getCdtrAgt().getFinInstnId().getOthr().getId();
        String Narration = ((CreditTransferTransaction43)p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getRmtInf().getUstrd().get(0);
        String ChargeAmount = this.tr.getCharges(TranAmount);
        String TranType = "A2A";
        String edp = DbMethods.getParamValue("KITS.EDUTY.PCNT");
        double double_edp = Double.parseDouble(edp);
        double chrg_amt = Double.parseDouble(ChargeAmount);
        Double excDutyAmt = Double.valueOf(chrg_amt * double_edp / 100.0D);
        String ExciseDuty = String.valueOf(excDutyAmt);
        String Pacs002Code = "NA";
        String PrincipalAmtFinResponse = "NA";
        String ChargeAmtFinResponse = "NA";
        String EdutyAmtFinResponse = "NA";
        String CreditTranFinResponse = "NA";
        String direction = "Outgoing";
        String CreditorPhoneNo = "NA";
        String PrincipalStan = "NA";
        String ChargesStan = "NA";
        String EdutyStan = "NA";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String TSQMinutes = sdf.format(timestamp);
        String data = MsgId + "," + MsgId + "," + CredDtTm + "," + NbOfTxs + "," + SttlmMtd1 + "," + ClrSysPrtry + "," + PmtTpInfSvcLvl + "," + LclInstrm1 + "," + CtgyPurp1 + "," + OriginatorPIC + "," + IPSPIC + "," + EndToEndId + "," + TranAmount + "," + Currency + "," + AccptncDtTm + "," + ChargeBearer + "," + DebtorName + "," + DebtorPhoneNo + "," + DebitAccount + "," + CreditorAccount + "," + CreditorName + "," + BeneficiaryBank + "," + Narration + "," + ChargeAmount + "," + ExciseDuty + "," + Pacs002Code + "," + PrincipalAmtFinResponse + "," + ChargeAmtFinResponse + "," + EdutyAmtFinResponse + "," + CreditTranFinResponse + "," + TranType + "," + CreditorPhoneNo + "," + direction + "," + TSQMinutes + "," + refNum + "," + PrincipalStan + "," + ChargesStan;
        System.out.println("Data to be inserted for outgoing tran");
        System.out.println(data);
        try {
            String signed = ge.genEnveloped(xml, outputpacs008, key, seal);
            System.out.println("Pacs 008 to be sent");
            System.out.println(signed);
            MediaType mediaType = MediaType.parse("application/xml;charset=UTF-8");
            RequestBody body = RequestBody.create(mediaType, signed);
            Request request = (new Request.Builder()).url(pacs008url).post(body).addHeader("Content-Type", "application/xml;charset=UTF-8").build();
            Response response = client.newCall(request).execute();
            try {
                if (!response.isSuccessful()) {
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++)
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.name(i));
                    throw new IOException("Error " + response);
                }
                DbMethods.dbWork(insertquery, 36, data);
                System.out.println("\n\n...............................................................................................................\nOutgoing Transaction (Account to Account) : Message ID :: " + MsgId + "\n...............................................................................................................\n\n");
                String TxnStatus = "NA";
                updatequery = cn.getProperties().getProperty("pesa.query.update.pacs");
                try {
                    GenericPackager packager = new GenericPackager(new FileInputStream(this.xmlpackager));
                    this.tr.channel = (ISOChannel)new ASCIIChannel(this.host, this.port, (ISOPackager)packager);
                    ChargesTransferResponse cr = new ChargesTransferResponse();
                    EdutyTransferResponse er = new EdutyTransferResponse();
                    PrincipalTransferResponse pr = new PrincipalTransferResponse();
                    pr = this.tr.sendPrincipalAmount(TranAmount, DebitAccount, Narration, refNum);
                    PrincipalAmtFinResponse = pr.getCode();
                    PrincipalStan = pr.getStan();
                    if (PrincipalAmtFinResponse.equalsIgnoreCase("000")) {
                        cr = this.tr.sendChargeAmount(ChargeAmount, DebitAccount, refNum);
                        ChargeAmtFinResponse = cr.getCode();
                        ChargesStan = cr.getStan();
                        er = this.tr.sendExciseDutyAmount(ExciseDuty, DebitAccount, refNum);
                        EdutyAmtFinResponse = er.getCode();
                        EdutyStan = er.getStan();
                        CreditTranFinResponse = "NS";
                        System.out.println("Successful Debits");
                        data = CreditorName + "," + CreditorName + "," + CreditorName + "," + TxnStatus + "," + PrincipalAmtFinResponse + "," + ChargeAmtFinResponse + "," + EdutyAmtFinResponse + "," + CreditTranFinResponse + "," + PrincipalStan + "," + ChargesStan;
                        DbMethods.dbWork(updatequery, 10, data);
                        System.out.println("\n\n...............................................................................................................\nEnd of Outgoing Transaction (Account to Account): Message ID :: " + MsgId + "\n\n...............................................................................................................\n\n\n");
                    } else {
                        System.out.println("Debiting of Customer Account - " + DebitAccount + " with Amount Ksh." + TranAmount + " Failed!");
                    }
                } catch (ISOException|java.sql.SQLException|java.io.FileNotFoundException e) {
                    System.out.println("Error :: " + e.getLocalizedMessage());
                }
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
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static boolean containsSpecialCharacters(String input) {
        String pattern = "[^a-zA-Z0-9]";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        return m.find();
    }
}
