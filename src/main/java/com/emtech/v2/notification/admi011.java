package com.emtech.v2.notification;

import com.emtech.v2.hakikisha.GenEnveloped;
import com.emtech.v2.utilities.AddCertificates;
import com.emtech.v2.utilities.Configurations;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class admi011 {
  static Configurations cn = new Configurations();
  
  static String admi011url = cn.getProperties().getProperty("pesa.url.admi011");
  
  static String outputadmi011 = cn.getProperties().getProperty("pesa.output.admi011");
  
  static String key = cn.getProperties().getProperty("pesa.sign.keys");
  
  static String seal = cn.getProperties().getProperty("pesa.sign.seal");
  
  static String logfile = cn.getProperties().getProperty("pesa.logs.admi11.txt");
  
  AddCertificates ac = new AddCertificates();
  
  public String sendADMI011(OkHttpClient oclient) throws Exception {
    String date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date());
    OkHttpClient client = this.ac.addCertificates(oclient);
    GenEnveloped ge = new GenEnveloped();
    Random rn = new Random();
    String unique = String.valueOf(rn.nextInt(999999901) + 2);
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:admi.011.001.01\">\n\t<SysEvtAck>\n\t\t<MsgId>" + unique + "</MsgId>\n\t</SysEvtAck>\n</Document>";
    String signed = ge.genEnveloped(xml, outputadmi011, key, seal);
    System.out.println("****************************************************************************");
    System.out.println(xml + "\n");
    System.out.println("ADMI.011.001.01 SENT AT " + date);
    System.out.println("****************************************************************************");
    MediaType mediaType = MediaType.parse("application/xml;charset=UTF-8");
    RequestBody body = RequestBody.create(mediaType, signed);
    Request request = (new Request.Builder()).url(admi011url).post(body).addHeader("Content-Type", "application/xml;charset=UTF-8").build();
    Response response = client.newCall(request).execute();
    try {
      if (!response.isSuccessful()) {
        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++)
          System.out.println(responseHeaders.name(i) + ": " + responseHeaders.name(i)); 
        throw new IOException("Error " + response);
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
    return "ACK ADMI.011 Message Sent.ID :: " + unique;
  }
}
