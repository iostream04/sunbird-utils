package org.sunbird.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.TableMetadata;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ConfigUtil;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.helper.CassandraConnectionManagerImpl;
import org.sunbird.helper.CassandraConnectionMngrFactory;
import org.sunbird.helper.ServiceFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CassandraTest {
	
	private CassandraOperation operation = ServiceFactory.getInstance();
	static Map<String,Object> address = null;
	static Map<String,Object> dummyAddress = null;
	private static CassandraConnectionManagerImpl connectionManager = (CassandraConnectionManagerImpl) CassandraConnectionMngrFactory.getObject(ConfigUtil.config.getString(JsonKey.SUNBIRD_CASSANDRA_MODE));
	
	@BeforeClass
	public static void init(){
	  
	  address = new HashMap<>();
	  address.put(JsonKey.ID,"123");
	  address.put(JsonKey.ADDRESS_LINE1, "Line 1");
	  address.put(JsonKey.USER_ID, "USR1");
	  
	  dummyAddress = new HashMap<>();
	  dummyAddress.put(JsonKey.ID,"12345");
	  dummyAddress.put(JsonKey.ADDRESS_LINE1, "Line 111");
	  dummyAddress.put(JsonKey.USER_ID, "USR111");
	  dummyAddress.put("DummyColumn", "USR111");

    connectionManager.createConnection(ConfigUtil.config.getString("contactPoint"), ConfigUtil.config.getString("port"), "cassandra", "password", ConfigUtil.config.getString("keyspace"));
   	}
	
	@Test
	public void testConnectionWithoutUserNameAndPassword() {
		boolean bool= connectionManager.createConnection(ConfigUtil.config.getString("contactPoint"), ConfigUtil.config.getString("port"), null, null, ConfigUtil.config.getString("keyspace"));
		assertEquals(true,bool);
	}
	
	@Test
    public void testConnection() {
        boolean bool= connectionManager
            .createConnection(ConfigUtil.config.getString("contactPoint"), ConfigUtil.config.getString("port"), "cassandra", "password", ConfigUtil.config.getString("keyspace"));
        assertEquals(true,bool);
    }
	
	  //@Test(expected=ProjectCommonException.class)
    public void testFailedConnection() {
    connectionManager.createConnection("127.0.0.1", "9042", "cassandra", "pass", "eySpace");
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFailedSessionCheck() {
      connectionManager.getSession("Keyspace");
    }
    
    @Test
    public void testAInsertOp() {
      Response response=operation.insertRecord(ConfigUtil.config.getString("keyspace"), "address", address);
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testAInsertFailedOp() {
      operation.insertRecord(ConfigUtil.config.getString("keyspace"), "address1", address);
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testAInsertFailedOpWithInvalidProperty() {
      operation.insertRecord(ConfigUtil.config.getString("keyspace"), "address", dummyAddress);
    }
       
    @Test
    public void testBUpdateOp() {
      address.put(JsonKey.CITY, "city");
      address.put(JsonKey.ADD_TYPE, "addrType");
      Response response=operation.updateRecord(ConfigUtil.config.getString("keyspace"), "address", address);
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testBUpdateFailedOp() {
      dummyAddress.put(JsonKey.CITY, "city");
      dummyAddress.put(JsonKey.ADD_TYPE, "addrType");
      operation.updateRecord(ConfigUtil.config.getString("keyspace"), "address1", address);
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testBUpdateFailedOpWithInvalidProperty() {
      dummyAddress.put(JsonKey.CITY, "city");
      dummyAddress.put(JsonKey.ADD_TYPE, "addrType");
      operation.updateRecord(ConfigUtil.config.getString("keyspace"), "address", dummyAddress);
    }
    
    @Test
    public void testBgetAllRecordsOp() {
      Response response=operation.getAllRecords(ConfigUtil.config.getString("keyspace"), "address");
      assertTrue(((List<?>)response.get("response")).size()>0);
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testBgetAllRecordsFailedOp() {
      operation.getAllRecords(ConfigUtil.config.getString("keyspace"), "Dummy Table Name");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCgetPropertiesValueByIdOp() {
      Response response=operation.getPropertiesValueById(ConfigUtil.config.getString("keyspace"), "address", "123", JsonKey.ID,JsonKey.CITY,JsonKey.ADD_TYPE);
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.ID)).equals("123"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testCgetPropertiesValueByIdFailedOp() {
      operation.getPropertiesValueById(ConfigUtil.config.getString("keyspace"), "address", "123", "Dummy Column");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testDgetRecordByIdOp() {
      Response response=operation.getRecordById(ConfigUtil.config.getString("keyspace"), "address", "123");
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.CITY)).equals("city"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testDgetRecordByIdFailedOp() {
      operation.getRecordById(ConfigUtil.config.getString("keyspace"), "Dummy Table Name", "12345");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFgetRecordsByPropertiesOp() {
      Map<String,Object> map = new HashMap<>();
      map.put(JsonKey.USER_ID, "USR1");
      map.put(JsonKey.ADD_TYPE, "addrType");
      Response response=operation.getRecordsByProperties(ConfigUtil.config.getString("keyspace"), "address", map);
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.ID)).equals("123"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFgetRecordsByPropertiesFailed2Op() {
      Map<String,Object> map = new HashMap<>();
      map.put(JsonKey.ADDRESS_TYPE, "add");
      map.put(JsonKey.ADDRESS_LINE1, "line1");
      List<String> list= new ArrayList<>();
      list.add("USR1");
      map.put("dummy", list);
      operation.getRecordsByProperties(ConfigUtil.config.getString("keyspace"), "address", map);
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFgetRecordsByPropertiesFailedOp() {
      Map<String,Object> map = new HashMap<>();
      map.put(JsonKey.ADDRESS_TYPE, "add");
      map.put(JsonKey.ADDRESS_LINE1, "line1");
      operation.getRecordsByProperties(ConfigUtil.config.getString("keyspace"), "address", map);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFgetRecordsByPropertyFrListOp() {
      List<Object> list = new ArrayList<>();
      list.add("123");
      list.add("321");
      Response response=operation.getRecordsByProperty(ConfigUtil.config.getString("keyspace"), "address", JsonKey.ID, list);
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.ID)).equals("123"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFgetRecordsByPropertyFrListFailedOp() {
      List<Object> list = new ArrayList<>();
      list.add("123");
      list.add("321");
      operation.getRecordsByProperty(ConfigUtil.config.getString("keyspace"), "address", JsonKey.ADD_TYPE, list);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFgetRecordsByPropertyOp() {
      Response response=operation.getRecordsByProperty(ConfigUtil.config.getString("keyspace"), "address", JsonKey.ADD_TYPE, "addrType");
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.ID)).equals("123"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testFgetRecordsByPropertyFailedOp() {
      operation.getRecordsByProperty(ConfigUtil.config.getString("keyspace"), "address", JsonKey.ADDRESS_LINE1, "Line1");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGgetRecordByIdOp() {
      Response response=operation.getRecordById(ConfigUtil.config.getString("keyspace"), "address", "123");
      assertTrue(((String)((List<Map<String,Object>>)response.get("response")).get(0).get(JsonKey.CITY)).equals("city"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testGgetRecordByIdOpFailed() {
      operation.getRecordById(ConfigUtil.config.getString("keyspace"), "address1", "123");
    }
    
    @Test
    public void testHUpsertOp() {
      address.put("Country", "country");
      Response response=operation.upsertRecord(ConfigUtil.config.getString("keyspace"), "address", address);
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testHUpsertOpFailed() {
      address.put("Country", "country");
      Response response=operation.upsertRecord(ConfigUtil.config.getString("keyspace"), "address1", address);
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @SuppressWarnings("unused")
    @Test(expected=ProjectCommonException.class)
    public void testHUpsertOpFailedWithInvalidParameter() {
      //address.put("Country", "country");
      Response response=operation.upsertRecord(ConfigUtil.config.getString("keyspace"), "address", dummyAddress);
      //assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test
    public void testZDeleteOp() {
      Response response=operation.deleteRecord(ConfigUtil.config.getString("keyspace"), "address", "123");
      assertEquals("SUCCESS", response.get("response"));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testZDeleteFailedOp() {
      operation.deleteRecord(ConfigUtil.config.getString("keyspace"), "address1", "123");
    }
    
    @Test
    public void testZaDeleteFailedOp() {
      connectionManager.createConnection(ConfigUtil.config.getString("contactPoint"), ConfigUtil.config.getString("port"), null, null, ConfigUtil.config.getString("keyspace"));
    }
    
    @Test
    public void testZgetTableList() {
      List<String> tableList = connectionManager.getTableList(ConfigUtil.config.getString("keyspace"));
      assertTrue(tableList.contains(JsonKey.USER));
    }
    
    @Test
    public void testZgetCluster() {
      Cluster cluster = connectionManager.getCluster(ConfigUtil.config.getString("keyspace"));
      Collection<TableMetadata> tables = cluster.getMetadata().getKeyspace(ConfigUtil.config.getString("keyspace")).getTables();
      List<String> tableList = tables.stream().map(tm -> tm.getName()).collect(Collectors.toList());
      assertTrue(tableList.contains(JsonKey.USER));
    }
    
    @Test(expected=ProjectCommonException.class)
    public void testZgetClusterWithInvalidKeySpace() {
      connectionManager.getCluster("sun");
    }
	
  @SuppressWarnings("static-access")
  @AfterClass
	public static void shutdownhook() {
    connectionManager.registerShutDownHook();
		address = null;
    }

}

