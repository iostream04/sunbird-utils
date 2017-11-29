package org.sunbird.common.models.util;

import java.io.File;
import java.util.List;

/**
 * Util which is used to create files of different file types or format.
 * Currently supports only Excel format.
 *
 */
public abstract class FileUtil {
  /**
   * Override this method to provide implementation for tabular data
   * @param fileName 
   *        String value of the name of the file to be created without specifying the type
   *        of the file.
   * @param dataValues
   *        List<List<Object>> the data which is in tabular form.
   * @return File object created
   */
  public abstract File writeToFile(String fileName, List<List<Object>> dataValues);

  /**
   * Method to get the List of objects as string
   * @param obj
   * @return String format of the list
   */
  @SuppressWarnings("unchecked")
  protected static String getListValue(Object obj) {
    List<Object> data = (List<Object>) obj;
    if (!(data.isEmpty())) {
      StringBuffer sb = new StringBuffer();
      for (Object value : data) {
        sb.append((String) value).append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
      return sb.toString();
    }
    return "";
  }

  /**
   * Method to get a FileUtil Object based on the file type/format.
   * @param format File formats currently suuports only xlsx Excel
   * @return FileUtil object
   */
  public static FileUtil getFileUtil(String format) {
    if (ProjectUtil.isStringNullOREmpty(format)) {
      format = format.toLowerCase();
    }
    switch (format) {
      case "excel":
        return (new ExcelFileUtil());
      default:
        return (new ExcelFileUtil());
    }
  }

}
