package org.sunbird.common.models.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Util class to create a excel file 
 */
public class ExcelFileUtil extends FileUtil {

  /**
   * Method to write data into a file
   * 
   * @param fileName 
   *        String value of the name of the file to be created without specifying the type
   *        of the file. The type of the file will be xlsx [Excel].
   * @param dataValues 
   *        List<List<Object>> usually the data which is in tabular form, 
   *        where the List<Object> indicates a single row data with each column value 
   *        and List<List<Object>> indicates the rows of the tabular data.        
   */
  @SuppressWarnings({"resource", "unused"})
  public File writeToFile(String fileName, List<List<Object>> dataValues) {
    // Blank workbook
    XSSFWorkbook workbook = new XSSFWorkbook();
    // Create a blank sheet
    XSSFSheet sheet = workbook.createSheet("Data");
    FileOutputStream out = null;
    File file = null;
    int rownum = 0;
    for (Object key : dataValues) {
      Row row = sheet.createRow(rownum);
      List<Object> objArr = dataValues.get(rownum);
      int cellnum = 0;
      for (Object obj : objArr) {
        Cell cell = row.createCell(cellnum++);
        if (obj instanceof String) {
          cell.setCellValue((String) obj);
        } else if (obj instanceof Integer) {
          cell.setCellValue((Integer) obj);
        } else if (obj instanceof List) {
          cell.setCellValue(getListValue(obj));
        } else if (obj instanceof Double) {
          cell.setCellValue((Double) obj);
        }else {
          if(ProjectUtil.isNotNull(obj)) {
            cell.setCellValue(obj.toString());
          }
        }
      }
      rownum++;
    }

    try {
      // Write the workbook in file system
      file = new File(fileName + ".xlsx");
      out = new FileOutputStream(file);
      workbook.write(out);
      ProjectLogger.log("File " + fileName + " created successfully");

    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    } finally {
      if (null != out) {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return file;

  }
}
