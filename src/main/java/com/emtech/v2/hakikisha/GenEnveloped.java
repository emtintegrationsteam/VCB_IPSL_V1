package com.emtech.v2.hakikisha;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class GenEnveloped {
  public String genEnveloped(String inXmlPath, String outXmlPath, String privateKeyPath, String certPath) throws Exception {
    XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
    DigestMethod dm = fac.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", null);
    List<Transform> transforms = Collections.singletonList(fac.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec)null));
    Reference ref = fac.newReference("", dm, transforms, null, null);
    CanonicalizationMethod cm = fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec)null);
    SignatureMethod sm = fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);
    List<Reference> references = Collections.singletonList(ref);
    SignedInfo si = fac.newSignedInfo(cm, sm, references);
    BufferedReader br = new BufferedReader(new FileReader(privateKeyPath));
    Security.addProvider((Provider)new BouncyCastleProvider());
    PEMParser pp = new PEMParser(br);
    PEMKeyPair pemKeyPair = (PEMKeyPair)pp.readObject();
    KeyPair kp = (new JcaPEMKeyConverter()).getKeyPair(pemKeyPair);
    pp.close();
    KeyFactory kf = KeyFactory.getInstance("RSA");
    RSAPrivateKey privateKey = (RSAPrivateKey)kp.getPrivate();
    KeyInfoFactory kif = fac.getKeyInfoFactory();
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream(certPath));
    X509Data x509Data = kif.newX509Data(Collections.singletonList(cert));
    KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509Data));
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    InputSource is = new InputSource(new StringReader(inXmlPath));
    Document doc = dbf.newDocumentBuilder().parse(is);
    DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
    dsc.setDefaultNamespacePrefix("ds");
    XMLSignature signature = fac.newXMLSignature(si, ki);
    signature.sign(dsc);
    OutputStream os = new FileOutputStream(outXmlPath);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer trans = tf.newTransformer();
    trans.transform(new DOMSource(doc), new StreamResult(os));
    String result = getStringFromDocument(doc);
    return result;
  }
  
  public static String getStringFromDocument(Document doc) throws TransformerException {
    DOMSource domSource = new DOMSource(doc);
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.transform(domSource, result);
    return writer.toString();
  }
}
