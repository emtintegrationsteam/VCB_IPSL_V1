package com.emtech.v2.utilities;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;

public class Encryptor {
  public static String encrypt(String key, String initVector, String value) {
    try {
      IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(1, skeySpec, iv);
      byte[] encrypted = cipher.doFinal(value.getBytes());
      return Base64.encodeBase64String(encrypted);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    } 
  }
  
  public static String decrypt(String key, String initVector, String encrypted) {
    try {
      IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(2, skeySpec, iv);
      byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
      return new String(original);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    } 
  }
  
  public static void main(String[] args) {
    String key = "C3lCCRd2T5PtUKQMSzYyb9NtKlf9dSdmwOZh1AAcAG1bFWde5Cpo/C9ta36GYD+UfZRJ1IV2QbQ5CpINYzCwEUTAiHLQQCZFULlXD4W6BnotfIloIy7psDB99i81PA2tk1GfZJbs3jM1qSRM/P1o3U2kGR17Wdeu8q224uDrntxHC3DSsKTxB3F0dkBm7EmYbP31LvSMMo8XQUC6hwOLlTlSoL6ykOtoav3i+ZuttmP5SZ8wjrDhLCmgIL53BLmFjdB7UgcbpzCu16ZY0a3uZsaqv1dPDPR1X9rcNv2MYYbA9/Si9fEmnda8/rSbQNahQQcF74xNES7C7zNuedV26w=";
    String initvector = "JESSEEMUKULEOMUK";
    String encrypted = "eyJjbGllbnRQcmlLZXkiOiIiLCJrZXlUYWJsZSI6IiIsInNlcnZlclB1YktleSI6Ik1JSUJJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBZzkza1hWSnRRcDFwS2VHenV5M0RYVjZMd0VwaVlBU2dFSUhlNEhna04rY2FvemJUNFhMbWRDVXN5UkJWOWJSb1dsQzNacTNkaCtLRHV2Y1VOZDcyM2ZwazN4YmNKRWc5U0ptVGlIWlFodThPM2FQeG1aRXpyMDZZaHdkczhSRmZCSG1WaTNTTUtyQ29KekJHMzV3YU0rZGpmSDUxMk9JQktsQTFEWjN1WHQyZHh2KzA2WEJSVGF0RmtIcVdLTlZ0c1IxOWNZV1hFenVFZEJpVWgvSCtPaTlLQS8zcDdmUnlIMWxCdFZtUkUva2pCL1VROUVUclgwRjdhQXIzMWFjRW1helR0S2krL0kyczFhZmZJczhONGVScWN1Y3Z1b0JOS3cyNHpzY3lFaVlWcUZlYWpweXhqUS9vQUVEZW9WbFY4Yng5SnAxMFJOYU5XcDVrNk8zWjFRSURBUUFCIn0=";
    System.out.println(decrypt(key, initvector, encrypted));
  }
}
