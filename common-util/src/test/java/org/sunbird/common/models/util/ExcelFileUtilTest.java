package org.sunbird.common.models.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ExcelFileUtilTest {
  
  @Test
  public void testWriteToFile(){
    String fileName = "test";
    List<List<Object>> data = new ArrayList<>();
    ExcelFileUtil excelFileUtil = new ExcelFileUtil();
    File file = excelFileUtil.writeToFile(fileName, data);
    String[] expectedFileName = StringUtils.split(file.getName(),'.');
    Assert.assertEquals("test", expectedFileName[0]);
    Assert.assertEquals("xlsx", expectedFileName[1]);
  }

}
