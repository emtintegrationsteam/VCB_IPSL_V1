package com.emtech.v2.bulkprocessing;
import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.bulkprocessing.BulkCallback;
import com.emtech.v2.bulkprocessing.BulkPacs008;
import com.emtech.v2.bulkprocessing.Pesalink;
import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.DisableSSL;
import com.google.gson.Gson;
import iso.std.iso._20022.tech.xsd.pacs_002_001_bulk.Document;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/"})
public class BulkProcessingController {
  private static final Logger log = LoggerFactory.getLogger(BulkProcessingController.class);
  
  BulkPacs008 bulkPacs008 = new BulkPacs008();
  
  static String updatequery = "";
  
  static Configurations cn = new Configurations();
  
  @RequestMapping(value = {"bulk-processing"}, produces = {"application/json"})
  public ResponseEntity<?> sendBulkCreditTransfer(OkHttpClient client, @RequestBody List<Pesalink> pesalink) throws Exception {
    log.info("" + pesalink.size() + " Bulk Request Received - " + pesalink.size());
    log.info((new Gson()).toJson(((Pesalink)pesalink.get(0)).getOriginalMessageId()));
    return ResponseEntity.ok(this.bulkPacs008.sendBulkPACS008(client, pesalink));
  }
  
  @RequestMapping(value = {"payment-status-report-bulk"}, consumes = {"application/xml", "application/json"}, produces = {"application/xml", "application/json"})
  public ResponseEntity<?> sendBulkPaymentStatus(@RequestBody String xml) throws Exception {
    System.out.println("---------------Bulk Response Received ---------------------\n" + xml);
    JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { Document.class });
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    StringReader XmlreaderObj = new StringReader(xml);
    Source source = new StreamSource(XmlreaderObj);
    JAXBElement<Document> root = unmarshaller.unmarshal(source, Document.class);
    Document p2 = (Document)root.getValue();
    String OriginatorBank = String.valueOf(p2.getFIToFIPmtStsRpt().getGrpHdr().getInstgAgt().getFinInstnId().getOthr().getId());
    String OrgnlMsgId = String.valueOf(p2.getFIToFIPmtStsRpt().getOrgnlGrpInfAndSts().getOrgnlMsgId());
    String MsgId = String.valueOf(p2.getFIToFIPmtStsRpt().getGrpHdr().getMsgId());
    String TxnStatus = p2.getFIToFIPmtStsRpt().getOrgnlGrpInfAndSts().getGrpSts();
    log.info("--------GROUP STATUS----------");
    log.info("................{}............", TxnStatus);
    if (!TxnStatus.equalsIgnoreCase("ACCP")) {
      log.info("Request Rejected!");
    } else {
      log.info("Request Accepted!");
    } 
    List<Document.FIToFIPmtStsRpt.TxInfAndSts> txInfAndStsList = p2.getFIToFIPmtStsRpt().getTxInfAndSts();
    log.info("All transactions {}", Integer.valueOf(txInfAndStsList.size()));
    String remarks = "Successful";
    Integer tranNumber = Integer.valueOf(0);
    List<BulkCallback> bulkCallbackList = new ArrayList<>();
    String CreditorName = "-";
    for (Document.FIToFIPmtStsRpt.TxInfAndSts t : txInfAndStsList) {
      Integer integer1 = tranNumber, integer2 = tranNumber = Integer.valueOf(tranNumber.intValue() + 1);
      log.info("-------------------------Transaction -{}-------------------------", tranNumber);
      String txnStatus = t.getTxSts();
      String senderName = t.getOrgnlTxRef().getDbtr().getPty().getNm();
      CreditorName = t.getOrgnlTxRef().getCdtr().getPty().getNm();
      String endToEndId = t.getOrgnlEndToEndId();
      String BeneficiaryBank = String.valueOf(t.getOrgnlTxRef().getCdtrAgt().getFinInstnId().getOthr().getId());
      updatequery = cn.getProperties().getProperty("pesa.query.update.bulk.pacs");
      String data = "";
      if (!txnStatus.equalsIgnoreCase("ACCP")) {
        log.info("--------________TRANSACTION REJECTED____________----------");
        data = txnStatus + "," + txnStatus + "," + OrgnlMsgId;
        log.info("Request Rejected!");
        if (t.getStsRsnInf().getRsn().getCd() != null) {
          remarks = t.getStsRsnInf().getRsn().getCd();
        } else {
          remarks = t.getStsRsnInf().getRsn().getCd();
        } 
        log.info("Reason : {}", remarks);
      } else {
        log.info("--------________TRANSACTION ACCEPTED____________----------");
        remarks = "Successful";
        data = txnStatus + "," + txnStatus + "," + OrgnlMsgId;
      } 
      System.out.println("End to End ID - " + endToEndId);
      System.out.println("OriginalMessageId - " + OrgnlMsgId);
      System.out.println("MsgId - " + MsgId);
      System.out.println("Transaction Status - " + TxnStatus);
      System.out.println("Recipient Name - " + CreditorName);
      System.out.println("Sender Name - " + senderName);
      System.out.println("Remarks - " + remarks);
      System.out.println("BeneficiaryBank - " + BeneficiaryBank);
      log.info("Updating CreditTransfer table");
      DbMethods.dbWork(updatequery, 3, data);
      BulkCallback bc = new BulkCallback(MsgId, OrgnlMsgId, txnStatus, endToEndId, CreditorName, senderName, remarks);
      bulkCallbackList.add(bc);
    } 
    log.info("BulkCallback List {}", Integer.valueOf(bulkCallbackList.size()));
    sendCallBackToBUlk(bulkCallbackList);
    return ResponseEntity.ok().body(xml);
  }
  
  public void sendCallBackToBUlk(List<BulkCallback> bulkCallbacks) {
    log.info("Sending bulkCallbacks ....");
    DisableSSL ssl = new DisableSSL();
    Gson gson = new Gson();
    String url = "http://localhost:9095/api/v1/pesalink/callback";
    try {
      OkHttpClient client = DisableSSL.init(true);
      MediaType mediaType = MediaType.parse("application/json");
      okhttp3.RequestBody body =  okhttp3.RequestBody.create(mediaType, gson.toJson(bulkCallbacks));
      Request request = (new Request.Builder()).url(url).post(body).build();
      client.newCall(request).execute();
    } catch (Exception e) {
      System.out.println("Error Sending transaction status for bulk - " + e.getLocalizedMessage());
    } 
  }
}
