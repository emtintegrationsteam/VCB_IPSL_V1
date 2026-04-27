package com.emtech.v2.credittransfer.account2account;


import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.ISO8583Finacle.Transfer;
import com.emtech.v2.credittransfer.ToolKit;
import com.emtech.v2.hakikisha.AccountDetails;
import com.emtech.v2.hakikisha.GenEnveloped;
import com.emtech.v2.hakikisha.acmt23;
import com.emtech.v2.utilities.AccntDetailsResponse;
import com.emtech.v2.utilities.AddCertificates;
import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.DatabaseMethods;
import iso.std.iso._20022.tech.xsd.pacs_008_001.CreditTransferTransaction43;
import iso.std.iso._20022.tech.xsd.pacs_008_001.Document;
import iso.std.iso._20022.tech.xsd.pacs_008_001.ServiceLevel8Choice;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
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

public class pacs002 {
    private static final Logger log = LoggerFactory.getLogger(com.emtech.v2.credittransfer.account2account.pacs002.class);

    static Configurations cn = new Configurations();

    static String pacs002url = cn.getProperties().getProperty("pesa.url.pacs002");

    static String outputpacs002 = cn.getProperties().getProperty("pesa.output.pacs002");

    static String key = cn.getProperties().getProperty("pesa.sign.keys");

    static String seal = cn.getProperties().getProperty("pesa.sign.seal");

    static String insertquery = cn.getProperties().getProperty("pesa.query.insert.pacs");

    Transfer tr = new Transfer();

    String select_phoneno = cn.getProperties().getProperty("stmt.api.sql.phoneno").trim();

    String detailsQuery = cn.getProperties().getProperty("stmt.api.sql.accountdetails").trim();

    String dormancyuery = cn.getProperties().getProperty("pesa.query.checkdormancy").trim();

    String statusquery = cn.getProperties().getProperty("pesa.query.checkopenstatus").trim();

    static String reqlogfile = cn.getProperties().getProperty("pesa.logs.pacs002.txt");

    AddCertificates ac = new AddCertificates();

    ToolKit tk = new ToolKit();

    acmt23 a23 = new acmt23();

    public void sendPACS002(OkHttpClient oclient, String pacs008xml) throws Exception {
        GenEnveloped ge = new GenEnveloped();
        Random rn = new Random();
        String unique = String.valueOf(rn.nextInt(999999901) + 2);
        String date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
        String AccptncDtTm = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{Document.class});
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader XmlreaderObj = new StringReader(pacs008xml);
        Source source = new StreamSource(XmlreaderObj);
        JAXBElement<Document> root = unmarshaller.unmarshal(source, Document.class);
        Document p8 = (Document) root.getValue();
        String OrgnlMsgId = p8.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId();
        String OrgnlMsgNmId = "pacs.008";
        String OrgnlCreDtTm = p8.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm().toString();
        String StsId = this.tk.generateString();
        String OrgnlEndToEndId = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getPmtId().getEndToEndId();
        String Ccy = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getIntrBkSttlmAmt().getCcy();
        String IntrBkSttlmAmt = String.valueOf(((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getIntrBkSttlmAmt().getValue());
        String SttlmMtd = p8.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getSttlmMtd().value();
        String ClrSys = p8.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getClrSys().getPrtry();
        String SvcLvl = ((ServiceLevel8Choice) p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getSvcLvl().get(0)).getPrtry();
        String LclInstrm = p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getLclInstrm().getCd();
        String CtgyPurp = p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getCtgyPurp().getPrtry();
        String Ustrd = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getRmtInf().getUstrd().get(0);
        String DbtrNm = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtr().getNm();
        String DbtrPhneNb = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtr().getCtctDtls().getPhneNb();
        String DbtrAcctNm = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtrAcct().getNm();
        String DbtrAgtId = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtrAgt().getFinInstnId().getOthr().getId();
        String CdtrAgtId = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getCdtrAgt().getFinInstnId().getOthr().getId();
        String CdtrAcctId = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getCdtrAcct().getId().getOthr().getId();
        String CdtrNm = "";
        String CdtrPhneNb = "";
        String CdtrAcctNm = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getCdtr().getNm();
        String TxSts = "";
        String PurpPrtry = "0001";
        String DbtrAcctId = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtrAcct().getId().getOthr().getId();
        String originbank = p8.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt().getFinInstnId().getOthr().getId();
        OkHttpClient client = this.ac.addCertificates(oclient);
        String xml = "";
        String CdtrId = "0054";
        AccountDetails ad = new AccountDetails();
        AccntDetailsResponse res = new AccntDetailsResponse();
        String dormancy = DatabaseMethods.selectValues(this.dormancyuery, 1, 1, CdtrAcctId).trim();
        String status = DatabaseMethods.selectValues(this.statusquery, 1, 1, CdtrAcctId).trim();
        res = ad.getAccountDetails(CdtrAcctId);
        System.out.println("this is the encoded Debitor name************************"+DbtrNm);
        DbtrNm = URLDecoder.decode(DbtrNm, StandardCharsets.UTF_8);
        DbtrNm = DbtrNm.replaceAll("[^a-zA-Z ]", "");
        DbtrNm = DbtrNm.replaceAll("\\s+", " ").trim();
        System.out.println("this is the Decoded Debitor name************************"+DbtrNm);
        if (res.isStatus() && status.equalsIgnoreCase("N") && ValidateAccountCurrency(CdtrAcctId)) {
            CdtrNm = DatabaseMethods.selectValues(this.detailsQuery, 1, 1, CdtrAcctId);
            //   CdtrNm = URLEncoder.encode(CdtrNm, StandardCharsets.UTF_8);
            System.out.println("this is the  Creditor name from the database************************"+CdtrNm);
            CdtrPhneNb = DatabaseMethods.selectValues(this.select_phoneno, 1, 1, CdtrAcctId);
            if (CdtrPhneNb == null || CdtrPhneNb.trim().isEmpty()) {
                CdtrPhneNb = "+254-709876100";  // Default phone number
                System.out.println("No phone number found for account " + CdtrAcctId + ", using default: " + CdtrPhneNb);
            } else {
                if (!CdtrPhneNb.contains("+")) {
                    CdtrPhneNb = "+" + CdtrPhneNb;
                }
                CdtrPhneNb = CdtrPhneNb.replace("(", "").replace(")", "");
                CdtrPhneNb = this.tk.insertString(CdtrPhneNb, "-", 3);  // Insert dash after first 3 characters
            }
            TxSts = "ACCP";
            System.out.println("this is the account details*****************************************" + CdtrNm);
            System.out.println("this is the account details*****************************************" +CdtrAcctNm);
            xml = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.002.001.11\">\n    <FIToFIPmtStsRpt>\n        <GrpHdr>\n            <MsgId>" + unique + "</MsgId> \n            <CreDtTm>" + date + "</CreDtTm>             <InstgAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>0054</Id>\n                    </Othr>\n                </FinInstnId>\n            </InstgAgt> \n            <InstdAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>9999</Id>\n                    </Othr>\n                </FinInstnId>\n            </InstdAgt>\n        </GrpHdr>\n        <OrgnlGrpInfAndSts> \n            <OrgnlMsgId>" + OrgnlMsgId + "</OrgnlMsgId> \n            <OrgnlMsgNmId>" + OrgnlMsgNmId + "</OrgnlMsgNmId> \n            <OrgnlCreDtTm>" + OrgnlCreDtTm + "</OrgnlCreDtTm>\n        </OrgnlGrpInfAndSts>\n        <TxInfAndSts> \n            <StsId>" + StsId + "</StsId> \n            <OrgnlEndToEndId>" + OrgnlEndToEndId + "</OrgnlEndToEndId>\n            <TxSts>" + TxSts + "</TxSts> \n            <AccptncDtTm>" + AccptncDtTm + "</AccptncDtTm> \n            <OrgnlTxRef> \n                <IntrBkSttlmAmt Ccy=\"" + Ccy + "\">" + IntrBkSttlmAmt + "</IntrBkSttlmAmt> \n                <ReqdExctnDt>\n                    <DtTm>" + AccptncDtTm + "</DtTm>\n                </ReqdExctnDt> \n                <SttlmInf>\n                    <SttlmMtd>" + SttlmMtd + "</SttlmMtd>\n                    <ClrSys>\n                        <Prtry>" + ClrSys + "</Prtry>\n                    </ClrSys>\n                </SttlmInf> \n                <PmtTpInf>\n                    <SvcLvl>\n                        <Prtry>" + SvcLvl + "</Prtry>\n                    </SvcLvl>\n                    <LclInstrm>\n                        <Cd>" + LclInstrm + "</Cd>\n                    </LclInstrm>\n                    <CtgyPurp>\n                        <Prtry>" + CtgyPurp + "</Prtry>\n                    </CtgyPurp>\n                </PmtTpInf> \n                <RmtInf>\n                    <Ustrd>" + Ustrd + "</Ustrd>\n                </RmtInf>\n                <Dbtr>\n                    <Pty>\n                        <Nm>" + DbtrNm + "</Nm>\n                        <CtctDtls>\n                            <PhneNb>" + DbtrPhneNb + "</PhneNb>\n                        </CtctDtls>\n                    </Pty>\n                </Dbtr> \n                <DbtrAcct>\n                    <Id>\n                        <Othr>\n                            <Id>" + DbtrAcctId + "</Id>\n                        </Othr>\n                    </Id>\n                </DbtrAcct> \n                <DbtrAgt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>" + DbtrAgtId + "</Id>\n                        </Othr>\n                    </FinInstnId>\n                </DbtrAgt> \n                <CdtrAgt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>" + CdtrAgtId + "</Id>\n                        </Othr>\n                    </FinInstnId>\n                </CdtrAgt> \n                <Cdtr>\n                    <Pty> \n                        <Nm>" + CdtrNm + "</Nm> \n                        <Id>\n                            <OrgId>\n                                <Othr>\n                                    <Id>" + CdtrId + "</Id>\n                                </Othr>\n                            </OrgId>\n                        </Id> \n                        <CtctDtls>\n                            <PhneNb>" + CdtrPhneNb + "</PhneNb>\n                        </CtctDtls>\n                    </Pty>\n                </Cdtr> \n                <CdtrAcct>\n                    <Id>\n                        <Othr>\n                            <Id>" + CdtrAcctId + "</Id>\n                        </Othr>\n                    </Id>\n                </CdtrAcct> \n                <Purp>\n                    <Prtry>" + PurpPrtry + "</Prtry>\n                </Purp>\n            </OrgnlTxRef>\n        </TxInfAndSts>\n    </FIToFIPmtStsRpt>\n</Document>";
        } else {
            TxSts = "RJCT";
            CdtrPhneNb = "+254-701665104";
            CdtrNm = "NA";
            xml = "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.002.001.11\">\n    <FIToFIPmtStsRpt>\n        <GrpHdr>\n            <MsgId>" + unique + "</MsgId> \n            <CreDtTm>" + date + "</CreDtTm>             <InstgAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>0054</Id>\n                    </Othr>\n                </FinInstnId>\n            </InstgAgt> \n            <InstdAgt>\n                <FinInstnId>\n                    <Othr>\n                        <Id>9999</Id>\n                    </Othr>\n                </FinInstnId>\n            </InstdAgt>\n        </GrpHdr>\n        <OrgnlGrpInfAndSts> \n            <OrgnlMsgId>" + OrgnlMsgId + "</OrgnlMsgId> \n            <OrgnlMsgNmId>" + OrgnlMsgNmId + "</OrgnlMsgNmId> \n            <OrgnlCreDtTm>" + OrgnlCreDtTm + "</OrgnlCreDtTm>\n        </OrgnlGrpInfAndSts>\n        <TxInfAndSts> \n            <StsId>" + StsId + "</StsId> \n            <OrgnlEndToEndId>" + OrgnlEndToEndId + "</OrgnlEndToEndId>\n            <TxSts>" + TxSts + "</TxSts> \n            <StsRsnInf>\n                <Orgtr>\n                    <Id>\n                        <OrgId>\n                            <Othr>\n\t\t\t\t<Id>0054</Id>\n                            </Othr>\n                        </OrgId>\n                    </Id>\n                </Orgtr>\n                <Rsn>\n                    <Cd>AC01</Cd>\n                </Rsn>\n            </StsRsnInf>\n            <AccptncDtTm>" + AccptncDtTm + "</AccptncDtTm> \n            <OrgnlTxRef> \n                <IntrBkSttlmAmt Ccy=\"" + Ccy + "\">" + IntrBkSttlmAmt + "</IntrBkSttlmAmt> \n                <ReqdExctnDt>\n                    <DtTm>" + AccptncDtTm + "</DtTm>\n                </ReqdExctnDt> \n                <SttlmInf>\n                    <SttlmMtd>" + SttlmMtd + "</SttlmMtd>\n                    <ClrSys>\n                        <Prtry>" + ClrSys + "</Prtry>\n                    </ClrSys>\n                </SttlmInf> \n                <PmtTpInf>\n                    <SvcLvl>\n                        <Prtry>" + SvcLvl + "</Prtry>\n                    </SvcLvl>\n                    <LclInstrm>\n                        <Cd>" + LclInstrm + "</Cd>\n                    </LclInstrm>\n                    <CtgyPurp>\n                        <Prtry>" + CtgyPurp + "</Prtry>\n                    </CtgyPurp>\n                </PmtTpInf> \n                <RmtInf>\n                    <Ustrd>" + Ustrd + "</Ustrd>\n                </RmtInf>\n                <Dbtr>\n                    <Pty>\n                        <Nm>" + DbtrNm + "</Nm>\n                        <CtctDtls>\n                            <PhneNb>" + DbtrPhneNb + "</PhneNb>\n                        </CtctDtls>\n                    </Pty>\n                </Dbtr>\n                <DbtrAcct>\n                    <Id>\n                        <Othr>\n                            <Id>" + DbtrAcctId + "</Id>\n                        </Othr>\n                    </Id>\n                </DbtrAcct> \n                <DbtrAgt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>" + DbtrAgtId + "</Id>\n                        </Othr>\n                    </FinInstnId>\n                </DbtrAgt> \n                <CdtrAgt>\n                    <FinInstnId>\n                        <Othr>\n                            <Id>" + CdtrAgtId + "</Id>\n                        </Othr>\n                    </FinInstnId>\n                </CdtrAgt> \n                <Cdtr>\n                    <Pty> \n                        <Nm>" + CdtrNm + "</Nm> \n                        <Id>\n                            <OrgId>\n                                <Othr>\n                                    <Id>" + CdtrId + "</Id>\n                                </Othr>\n                            </OrgId>\n                        </Id> \n                        <CtctDtls>\n                            <PhneNb>" + CdtrPhneNb + "</PhneNb>\n                        </CtctDtls>\n                    </Pty>\n                </Cdtr> \n                <CdtrAcct>\n                    <Id>\n                        <Othr>\n                            <Id>" + CdtrAcctId + "</Id>\n                        </Othr>\n                    </Id>\n                </CdtrAcct> \n                <Purp>\n                    <Prtry>" + PurpPrtry + "</Prtry>\n                </Purp>\n            </OrgnlTxRef>\n        </TxInfAndSts>\n    </FIToFIPmtStsRpt>\n</Document>";
        }
        System.out.println("-----------xml------------");
        System.out.println(xml);
        String MsgId = OrgnlMsgId;
        String CredDtTm = String.valueOf(p8.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm());
        String NbOfTxs = p8.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs();
        String ClrSysPrtry = p8.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getClrSys().getPrtry();
        String SttlmMtd1 = p8.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getSttlmMtd().value();
        String PmtTpInfSvcLvl = ((ServiceLevel8Choice) p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getSvcLvl().get(0)).getPrtry();
        String LclInstrm1 = p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getLclInstrm().getCd();
        String CtgyPurp1 = p8.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().getCtgyPurp().getPrtry();
        String OriginatorPIC = p8.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt().getFinInstnId().getOthr().getId();
        String IPSPIC = p8.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt().getFinInstnId().getOthr().getId();
        String EndToEndId = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getPmtId().getEndToEndId();
        String TranAmount = String.valueOf(((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getIntrBkSttlmAmt().getValue());
        String Currency = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getIntrBkSttlmAmt().getCcy();
        String AccptncDtTm1 = String.valueOf(((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getAccptncDtTm());
        String ChargeBearer = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getChrgBr().value();
        String DebtorName = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtr().getNm();
        String DebtorPhoneNo = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtr().getCtctDtls().getPhneNb();
        String DebitAccount = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getDbtrAcct().getId().getOthr().getId();
        String CreditorName = "NA";
        String CreditorAccount = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getCdtrAcct().getId().getOthr().getId();
        String BeneficiaryBank = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0)).getCdtrAgt().getFinInstnId().getOthr().getId();

        String rawNarration = ((CreditTransferTransaction43) p8.getFIToFICstmrCdtTrf()
                .getCdtTrfTxInf().get(0))
                .getRmtInf().getUstrd().get(0);

// Remove special characters and normalize spacing
        String Narration = rawNarration.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", " ").trim();

        String ChargeAmount = this.tr.getCharges(TranAmount);
        String edp = DbMethods.getParamValue("KITS.EDUTY.PCNT");
        double double_edp = Double.parseDouble(edp);
        double chrg_amt = Double.parseDouble(ChargeAmount);
        Double excDutyAmt = Double.valueOf(chrg_amt * double_edp / 100.0D);
        String ExciseDuty = String.valueOf(excDutyAmt);
        String Pacs002Code = "NA";
        String PrincipalAmtFinResponse = "NA";
        String ChargeAmtFinResponse = "NA";
        String EdutyAmtFinResponse = "NA";
        String TranType = "A2A";
        String CreditTranFinResponse = "";
        String direction = "Incoming";
        String CreditorPhoneNo = "NA";
        String PrincipalStan = "NA";
        String ChargesStan = "NA";
        String EdutyStan = "NA";
        String refNum = this.tk.generateString().toUpperCase();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String TSQMinutes = sdf.format(timestamp);
//        String data = MsgId + "," + MsgId + "," + CredDtTm + "," + NbOfTxs + "," + SttlmMtd1 + "," + ClrSysPrtry + "," + PmtTpInfSvcLvl + "," + LclInstrm1 + "," + CtgyPurp1 + "," + OriginatorPIC + "," + IPSPIC + "," + EndToEndId + "," + TranAmount + "," + Currency + "," + AccptncDtTm + "," + ChargeBearer + "," + DebtorName + "," + DebtorPhoneNo + "," + DebitAccount + "," + CreditorAccount + "," + CreditorName + "," + BeneficiaryBank + "," + Narration + "," + ChargeAmount + "," + ExciseDuty + "," + Pacs002Code + "," + PrincipalAmtFinResponse + "," + ChargeAmtFinResponse + "," + EdutyAmtFinResponse + "," + CreditTranFinResponse + "," + TranType + "," + CreditorPhoneNo + "," + direction + "," + TSQMinutes + "," + refNum + "," + PrincipalStan + "," + ChargesStan;
//        DbMethods.dbWork(insertquery, 36, data);
        String data = "" + MsgId + "," + CredDtTm + "," + NbOfTxs + "," + SttlmMtd1 + "," + ClrSysPrtry + "," + PmtTpInfSvcLvl + "," + LclInstrm1 + "," + CtgyPurp1 + "," + OriginatorPIC + "," + IPSPIC + "," +
                EndToEndId + "," + TranAmount + "," + Currency + "," + AccptncDtTm + "," + ChargeBearer + "," + DebtorName + "," + DebtorPhoneNo + "," + DebitAccount + "," + CreditorAccount + "," + CreditorName + "," +
                BeneficiaryBank + "," + Narration + "," + ChargeAmount + "," + ExciseDuty + "," + Pacs002Code + "," + PrincipalAmtFinResponse + "," + ChargeAmtFinResponse + "," + EdutyAmtFinResponse + "," + CreditTranFinResponse + "," +
                TranType + "," + CreditorPhoneNo + "," + direction + "," + TSQMinutes + "," + refNum + "," + PrincipalStan + "," + ChargesStan + "," + EdutyStan;
        DbMethods.dbWork(insertquery, 36, data);
        log.info("Debtor Name {}", DebtorName);
        log.info("Debit Account {}", DebitAccount);
        log.info("Credit Account {}", CreditorAccount);
        log.info("Creditor Name {}", CdtrNm);
     //   log.info("Decoded Debtor Name {}", URLDecoder.decode(DebtorName, StandardCharsets.UTF_8));
        try {
            String signed = ge.genEnveloped(xml, outputpacs002, key, seal);
            MediaType mediaType = MediaType.parse("application/xml;charset=UTF-8");
            RequestBody body = RequestBody.create(mediaType, signed);
            Request request = (new Request.Builder()).url(pacs002url).post(body).addHeader("Content-Type", "application/xml;charset=UTF-8").build();
            Response response = client.newCall(request).execute();
            try {
                if (!response.isSuccessful()) {
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++)
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.name(i));
                    throw new IOException("Error " + response);
                }
                System.out.println(response.body().string());
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
        log.info("-------------------------done------------------------");
    }
    public static boolean ValidateAccountCurrency(String Account) {
        String query = "SELECT ACCT_CRNCY_CODE FROM TBAADM.GAM WHERE FORACID = ?";
        String currency = DatabaseMethods.selectValues(query, 1, 1, Account).trim();
        boolean res = false;
        if (currency.equalsIgnoreCase("KES")) {
            res = true;
            return res;
        }
        log.info("Account Currency Not KES");
        res = false;
        return res;
    }
}
