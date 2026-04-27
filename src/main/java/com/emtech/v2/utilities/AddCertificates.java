package com.emtech.v2.utilities;

import com.emtech.v2.hakikisha.GenEnveloped;
import com.emtech.v2.utilities.Configurations;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class AddCertificates {
  static Configurations cn = new Configurations();
  
  static String key = cn.getProperties().getProperty("pesa.sign.keys");
  
  static String keystorepass = cn.getProperties().getProperty("pesa.cert.keystore.password");
  
  static String tlsversion = cn.getProperties().getProperty("pesa.cert.tls.version");
  
  static String transport = cn.getProperties().getProperty("pesa.sign.transport");
  
  static String chain = cn.getProperties().getProperty("pesa.sign.chain");
  
  public OkHttpClient addCertificates(OkHttpClient client) throws ProtocolException, IOException, IOException, Exception, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException, InvalidKeySpecException {
    GenEnveloped ge = new GenEnveloped();
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    InputStream trustedCertificateAsInputStream = Files.newInputStream(Paths.get(chain, new String[0]), new OpenOption[] { StandardOpenOption.READ });
    Certificate trustedCertificate = certificateFactory.generateCertificate(trustedCertificateAsInputStream);
    KeyStore trustStore = createEmptyKeyStore(keystorepass.toCharArray());
    trustStore.setCertificateEntry("server-certificate", trustedCertificate);
    BufferedReader br = new BufferedReader(new FileReader(key));
    Security.addProvider((Provider)new BouncyCastleProvider());
    PEMParser pp = new PEMParser(br);
    PEMKeyPair pemKeyPair = (PEMKeyPair)pp.readObject();
    KeyPair kp = (new JcaPEMKeyConverter()).getKeyPair(pemKeyPair);
    pp.close();
    KeyFactory kf = KeyFactory.getInstance("RSA");
    RSAPrivateKey privateKey = (RSAPrivateKey)kp.getPrivate();
    InputStream certificateChainAsInputStream = Files.newInputStream(Paths.get(transport, new String[0]), new OpenOption[] { StandardOpenOption.READ });
    Certificate certificateChain = certificateFactory.generateCertificate(certificateChainAsInputStream);
    KeyStore identityStore = createEmptyKeyStore(keystorepass.toCharArray());
    identityStore.setKeyEntry("client", privateKey, keystorepass.toCharArray(), new Certificate[] { certificateChain });
    trustedCertificateAsInputStream.close();
    certificateChainAsInputStream.close();
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(trustStore);
    TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(identityStore, keystorepass.toCharArray());
    KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(keyManagers, trustManagers, null);
    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
    client = (new OkHttpClient.Builder()).sslSocketFactory(sslSocketFactory, (X509TrustManager)trustManagers[0]).connectTimeout(2L, TimeUnit.MINUTES).writeTimeout(2L, TimeUnit.MINUTES).readTimeout(2L, TimeUnit.MINUTES).build();
    System.setProperty("https-protocol", tlsversion);
    return client;
  }
  
  public static KeyStore createEmptyKeyStore(char[] keyStorePassword) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null, keyStorePassword);
    return keyStore;
  }
}
