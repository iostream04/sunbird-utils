package org.sunbird.common.models.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;

/**
 * Created by arvind on 6/10/17.
 */
public class ProjectUtilTest {


  @BeforeClass
  public static void setUp(){

  }

  @Test
  public void testMailTemplateContextNameAsent(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    Assert.assertEquals(false ,context.internalContainsKey(JsonKey.NAME));

  }

  @Test
  public void testMailTemplateContextActionUrlAbsent(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.NAME, "userName");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    Assert.assertEquals(false ,context.internalContainsKey(JsonKey.ACTION_URL));

  }

  @Test
  public void testMailTemplateContextCheckFromMail(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");
    templateMap.put(JsonKey.NAME, "userName");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    Assert.assertEquals(ConfigUtil.config.getString(JsonKey.EMAIL_SERVER_FROM) , (String)context.internalGet(JsonKey.FROM_EMAIL));

  }

  @Test
  public void testMailTemplateContextCheckOrgImageUrl(){

    Map<String , Object> templateMap = new HashMap<>();
    templateMap.put(JsonKey.ACTION_URL, "googli.com");
    templateMap.put(JsonKey.NAME, "userName");

    VelocityContext context = ProjectUtil.getContext(templateMap);
    Assert.assertEquals(ConfigUtil.config.getString(JsonKey.SUNBIRD_ENV_LOGO_URL) , (String)context.internalGet(JsonKey.ORG_IMAGE_URL));

  }

}
