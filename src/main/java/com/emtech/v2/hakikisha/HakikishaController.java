package com.emtech.v2.hakikisha;



import com.emtech.v2.hakikisha.HakikishaResponse;
import com.emtech.v2.hakikisha.acmt23;
import com.emtech.v2.hakikisha.acmt24;
import okhttp3.OkHttpClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/"})
public class HakikishaController {
  acmt23 a23 = new acmt23();
  
  acmt24 a24 = new acmt24();
  
  @RequestMapping(value = {"SendHakikisha/{bankcode}/{accntno}"}, produces = {"application/json"})
  public HakikishaResponse sendVerificationRequest(@PathVariable("bankcode") String bankcode, @PathVariable("accntno") String accntno, OkHttpClient client) throws Exception {
    return this.a23.sendACMT23(client, bankcode, accntno);
  }
  
  @RequestMapping(value = {"verification-request"}, consumes = {"application/xml", "application/json"}, produces = {"application/xml", "application/json"})
  public void receiveHakikisha(@RequestBody String xml, OkHttpClient client) throws Exception {
    System.out.println("Incoming Hakikisha Request:\n" + xml);
    this.a24.sendACMT24(client, xml);
  }
}
