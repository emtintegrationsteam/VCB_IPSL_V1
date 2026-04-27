package com.emtech.v2.phonehakikisha;

import com.emtech.v2.phonehakikisha.LookUP;
import com.emtech.v2.phonehakikisha.PhoneHakikishaResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/"})
public class PhoneHakikishaController {
  LookUP lu = new LookUP();
  
  @RequestMapping(value = {"SendPhoneHakikisha/{phoneno}"}, produces = {"application/json"})
  public PhoneHakikishaResponse sendPhoneHakikishaRequest(@PathVariable("phoneno") String phoneno) {
    PhoneHakikishaResponse pr = this.lu.bankCustListService(phoneno);
    return pr;
  }
}
