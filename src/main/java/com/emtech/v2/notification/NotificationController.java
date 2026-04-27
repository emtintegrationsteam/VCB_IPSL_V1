package com.emtech.v2.notification;

import com.emtech.v2.notification.admi011;
import okhttp3.OkHttpClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping({"/"})
public class NotificationController {
  admi011 a11 = new admi011();
  
  @RequestMapping(value = {"system-event-notification"}, consumes = {"application/xml", "application/json"}, produces = {"application/xml", "application/json"})
  public String sendNotification(OkHttpClient client) throws Exception {
    return this.a11.sendADMI011(client);
  }
}
