package org.sunbird.common.models.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/** 
 * @author Mahesh Kumar Gangula
 * Load configurations
 */
public class ConfigUtil {
  
  private static Config defaultConf = ConfigFactory.load();
  private static Config envConf = ConfigFactory.systemEnvironment();
  public static Config config = envConf.withFallback(defaultConf);
  public static void loadProperties(Config conf){
      config = config.withFallback(conf);
  }
  
  public static List<String> getStringList(String path){
    String configVariable = config.getString(path);
    List<String> properties  = new ArrayList<String>();
    String[] configVars = StringUtils.split(configVariable, ",");
    properties = Arrays.asList(configVars);
    return properties;
  }
}
