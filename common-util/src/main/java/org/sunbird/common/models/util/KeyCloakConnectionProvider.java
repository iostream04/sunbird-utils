/**
 * 
 */
package org.sunbird.common.models.util;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

/**
 * @author Manzarul
 * This class will connect to key cloak server 
 * and provide the connection to do other operations.
 */

public class KeyCloakConnectionProvider {
  
   private static Keycloak keycloak; 
   public static String SSO_URL = null;
   public static String SSO_REALM = null;
   public static String CLIENT_ID = null;
   static {
		try {
      initialiseConnection();
    } catch (Exception e) {
      ProjectLogger.log(e.getMessage(), e);
    }
		registerShutDownHook();
	}

	/**
	 * Method to initializate the Keycloak connection
	 * @return Keycloak connection
	 */
  public static Keycloak initialiseConnection() throws Exception{
    ProjectLogger.log("key cloak instance is creation started.");
    KeycloakBuilder keycloakBuilder = KeycloakBuilder.builder()
        .serverUrl(ConfigUtil.config.getString(JsonKey.SSO_URL)).realm(ConfigUtil.config.getString(JsonKey.SSO_REALM))
        .username(ConfigUtil.config.getString(JsonKey.SSO_USERNAME))
        .password(ConfigUtil.config.getString(JsonKey.SSO_PASSWORD))
        .clientId(ConfigUtil.config.getString(JsonKey.SSO_CLIENT_ID))
        .resteasyClient(new ResteasyClientBuilder()
            .connectionPoolSize(ConfigUtil.config.getInt(JsonKey.SSO_POOL_SIZE))
            .build());
    if (ConfigUtil.config.getString(JsonKey.SSO_CLIENT_SECRET) != null
        && !(ConfigUtil.config.getString(JsonKey.SSO_CLIENT_SECRET).equals(JsonKey.SSO_CLIENT_SECRET))) {
      keycloakBuilder.clientSecret(ConfigUtil.config.getString(JsonKey.SSO_CLIENT_SECRET));
    }
    SSO_URL = ConfigUtil.config.getString(JsonKey.SSO_URL);
    SSO_REALM = ConfigUtil.config.getString(JsonKey.SSO_REALM);
    CLIENT_ID = ConfigUtil.config.getString(JsonKey.SSO_CLIENT_ID);
    keycloak = keycloakBuilder.build();

	ProjectLogger.log("key cloak instance is created successfully.");
	return keycloak;
  }
  
  
   /**
	 * This method will provide key cloak
	 * connection instance. 
	 * @return Keycloak
	 */
  public static Keycloak getConnection() {
    if (keycloak != null) {
      return keycloak;
    } else {
      try {
        return initialiseConnection();
      } catch (Exception e) {
        ProjectLogger.log(e.getMessage(), e);
      }
    }
    return null;
  }
	
	/**
	 * This class will be called by registerShutDownHook to 
	 * register the call inside jvm , when jvm terminate it will call
	 * the run method to clean up the resource.
	 * @author Manzarul
	 *
	 */
	static class ResourceCleanUp extends Thread {
		  public void run() {
		      ProjectLogger.log("started resource cleanup.");
			  keycloak.close(); 
			  ProjectLogger.log("completed resource cleanup.");
		  }
	}
	
	/**
	 * Register the hook for resource clean up.
	 * this will be called when jvm shut down.
	 */
	public static void registerShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ResourceCleanUp());
		ProjectLogger.log("ShutDownHook registered.");
	}
	
}
