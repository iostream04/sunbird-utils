package org.sunbird.common.models.util;

import org.junit.Assert;
import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class ConfigUtilTest {
  
  @Test
  public void testLoadProperties(){
    Config conf = ConfigFactory.load("test.properties");
    ConfigUtil.loadProperties(conf);
    Assert.assertTrue(ConfigUtil.config.hasPath("test.id"));
    Assert.assertEquals("testing", ConfigUtil.config.getString("test.id"));
  }

}
