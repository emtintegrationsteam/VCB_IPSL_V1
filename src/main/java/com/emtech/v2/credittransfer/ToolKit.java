package com.emtech.v2.credittransfer;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ToolKit {
  public String generateString() {
    String characters = "abcdefghijklmnopqrstuvwxyz";
    StringBuilder sb = new StringBuilder();
    Random rnd = new Random();
    while (sb.length() < 8) {
      int index = (int) (rnd.nextFloat() * characters.length());
      sb.append(characters.charAt(index));
    }
    String s = sb.toString();
    return s;
  }
  
  public String generateStringBulk() {
    String characters = "abcdefghijklmnopqrstuvwxyz";
    StringBuilder sb = new StringBuilder();
    Random rnd = new Random();
    while (sb.length() < 4) {
      int index = (int)(rnd.nextFloat() * characters.length());
      sb.append(characters.charAt(index));
    } 
    String s = sb.toString();
    return s;
  }
  
  public String generateEndToEndId(String beneficiarybank) {
    String bankcode = "0054";
    String randomchars = generateString();
    String date = (new SimpleDateFormat("YYYYMMddhhmmss")).format(new Date());
    String EndToEndId = bankcode + bankcode + beneficiarybank + date;
    return EndToEndId;
  }
  
  public String generateEndToEndIdBulk(String beneficiarybank) {
    String bankcode = "0054";
    String randomchars = generateStringBulk();
    String date = (new SimpleDateFormat("YYYYMMddhhmmss")).format(new Date());
    String EndToEndId = bankcode + bankcode + beneficiarybank + date;
    return EndToEndId;
  }
  
  public String padLeft(String s, int n) {
    if (n <= 0)
      return s; 
    StringBuilder output = new StringBuilder(s.length() + n);
    while (n > 0) {
      output.append("0");
      n--;
    } 
    output.append(s);
    return output.toString();
  }

  public  String insertString(String originalString, String stringToBeInserted, int index) {
    // Create a new string
    String newString = "";
    for (int i = 0; i < originalString.length(); i++) {
      // Insert the original string character
      // into the new string
      newString += originalString.charAt(i);
      if (i == index) {
        // Insert the string to be inserted
        // into the new string
        newString += stringToBeInserted;
      }
    }
    // return the modified String
    return newString;
  }
  
  public static boolean checkIfNodeExists(String xml, String xpathExpression) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    InputSource is = new InputSource(new StringReader(xml));
    Document document = dbf.newDocumentBuilder().parse(is);
    boolean matches = false;
    XPathFactory xpathFactory = XPathFactory.newInstance();
    XPath xpath = xpathFactory.newXPath();
    try {
      XPathExpression expr = xpath.compile(xpathExpression);
      NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
      if (nodes != null && nodes.getLength() > 0)
        matches = true; 
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    } 
    return matches;
  }
}
