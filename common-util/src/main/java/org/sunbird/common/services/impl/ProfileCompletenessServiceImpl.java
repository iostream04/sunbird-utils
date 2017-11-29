/**
 * 
 */
package org.sunbird.common.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.sunbird.common.models.util.ConfigUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.services.ProfileCompletenessService;

/**
 * @author Manzarul
 *
 */
public class ProfileCompletenessServiceImpl
    implements ProfileCompletenessService {

  private static Map<String, Double> attributePercentageMap = new ConcurrentHashMap<>();

  @SuppressWarnings("rawtypes")
  @Override
  public Map<String, Object> computeProfile(Map<String, Object> profileData) {
    Map<String, Object> response = new HashMap<>();
    double completedCount = 0;
    if (profileData == null || profileData.size() == 0) {
      response.put(JsonKey.COMPLETENESS, (int) Math.ceil(completedCount));
      response.put(JsonKey.MISSING_FIELDS, findMissingAttribute(profileData));
      return response;
    }
    Iterator<Entry<String, Object>> itr = profileData.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, Object> entry = itr.next();
      Object value = entry.getValue();
      if (value instanceof List) {
        List list = (List) value;
        if (list.size() > 0) {
          completedCount = completedCount + getValue(entry.getKey());
        }
      } else if (value instanceof Map) {
        Map map = (Map) value;
        if (map != null && map.size() > 0) {
          completedCount = completedCount + getValue(entry.getKey());
        }
      } else {
        if (value !=null && !ProjectUtil.isStringNullOREmpty(value.toString())){
        completedCount = completedCount + getValue(entry.getKey());
        }
      }
    }
    response.put(JsonKey.COMPLETENESS, (int) Math.ceil(completedCount));
    response.put(JsonKey.MISSING_FIELDS, findMissingAttribute(profileData));
    return response;
  }

  /**
   * This method will provide weighted value for particular attribute
   * 
   * @param key String
   * @return float
   */
  private double getValue(String key) {
    return attributePercentageMap.get(key) != null
        ? attributePercentageMap.get(key) : 0;
  }


  /**
   * This method will provide all the missing filed list
   * 
   * @param profileData Map<String, Object>
   * @return List<String>
   */
  @SuppressWarnings("rawtypes")
  private List<String> findMissingAttribute(Map<String, Object> profileData) {
    List<String> attribute = new ArrayList<>();
    Iterator<Entry<String, Double>> itr =
        attributePercentageMap.entrySet()
            .iterator();
    while (itr.hasNext()) {
      Entry<String, Double> entry = itr.next();
      if (profileData==null || !profileData.containsKey(entry.getKey())) {
        attribute.add(entry.getKey());
      } else {
         Object val = profileData.get(entry.getKey());
         if (val == null) {
           attribute.add(entry.getKey());
         } else if (val instanceof List) {
            List list = (List) val;
            if (list.size()==0){
              attribute.add(entry.getKey()); 
            }
         }else if (val instanceof Map) {
            Map map = (Map) val;
            if (map == null || map.size()==0) {
              attribute.add(entry.getKey()); 
            }
         }else {
           if (ProjectUtil.isStringNullOREmpty(val.toString())) {
             attribute.add(entry.getKey()); 
           }
         }
      }
    }
    return attribute;
  }
  
  static {
    List<String> keys = ConfigUtil.getStringList(JsonKey.USER_PROFILE_ATTRIBUTE);
    List<String> values = new ArrayList<>();
    if(ConfigUtil.config.hasPath(JsonKey.USER_PROFILE_WEIGHTAGE)){
      values = ConfigUtil.getStringList(JsonKey.USER_PROFILE_WEIGHTAGE);
    }
    if (keys.size() == values.size()) {
      // then take the value from user
      ProjectLogger.log("weighted value is provided by user.");
      for (int i = 0; i < keys.size(); i++)
        attributePercentageMap.put(keys.get(i), Double.valueOf(values.get(i)));
    } else {
      // equally divide all the provided field.
      ProjectLogger.log("weighted value is not provided  by user.");
      double perc = (double) 100.0 / keys.size();
      for (int i = 0; i < keys.size(); i++)
        attributePercentageMap.put(keys.get(i), perc);
    }
  }
}
