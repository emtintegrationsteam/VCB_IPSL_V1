package com.emtech.v2.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurations {
  Properties prop;
  
  public Properties getProperties() {
    this.prop = new Properties();
    try {
      InputStream url = getClass().getClassLoader().getResourceAsStream("application.properties");
      this.prop.load(url);
    } catch (IOException asd) {
      System.out.println(asd.getMessage());
    } 
    return this.prop;
  }
}
