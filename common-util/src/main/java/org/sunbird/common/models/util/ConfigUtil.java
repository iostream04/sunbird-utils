package org.sunbird.common.models.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/** 
 * Loads configurations from environment variables and configuration file
 * First checks for the value in environment variable if it finds then that 
 * will be set as value else the value will be taken from configuration file
 */
public class ConfigUtil {
  
  private static Config defaultConf = ConfigFactory.load();
  private static Config envConf = ConfigFactory.systemEnvironment();
  public static Config config = envConf.withFallback(defaultConf);
  
  /**
   * Loads specified configuration 
   * @param conf
   */
  public static void loadProperties(Config conf){
      config = config.withFallback(conf);
  }
  
  /** 
   * Converts the String value of the variable to List<String
   * @param path
   * @return
   */
  public static List<String> getStringList(String path){
    String configVariable = null;
    if(envConf.hasPath(path.toUpperCase())){
      configVariable = envConf.getString(path.toUpperCase());
    }else {
      configVariable = config.getString(path);
    }
    List<String> properties  = new ArrayList<String>();
    String[] configVars = StringUtils.split(configVariable, ",");
    properties = Arrays.asList(configVars);
    return properties;
  }
  
  public static String getString(String path){
    String configVariable = null;
    if(envConf.hasPath(path.toUpperCase())){
      configVariable = envConf.getString(path.toUpperCase());
    }else {
      configVariable = config.getString(path);
    }
    return configVariable;
  }
  
  public static Integer getInt(String path){
    Integer configVariable = null;
    if(envConf.hasPath(path.toUpperCase())){
      configVariable = envConf.getInt(path.toUpperCase());
    } else {
      configVariable = config.getInt(path);
    }
    return configVariable;
  }
  
  public static Long getLong(String path){
    Long configVariable = null;
    if(envConf.hasPath(path.toUpperCase())){
      configVariable = envConf.getLong(path.toUpperCase());
    } else {
      configVariable = config.getLong(path);
    }
    return configVariable;
  }
  
  public static Boolean getBoolean(String path){
    Boolean configVariable = null;
    if(envConf.hasPath(path.toUpperCase())){
      configVariable = envConf.getBoolean(path.toUpperCase());
    } else {
      configVariable = config.getBoolean(path);
    }
    return configVariable;
  }
}
