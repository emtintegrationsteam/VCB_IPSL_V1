package com.emtech.v2.tsq;
import com.emtech.v2.ISO8583Finacle.DbMethods;
import com.emtech.v2.tsq.pacs028;
import com.emtech.v2.utilities.Configurations;
import com.emtech.v2.utilities.MessageResponse;
import okhttp3.OkHttpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/"})
public class TSQController {
  pacs028 p28 = new pacs028();
  
  Configurations cn = new Configurations();
  
  String tsqquery = this.cn.getProperties().getProperty("pesa.tsq.query");
  
  @RequestMapping(value = {"send-tsq/{MsgId}"}, produces = {"application/json"})
  public ResponseEntity<?> sendTSQ(OkHttpClient client, @PathVariable("MsgId") String MsgId) throws Exception {
    int difference = Integer.parseInt(DbMethods.getValue(this.tsqquery, 1, 1, MsgId));
    if (difference >= 1) {
      this.p28.sendPACS028(client, MsgId);
    } else {
      return ResponseEntity.ok(new MessageResponse("TSQ Can only be Initiated 60 Seconds after completing a transaction!"));
    } 
    return ResponseEntity.ok(new MessageResponse("TSQ Initiated Successfully For Message ID : " + MsgId));
  }
}
