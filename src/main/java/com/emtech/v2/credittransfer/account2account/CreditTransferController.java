package com.emtech.v2.credittransfer.account2account;

import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.ISO8583Finacle.PrincipalTransferResponse;
import com.emtech.v2.ISO8583Finacle.Transfer;
import com.emtech.v2.bulkprocessing.BulkCallback;
import com.emtech.v2.bulkprocessing.BulkProcessingController;
import com.emtech.v2.credittransfer.ToolKit;
import com.emtech.v2.credittransfer.account2account.pacs002;
import com.emtech.v2.credittransfer.account2account.pacs008;
import com.emtech.v2.tsq.TSQResponse;
import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.MessageResponse;
import com.google.gson.Gson;
import iso.std.iso._20022.tech.xsd.pacs_002_001.Document;
import iso.std.iso._20022.tech.xsd.pacs_002_001.OriginalGroupHeader17;
import iso.std.iso._20022.tech.xsd.pacs_002_001.PaymentTransaction123;
import iso.std.iso._20022.tech.xsd.pacs_002_001.StatusReasonInformation12;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import okhttp3.OkHttpClient;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/"})
public class CreditTransferController {
    private static final Logger log = LoggerFactory.getLogger(CreditTransferController.class);

    pacs008 p8 = new pacs008();

    pacs002 p2 = new pacs002();

    Transfer tr = new Transfer();

    static Configurations cn = new Configurations();

    static String updatequery = "";

    ToolKit tk = new ToolKit();

    String host = cn.getProperties().getProperty("fin.host");

    int port = Integer.parseInt(cn.getProperties().getProperty("fin.port"));

    String xmlpackager = cn.getProperties().getProperty("fin.xml");

    String tsqinsert = cn.getProperties().getProperty("pesa.tsq.query.insert");

    @Autowired
    private BulkProcessingController bulkProcessingController;

    @RequestMapping(value = {"send-credit-transfer/{benbank}/{amount}/{debaccnt}/{narration}/{credaccnt}"}, produces = {"application/json"})
    public ResponseEntity<?> sendCreditTransfer(OkHttpClient client, @PathVariable("benbank") String benbank, @PathVariable("amount") String amount, @PathVariable("debaccnt") String debaccnt, @PathVariable("narration") String narration, @PathVariable("credaccnt") String credaccnt) throws Exception {
        String refNum = this.tk.generateString().toUpperCase();
        this.p8.sendPACS008(client, benbank, amount, debaccnt, narration, credaccnt, refNum);
        return ResponseEntity.ok(new MessageResponse("Credit Transfer of Amount Ksh. " + amount + " From A/C : " + debaccnt + " To A/C : " + credaccnt + " Bank Code : " + benbank + " initiated successfully!"));
    }

    @RequestMapping(value = {"payment-status-report"}, consumes = {"application/xml", "application/json"}, produces = {"application/xml", "application/json"})
    public ResponseEntity<?> sendPaymentStatus(@RequestBody String xml) throws Exception {
        log.info("Parse Pacs002 XML");
        log.info("-------------------Receiving  Credit transfer callback xml\n" + xml);
        JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader XmlreaderObj = new StringReader(xml);
        Source source = new StreamSource(XmlreaderObj);
        JAXBElement<Document> root = unmarshaller.unmarshal(source, Document.class);
        Document p2 = root.getValue();
        String OriginatorBank = p2.getFIToFIPmtStsRpt().getGrpHdr().getInstdAgt().getFinInstnId().getOthr().getId();
        String OrgnlMsgId = p2.getFIToFIPmtStsRpt().getOrgnlGrpInfAndSts().get(0).getOrgnlMsgId();
        String MsgId = p2.getFIToFIPmtStsRpt().getGrpHdr().getMsgId();
        String TxnStatus = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getTxSts();
        String Amount = String.valueOf(p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getIntrBkSttlmAmt().getValue());
        String CreditorName = "-";
        if (p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getCdtr().getPty().getNm() != null)
            CreditorName = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getCdtr().getPty().getNm();
        String senderName = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getDbtr().getPty().getNm();
        String remarks = "Successful";
        if (!TxnStatus.equalsIgnoreCase("ACCP"))
            if (p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getStsRsnInf().get(0).getRsn().getCd() != null) {
                remarks = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getStsRsnInf().get(0).getRsn().getCd();
            } else {
                remarks = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getStsRsnInf().get(0).getRsn().getPrtry();
            }
        String Dr = "";
        String Cr = "";
        String endToEndId = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlEndToEndId();
        log.info("End to End ID - " + endToEndId);
        log.info("Transaction Status - " + TxnStatus);
        log.info("Recipient Name - " + CreditorName);
        log.info("Sender Name - " + senderName);
        log.info("Remarks - " + remarks);
        log.info("Originator Bank - " + OriginatorBank);
        log.info("Message ID - " + MsgId);
        List<BulkCallback> bulkCallbackList = new ArrayList<>();
        if (endToEndId.contains("BULK")) {
            System.out.println("Sending Request to PesalinkBulk.jar to update transaction status - " + new Date());
            BulkCallback bc = new BulkCallback(MsgId, OrgnlMsgId, TxnStatus, endToEndId, CreditorName, senderName, remarks);
            bulkCallbackList.add(bc);
            log.info("BulkCallback Single {}", bulkCallbackList.size());
            this.bulkProcessingController.sendCallBackToBUlk(bulkCallbackList);
        }
        String BeneficiaryBank = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getCdtrAgt().getFinInstnId().getOthr().getId();
        log.info("Beneficiary Bank {}", BeneficiaryBank);
        log.info("Original MsgId {}",OrgnlMsgId);

        boolean exists = DbMethods.findDuplicate("SELECT CreDtTm FROM CreditTransfer WHERE MsgId=?", 1, OrgnlMsgId);
        String trantype = DbMethods.getValue("SELECT TranType FROM CreditTransfer WHERE MsgId=?", 1, 1, OrgnlMsgId);
        String pacscode = DbMethods.getValue("SELECT Pacs002Code FROM CreditTransfer WHERE MsgId=?", 1, 1, OrgnlMsgId);

        if (exists && !pacscode.equalsIgnoreCase("NA")) {
            log.info("Exists...Passcode {}",pacscode);
            log.info(".....TransType ..{} ",trantype);
            if (trantype.equalsIgnoreCase("A2A")) {
                Cr = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getCdtrAcct().getId().getOthr().getId();
                Dr = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getDbtrAcct().getId().getOthr().getId();
            } else {
                Cr = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getCdtr().getPty().getCtctDtls().getPhneNb();
                Dr = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getDbtrAcct().getId().getOthr().getId();
            }
            String details = OriginatorBank + "," + OriginatorBank + "," + BeneficiaryBank + "," + OrgnlMsgId + "," + TxnStatus + ",KES," + Amount + "," + Dr;
            DbMethods.dbWork(this.tsqinsert, 8, details);
            TSQResponse tr = new TSQResponse();
            tr.setMessageid(OrgnlMsgId);
            tr.setCode(TxnStatus);
            tr.setAmount(Amount);
            tr.setCurrency("KES");
            tr.setCreditAccount(Cr);
            tr.setDebitaccount(Dr);
            tr.setBeneficiarybank(BeneficiaryBank);
            tr.setOriginatorbank(OriginatorBank);
            Gson gs = new Gson();
            System.out.println("TSQ Response : \n" + gs.toJson(tr));
            return ResponseEntity.ok(new MessageResponse("TSQ Response : " + gs.toJson(tr)));
        }
        String Account = "";
        String data = "";
        String TranAmount = String.valueOf(p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getIntrBkSttlmAmt().getValue());
        String ChargeAmount = this.tr.getCharges(TranAmount);
        String Narration = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getRmtInf().getUstrd().get(0);
        String edp = DbMethods.getParamValue("KITS.EDUTY.PCNT");
        double double_edp = Double.parseDouble(edp);
        double chrg_amt = Double.parseDouble(ChargeAmount);
        Double excDutyAmt = chrg_amt * double_edp / 100.0D;
        String ExciseDuty = String.valueOf(excDutyAmt);
        System.out.println("Narration - " + Narration);
        System.out.println("Amount - " + TranAmount);
        System.out.println("Credited To - " + CreditorName);
        System.out.println("Sent From - " + senderName);
        System.out.println("OriginatorBank - " + OriginatorBank);
        PrincipalTransferResponse pr = new PrincipalTransferResponse();
        String PrincipalAmtFinResponse = "";
        String ChargeAmtFinResponse = "";
        String EdutyAmtFinResponse = "";
        String CreditTranFinResponse = "";
        String PrincipalReversalFinResponse = "";
        String ChargeReversalFinResponse = "";
        String EdutyReversalFinResponse = "";
        String CreditStan = "";
        if (OriginatorBank.equalsIgnoreCase("0054") && BeneficiaryBank.equalsIgnoreCase("0054")) {
            boolean msgidexistsincoming = DbMethods.findDuplicate("SELECT MsgId FROM CreditTransfer WHERE MsgId=? AND direction='Incoming'", 1, OrgnlMsgId);
            boolean msgidexistsoutgoing = DbMethods.findDuplicate("SELECT MsgId FROM CreditTransfer WHERE MsgId=? AND direction='Outgoing'", 1, OrgnlMsgId);
            updatequery = cn.getProperties().getProperty("pesa.query.update.pacs.credit");
            try {
                if (msgidexistsincoming) {
                    GenericPackager packager = new GenericPackager(new FileInputStream(this.xmlpackager));
                    this.tr.channel = (ISOChannel) new ASCIIChannel(this.host, this.port, (ISOPackager) packager);
                    if (TxnStatus.equalsIgnoreCase("ACCP")) {
                        System.out.println("\n\n...............................................................................................................\nCredit Customer's A/C for incoming : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
                        Account = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getCdtrAcct().getId().getOthr().getId();
                        pr = this.tr.creditCustomerAccount(TranAmount, Account, Narration, senderName);
                        CreditTranFinResponse = pr.getCode();
                        CreditStan = pr.getStan();
                        if (CreditTranFinResponse.equalsIgnoreCase("000")) {
                            System.out.println("Crediting of Account " + Account + " with Amount Ksh." + TranAmount + " Completed Successfully!");
                        } else {
                            System.out.println("Crediting of Account " + Account + " with Amount Ksh." + TranAmount + " Failed!");
                        }
                        data = CreditorName + "," + CreditorName + "," + TxnStatus + "," + CreditTranFinResponse + "," + CreditStan;
                        DbMethods.dbWork(updatequery, 5, data);
                        System.out.println("\n\n...............................................................................................................\nEnd of Credit Customer's A/C for incoming : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
                    } else if (TxnStatus.equalsIgnoreCase("RJCT")) {
                        System.out.println("\n\n...............................................................................................................\nStart Reversals : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
                        String Rsn = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getStsRsnInf().get(0).getRsn().getCd();
                        String data1 = "NS," + TxnStatus + "-" + Rsn + ",NS,NS,NS,,NS,NA,NA,NA," + OrgnlMsgId;
                        updatequery = cn.getProperties().getProperty("pesa.query.update.pacs");
                        DbMethods.dbWork(updatequery, 10, data1);
                        Account = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getDbtrAcct().getId().getOthr().getId();
                        String PrincipalStan = DbMethods.getValue("SELECT PrincipalStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                        String EdutyStan = DbMethods.getValue("SELECT EdutyStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                        String ChargeStan = DbMethods.getValue("SELECT ChargeStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                        String RefNum = DbMethods.getValue("SELECT RefNumber FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                        Account = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getDbtrAcct().getId().getOthr().getId();
                        PrincipalReversalFinResponse = this.tr.ReversePricipalAmount(Amount, Account, PrincipalStan, RefNum);
                        ChargeReversalFinResponse = this.tr.ReverseChargeAmount(ChargeAmount, Account, ChargeStan, RefNum);
                        EdutyReversalFinResponse = this.tr.ReverseEdutyAmount(ExciseDuty, Account, EdutyStan, RefNum);
                        String revdata = PrincipalReversalFinResponse + "," + PrincipalReversalFinResponse + "," + ChargeReversalFinResponse + ",Y," + EdutyReversalFinResponse;
                        updatequery = cn.getProperties().getProperty("pesa.query.update.reversal");
                        DbMethods.dbWork(updatequery, 5, revdata);
                        System.out.println("\n\n...............................................................................................................\nEnd of Reversals : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
                    }
                }
                if (msgidexistsoutgoing) {
                    data = CreditorName + "," + CreditorName + ",NA," + TxnStatus + "," + CreditStan;
                    DbMethods.dbWork(updatequery, 5, data);
                }
            } catch (ISOException e) {
                return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
            } catch (SQLException e) {
                return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
            } catch (FileNotFoundException e) {
                return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
            }
        } else if (OriginatorBank.equalsIgnoreCase("0054") && !BeneficiaryBank.equalsIgnoreCase("0054")) {
            if (TxnStatus.equalsIgnoreCase("ACCP")) {
                System.out.println("\n\n...............................................................................................................\nUpdate DB Status for Successful transfer Outgoing : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
                CreditTranFinResponse = "NA";
                data = CreditorName + "," + CreditorName + "," + TxnStatus + ",NA," + CreditTranFinResponse;
                updatequery = cn.getProperties().getProperty("pesa.query.update.pacs.credit");
                DbMethods.dbWork(updatequery, 5, data);
                System.out.println("\n\n...............................................................................................................\nEnd of Database Update DB Status for Successful transfer Outgoing : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
            } else if (TxnStatus.equalsIgnoreCase("RJCT")) {
                System.out.println("\n\n...............................................................................................................\nStart Reversals : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
                String Rsn = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getStsRsnInf().get(0).getRsn().getCd();
                String data1 = "NS," + TxnStatus + "-" + Rsn + ",NS,NS,NS,,NS,NA,NA,NA," + OrgnlMsgId;
                updatequery = cn.getProperties().getProperty("pesa.query.update.pacs");
                DbMethods.dbWork(updatequery, 10, data1);
                try {
                    GenericPackager packager = new GenericPackager(new FileInputStream(this.xmlpackager));
                    this.tr.channel = (ISOChannel) new ASCIIChannel(this.host, this.port, (ISOPackager) packager);
                    Account = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getDbtrAcct().getId().getOthr().getId();
                    String PrincipalStan = DbMethods.getValue("SELECT PrincipalStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                    String EdutyStan = DbMethods.getValue("SELECT EdutyStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                    String ChargeStan = DbMethods.getValue("SELECT ChargeStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                    String RefNum = DbMethods.getValue("SELECT RefNumber FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                    Account = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getDbtrAcct().getId().getOthr().getId();
                    PrincipalReversalFinResponse = this.tr.ReversePricipalAmount(Amount, Account, PrincipalStan, RefNum);
                    ChargeReversalFinResponse = this.tr.ReverseChargeAmount(ChargeAmount, Account, ChargeStan, RefNum);
                    EdutyReversalFinResponse = this.tr.ReverseEdutyAmount(ExciseDuty, Account, EdutyStan, RefNum);
                    String revdata = PrincipalReversalFinResponse + "," + PrincipalReversalFinResponse + "," + ChargeReversalFinResponse + ",Y," + EdutyReversalFinResponse;
                    updatequery = cn.getProperties().getProperty("pesa.query.update.reversal");
                    DbMethods.dbWork(updatequery, 5, revdata);
                } catch (ISOException e) {
                    return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
                } catch (SQLException e) {
                    return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
                } catch (FileNotFoundException e) {
                    return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
                }
                System.out.println("\n\n...............................................................................................................\nEnd of Reversals : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
            }
        } else if (!OriginatorBank.equalsIgnoreCase("0054") && BeneficiaryBank.equalsIgnoreCase("0054")) {
            try {
                if (TxnStatus.equalsIgnoreCase("ACCP")) {
                    updatequery = cn.getProperties().getProperty("pesa.query.update.pacs.credit");
                    Account = p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getOrgnlTxRef().getCdtrAcct().getId().getOthr().getId();
                    GenericPackager packager = new GenericPackager(new FileInputStream(this.xmlpackager));
                    this.tr.channel = (ISOChannel) new ASCIIChannel(this.host, this.port, (ISOPackager) packager);
                    pr = this.tr.creditCustomerAccount(TranAmount, Account, Narration, senderName);
                    CreditTranFinResponse = pr.getCode();
                    if (CreditTranFinResponse.equalsIgnoreCase("000")) {
                        System.out.println("Crediting of Account " + Account + " with Amount Ksh." + TranAmount + " Completed Successfully!");
                    } else {
                        System.out.println("Crediting of Account " + Account + " with Amount Ksh." + TranAmount + " Failed!");
                    }
                    data = CreditorName + "," + CreditorName + "," + TxnStatus + "," + CreditTranFinResponse + "," + CreditStan;
                    DbMethods.dbWork(updatequery, 5, data);
                } else if (TxnStatus.equalsIgnoreCase("RJCT")) {
                    System.out.println("\n\n...............................................................................................................\nStart Reversals : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
                    String PrincipalStan = DbMethods.getValue("SELECT PrincipalStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                    String EdutyStan = DbMethods.getValue("SELECT EdutyStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                    String ChargeStan = DbMethods.getValue("SELECT ChargeStan FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                    String RefNum = DbMethods.getValue("SELECT RefNumber FROM CreditTransfer WHERE MsgId = ?", 1, 1, OrgnlMsgId);
                    Account = ((PaymentTransaction123) p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)).getOrgnlTxRef().getDbtrAcct().getId().getOthr().getId();
                    PrincipalReversalFinResponse = this.tr.ReversePricipalAmount(Amount, Account, PrincipalStan, RefNum);
                    ChargeReversalFinResponse = this.tr.ReverseChargeAmount(ChargeAmount, Account, ChargeStan, RefNum);
                    EdutyReversalFinResponse = this.tr.ReverseEdutyAmount(ExciseDuty, Account, EdutyStan, RefNum);
                    String Rsn = ((StatusReasonInformation12) ((PaymentTransaction123) p2.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)).getStsRsnInf().get(0)).getRsn().getCd();
                    String data1 = "NS," + TxnStatus + "-" + Rsn + ",NS,NS,NS,,NS,NA,NA,NA," + OrgnlMsgId;
                    updatequery = cn.getProperties().getProperty("pesa.query.update.pacs");
                    DbMethods.dbWork(updatequery, 10, data1);
                    String revdata = PrincipalReversalFinResponse + "," + PrincipalReversalFinResponse + "," + ChargeReversalFinResponse + ",Y," + EdutyReversalFinResponse;
                    updatequery = cn.getProperties().getProperty("pesa.query.update.reversal");
                    DbMethods.dbWork(updatequery, 5, revdata);
                    System.out.println("\n\n...............................................................................................................\nEnd of Reversals : Message ID :: " + OrgnlMsgId + "\n...............................................................................................................\n\n");
                }
            } catch (ISOException e) {
                return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
            } catch (SQLException e) {
                return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
            } catch (FileNotFoundException e) {
                return ResponseEntity.ok(new MessageResponse("Error :: " + e.getLocalizedMessage()));
            }
        }
        return ResponseEntity.ok(new MessageResponse("Successfully Processed PACS 002 For Credit Transfer From Bank : " + OriginatorBank + " A/C : " + Dr + " To A/C : " + Cr + " At Bank : " + BeneficiaryBank + " Amount Ksh. " + Amount));
    }

    @RequestMapping(value = {"credit-transfer"}, consumes = {"application/xml", "application/json"}, produces = {"application/xml", "application/json"})
    public String receiveCreditTransfer(@RequestBody String xml, OkHttpClient client) throws Exception {
        log.info("------Receiving incoming Credit Transfer Request-------------");
        System.out.println(xml);
        this.p2.sendPACS002(client, xml);
        return xml;
    }
}
