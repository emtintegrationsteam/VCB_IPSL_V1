package com.emtech.v2.credittransfer.account2phone;

import com.emtech.v2.credittransfer.ToolKit;
import com.emtech.v2.credittransfer.account2phone.phonepacs008;
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
public class CreditTransferToPhoneController {
  phonepacs008 p8 = new phonepacs008();
  
  ToolKit tk = new ToolKit();
  
  @RequestMapping(value = {"send-to-phone/{amount}/{debaccnt}/{narration}/{phonenumber}/{index}"}, produces = {"application/json"})
  public ResponseEntity<?> sendCreditTransferToPhone(OkHttpClient client, @PathVariable("amount") String amount, @PathVariable("debaccnt") String debaccnt, @PathVariable("narration") String narration, @PathVariable("phonenumber") String phonenumber, @PathVariable("benbank") String benbank) throws Exception {
    String refNum = this.tk.generateString().toUpperCase();
    this.p8.sendPACS008(client, amount, debaccnt, narration, phonenumber, benbank, refNum);
    return ResponseEntity.ok(new MessageResponse("Credit Transfer of Amount Ksh. " + amount + " From A/C : " + debaccnt + " To Phone No : " + phonenumber + " initiated successfully!"));
  }
}
