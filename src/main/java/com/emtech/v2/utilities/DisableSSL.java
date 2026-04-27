package com.emtech.v2.utilities;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DisableSSL {
  private static final Logger log = LoggerFactory.getLogger(com.emtech.v2.utilities.DisableSSL.class);
  
  private static OkHttpClient client = null;
  
  private static boolean ignoreSslCertificate = false;
  
  public static OkHttpClient getClient() {
    return client;
  }
  
  public static OkHttpClient init(boolean ignoreCertificate) throws Exception {
    OkHttpClient.Builder builder = (new OkHttpClient.Builder()).connectTimeout(70000L, TimeUnit.MILLISECONDS).readTimeout(70000L, TimeUnit.MILLISECONDS);
    if (ignoreCertificate) {
      ignoreSslCertificate = true;
      builder = configureToIgnoreCertificate(builder);
    } 
    return client = builder.build();
  }
  
  private static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
    try {
      TrustManager[] trustAllCerts = { (TrustManager)new Object() };
      SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new SecureRandom());
      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
      builder.hostnameVerifier((HostnameVerifier)new Object());
    } catch (Exception e) {
      log.warn("Exception while configuring IgnoreSslCertificate" + e, e);
    } 
    return builder;
  }
}
