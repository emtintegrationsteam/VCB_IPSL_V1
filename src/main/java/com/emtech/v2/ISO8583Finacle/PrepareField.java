package com.emtech.v2.ISO8583Finacle;

public class PrepareField {
  public String formatField2(String str) {
    return leftPaddingNames(16, str);
  }
  
  public String formatField3(String str) {
    return formatProcessingCode(str);
  }
  
  public String formatField4(String str) {
    return formatMoney(str);
  }
  
  public String formatFieldKba4(String str) {
    int val = Integer.parseInt(str);
    String str2 = String.format("%12s", new Object[] { String.valueOf(val) }).replace(' ', '0');
    return str2;
  }
  
  public String formatField11(String str) {
    return leftPaddingNames(12, str);
  }
  
  public String formatField24(String str) {
    return leftPaddingNames(3, str);
  }
  
  public String formatField32(String str) {
    return formatBankCode(str);
  }
  
  public String formatField49(String str) {
    return leftPaddingNames(3, str);
  }
  
  public String formatField43(String str) {
    return leftPaddingNames(50, str);
  }
  
  public String formatField102(String account, String bank, String branch) {
    String resp = padRight(bank, 11) + padRight(bank, 11) + leftPaddingNames(8, branch);
    return resp;
  }
  
  public String formatField103(String account, String bank, String branch) {
    String resp = padRight(bank, 11) + padRight(bank, 11) + "  " + leftPaddingNames(8, branch);
    return resp;
  }
  
  public String formatField123(String str) {
    return leftPaddingNames(3, str);
  }
  
  public static String padRight(String s, int n) {
    return String.format("%1$-" + n + "s", new Object[] { s });
  }
  
  public static String formatMoney(String str) {
    int val = Integer.parseInt(str);
    String str2 = String.format("%16s", new Object[] { String.valueOf(val) }).replace(' ', '0');
    return str2;
  }
  
  public static String formatBankCode(String str) {
    int val = Integer.parseInt(str);
    String str2 = String.format("%6s", new Object[] { String.valueOf(val) }).replace(' ', '0');
    return str2;
  }
  
  public static String formatProcessingCode(String str) {
    int val = Integer.parseInt(str);
    String str2 = String.format("%-6s", new Object[] { Integer.valueOf(val) }).replace(' ', '0');
    return str2;
  }
  
  public static String leftPaddingNames(int length, String string) {
    return String.format("%1$" + length + "s", new Object[] { string });
  }
  
  public String formatField126(String str) {
    return leftPaddingNames(4, str);
  }
}
