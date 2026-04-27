package com.emtech.v2.ISO8583Finacle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DateUtils {
  Date currDate = new Date();
  
  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
  
  SimpleDateFormat lda = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
  
  SimpleDateFormat cyear = new SimpleDateFormat("yyyy", Locale.getDefault());
  
  SimpleDateFormat cdate = new SimpleDateFormat("dd", Locale.getDefault());
  
  SimpleDateFormat cMonth = new SimpleDateFormat("MM", Locale.getDefault());
  
  SimpleDateFormat log_dt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
  
  public String getCurrentDate() {
    String d = this.sdf.format(this.currDate);
    return d;
  }
  
  public String getLogoutDate() {
    String d = this.log_dt.format(this.currDate);
    return d;
  }
  
  public String getCurrentDateAndTime() {
    String d = this.lda.format(this.currDate);
    return d;
  }
  
  public String getCurrentYear() {
    String d = this.cyear.format(this.currDate);
    return d;
  }
  
  public String getCurrentDay() {
    String d = this.cdate.format(this.currDate);
    return d;
  }
  
  public String getCurrentMonth() {
    String d = this.cMonth.format(this.currDate);
    return d;
  }
  
  public String generateStan() {
    Random randomGenerator = new Random();
    int randomInt = randomGenerator.nextInt(1000000);
    String formatedInt = String.format("%06d", new Object[] { Integer.valueOf(randomInt) });
    return formatedInt;
  }
}
