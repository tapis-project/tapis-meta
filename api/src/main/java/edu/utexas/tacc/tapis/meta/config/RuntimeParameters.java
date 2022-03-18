package edu.utexas.tacc.tapis.meta.config;

import edu.utexas.tacc.tapis.client.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.shared.TapisConstants;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.shared.exceptions.runtime.TapisRuntimeException;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import edu.utexas.tacc.tapis.shared.parameters.TapisEnv;
import edu.utexas.tacc.tapis.shared.parameters.TapisEnv.EnvVar;
import edu.utexas.tacc.tapis.shared.parameters.TapisInput;
import edu.utexas.tacc.tapis.shared.security.ServiceClients;
import edu.utexas.tacc.tapis.shared.security.ServiceJWT;
import edu.utexas.tacc.tapis.shared.security.ServiceJWTParms;
// import edu.utexas.tacc.tapis.shared.uuid.TapisUUID;
// import edu.utexas.tacc.tapis.shared.uuid.UUIDType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class RuntimeParameters {
  
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(RuntimeParameters.class);
  
  // Distinguished user-chosen name of this runtime instance.
  // private String  instanceName;
  
  // The site in which this service is running.
  private String  siteId;
  private String  siteAdminTenantId;
  private ServiceClients serviceClients;

  // Globally unique id that identifies this JVM instance.
  // private static final TapisUUID id = new TapisUUID(UUIDType.METADATA);
  
  // Singleton instance.
  private static RuntimeParameters _instance = initInstance();
  
  /* ---------------------------------------------------------------------- */
  /* getLogSecurityInfo:                                                    */
  /* ---------------------------------------------------------------------- */
  /** Go directly to the environment to get the latest security info logging
   * value.  This effectively disregards any setting the appears in a
   * properties file or on the JVM command line.
   *
   * @return the current environment variable setting
   */
  private static boolean getLogSecurityInfo()
  {
    // Always return the latest environment value.
    return TapisEnv.getLogSecurityInfo();
  }
  
  // service locations.
  private String tenantBaseUrl = "";    // default "https://dev.develop.tapis.io/"
  private String skSvcURL      = "";    // default "https://dev.develop.tapis.io/v3"
  private String tokenBaseUrl  = "";    // default "https://dev.develop.tapis.io/"
  private ServiceJWT serviceJWT;
  private String servicePassword;
  
  // The slf4j/logback target directory and file.
  private String  logDirectory;
  private String  logFile;
  private String  coreServer = "http://restheart:8080/";  // default "http://restheart:8080/"
  
  
  // these need to move to shared library
  public static final String SERVICE_NAME_META  = "meta";
  public static final String SERVICE_USER_NAME  = "meta";
  public static final String SERVICE_TENANT_NAME = "admin";
  
  
  private RuntimeParameters() throws TapisRuntimeException {
    TapisInput tapisInput = new TapisInput(RuntimeParameters.SERVICE_NAME_META);
    Properties inputProperties;
    try {inputProperties = tapisInput.getInputParameters();}
    catch (TapisException e) {
      // Very bad news.
      String msg = MsgUtils.getMsg("TAPIS_SERVICE_INITIALIZATION_FAILED",
          RuntimeParameters.SERVICE_NAME_META,
          e.getMessage());
      _log.error(msg, e);
      throw new TapisRuntimeException(msg, e);
    }
    
    // The site must always be provided.
    String parm = inputProperties.getProperty(EnvVar.TAPIS_SITE_ID.getEnvName());
    _log.debug("TAPIS_SITE_ID: " + parm);
	if (StringUtils.isBlank(parm)) {
		parm = System.getenv(EnvVar.TAPIS_SITE_ID.name());
		//parm = System.getenv("tapis.site.id"); //envVar.name()
		if(StringUtils.isBlank(parm)) {
			String msg = MsgUtils.getMsg("TAPIS_SERVICE_PARM_INITIALIZATION_FAILED",
	            TapisConstants.SERVICE_NAME_META,
	            "siteId",
	            "No siteId specified.");
	    _log.error(msg);
	     throw new TapisRuntimeException(msg);
	    }
	}

    setSiteId(parm);
  
    //----------------------   Input parameters   ----------------------
    // String parm = inputProperties.getProperty(TapisEnv.EnvVar.TAPIS_LOG_DIRECTORY.getEnvName());
    parm = System.getenv("tapis.meta.core.server");
    
    if (StringUtils.isBlank(parm)) {
    	 parm = System.getenv("TAPIS_META_CORE_SERVER");
    	 if(StringUtils.isBlank(parm)) {
 			String msg = MsgUtils.getMsg("TAPIS_SERVICE_PARM_INITIALIZATION_FAILED",
 	            TapisConstants.SERVICE_NAME_META,
 	            "metaCoreServer",
 	            "No meta core server is specified.");
 	    _log.error(msg);
 	     throw new TapisRuntimeException(msg);
 	    }
    }
    	
    setCoreServer(parm);  	
  
    parm = inputProperties.getProperty("tapis.meta.log.directory");
    if (StringUtils.isBlank(parm)) {
   	 parm = System.getenv("TAPIS_META_LOG_DIRECTORY");
   	 if(StringUtils.isBlank(parm)) {
			String msg = MsgUtils.getMsg("TAPIS_SERVICE_PARM_INITIALIZATION_FAILED",
	            TapisConstants.SERVICE_NAME_META,
	            "metaLogDirectory",
	            "No meta log directory is specified.");
	    _log.warn(msg);
	     //throw new TapisRuntimeException(msg);
	    }
   }
   if (!StringUtils.isBlank(parm)) setLogDirectory(parm);
  
    parm = inputProperties.getProperty("tapis.meta.log.file");
    if (StringUtils.isBlank(parm)) {
      	 parm = System.getenv("TAPIS_META_LOG_FILE");
      	 if(StringUtils.isBlank(parm)) {
   			String msg = MsgUtils.getMsg("TAPIS_SERVICE_PARM_INITIALIZATION_FAILED",
   	            TapisConstants.SERVICE_NAME_META,
   	            "metaLogFile",
   	            "No meta log file is specified.");
   	    _log.warn(msg);
   	     //throw new TapisRuntimeException(msg);
   	    }
      }
    if (!StringUtils.isBlank(parm)) setLogFile(parm);
    
    /*
     * Commented on Mar 17 2022 - Needs to do the clean up once  we are sure no dependencies on the sectionn of the code.
     * 
     * parm = inputProperties.getProperty("tapis.meta.service.token");
    if (StringUtils.isBlank(parm)) {
     	 parm = System.getenv("TAPIS_META_SERVICE_TOKEN");
     	 if(StringUtils.isBlank(parm)) {
  			String msg = MsgUtils.getMsg("TAPIS_SERVICE_PARM_INITIALIZATION_FAILED",
  	            TapisConstants.SERVICE_NAME_META,
  	            "metaServiceToken",
  	            "No meta service token specified.");
  	     _log.warn(msg);
  	     //throw new TapisRuntimeException(msg);
  	    }
     }
   
    if (!StringUtils.isBlank(parm)) setMetaToken(parm);
     end of commented section Mar 17 2022 *****/
  
    parm = System.getenv("tapis.meta.service.tenantBaseUrl");
    if (StringUtils.isBlank(parm)) {
    	 parm = System.getenv("TAPIS_META_SERVICE_TENANT_BASE_URL");
    	 if(StringUtils.isBlank(parm)) {
 			String msg = MsgUtils.getMsg("TAPIS_SERVICE_PARM_INITIALIZATION_FAILED",
 	            TapisConstants.SERVICE_NAME_META,
 	            "meta ServiceTenantBaseUrl",
 	            "No meta service tenant base URL is specified.");
 	     _log.error(msg);
 	     throw new TapisRuntimeException(msg);
 	    }
    }
    
    if (!StringUtils.isBlank(parm)) setTenantBaseUrl(parm);
    
    
    parm = System.getenv("tapis.meta.service.skSvcURL");
    if (StringUtils.isBlank(parm)) {
   	 parm = System.getenv("TAPIS_META_SERVICE_SK_SVC_URL");
   	 if(StringUtils.isBlank(parm)) {
			String msg = MsgUtils.getMsg("TAPIS_SERVICE_PARM_INITIALIZATION_FAILED",
	            TapisConstants.SERVICE_NAME_META,
	            "meta ServiceSkSvcUrl",
	            "No meta service sk svc URL is specified.");
	     _log.error(msg);
	     throw new TapisRuntimeException(msg);
	    }
   }
    
    if (!StringUtils.isBlank(parm)) setSkSvcURL(parm);
  
    
    parm = System.getenv("tapis.meta.service.tokenBaseUrl");
    if (StringUtils.isBlank(parm)) {
   	 parm = System.getenv("TAPIS_META_SERVICE_TOKEN_BASE_URL");
   	 if(StringUtils.isBlank(parm)) {
			String msg = MsgUtils.getMsg("TAPIS_SERVICE_PARM_INITIALIZATION_FAILED",
	            TapisConstants.SERVICE_NAME_META,
	            "meta ServiceTokenBaseUrl",
	            "No meta service token base URL is specified.");
	     _log.error(msg);
	     throw new TapisRuntimeException(msg);
	    }
   }
    if (!StringUtils.isBlank(parm)) setTokenBaseUrl(parm);
    
    
    parm = System.getenv("TAPIS_SERVICE_PASSWORD");
    if (!StringUtils.isBlank(parm)) setServicePassword(parm);
  
  }
  
  /** Initialize the singleton instance of this class.
   *
   * @return the non-null singleton instance of this class
   */
  private static synchronized RuntimeParameters initInstance()
  {
    if (_instance == null) _instance = new RuntimeParameters();
    return _instance;
  }
  
  public static RuntimeParameters getInstance() {
    return _instance;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getRuntimeInfo:                                                        */
  /* ---------------------------------------------------------------------- */
  /** Augment the buffer with printable text based mostly on the parameters
   * managed by this class but also OS and JVM information.  The intent is
   * that the various job programs and utilities that rely on this class can
   * print their configuration parameters, including those from this class,
   * when they start up.
   *
   * @param buf StringBuilder buffer
   */
  public void getRuntimeInfo(StringBuilder buf)
  {
    buf.append("\n\n------- Logging -----------------------------------");
    buf.append("\ntapis.log.directory: ");
    buf.append(this.getLogDirectory());
    buf.append("\ntapis.log.file: ");
    buf.append(this.getLogFile());
    
    buf.append("\n\n------- Network -----------------------------------");
    buf.append("\nHost Addresses: ");
  
    buf.append("\n\n------- Site Id and Service password -----------------------------------");
    buf.append("\ntapis.site.id: ");
    buf.append(this.getSiteId());
    buf.append("\nTAPIS_SERVICE_PASSWORD: ");
    buf.append(this.getServicePassword());
    
    buf.append("\n\n------- Services base URLs -----------------------------------");
    buf.append("\ntapis.meta.service.tenantBaseUrl: ");
    buf.append(this.getTenantBaseUrl());
    buf.append("\ntapis.meta.service.skSvcURL: ");
    buf.append(this.getSkSvcURL());
    buf.append("\ntapis.meta.service.tokenBaseUrl: ");
    buf.append(this.getTokenBaseUrl());
    
    buf.append("\n\n------- DB Configuration --------------------------");
    
    buf.append("\n\n------- EnvOnly Configuration ---------------------");
    buf.append("\ntapis.envonly.log.security.info: ");
    buf.append(RuntimeParameters.getLogSecurityInfo());
    buf.append("\ntapis.envonly.allow.test.header.parms: ");
    // buf.append(this.isAllowTestHeaderParms());
    buf.append("\ntapis.envonly.jwt.optional: ");
    buf.append(TapisEnv.getBoolean(TapisEnv.EnvVar.TAPIS_ENVONLY_JWT_OPTIONAL));
    buf.append("\ntapis.envonly.skip.jwt.verify: ");
    buf.append(TapisEnv.getBoolean(TapisEnv.EnvVar.TAPIS_ENVONLY_SKIP_JWT_VERIFY));
    
    buf.append("\n\n------- Java Configuration ------------------------");
    buf.append("\njava.version: ");
    buf.append(System.getProperty("java.version"));
    buf.append("\njava.vendor: ");
    buf.append(System.getProperty("java.vendor"));
    buf.append("\njava.vm.version: ");
    buf.append(System.getProperty("java.vm.version"));
    buf.append("\njava.vm.vendor: ");
    buf.append(System.getProperty("java.vm.vendor"));
    buf.append("\njava.vm.name: ");
    buf.append(System.getProperty("java.vm.name"));
    buf.append("\nos.name: ");
    buf.append(System.getProperty("os.name"));
    buf.append("\nos.arch: ");
    buf.append(System.getProperty("os.arch"));
    buf.append("\nos.version: ");
    buf.append(System.getProperty("os.version"));
    buf.append("\nuser.name: ");
    buf.append(System.getProperty("user.name"));
    buf.append("\nuser.home: ");
    buf.append(System.getProperty("user.home"));
    buf.append("\nuser.dir: ");
    buf.append(System.getProperty("user.dir"));
    
    buf.append("\n\n------- JVM Runtime Values ------------------------");
    NumberFormat formatter = NumberFormat.getIntegerInstance();
    buf.append("\navailableProcessors: ");
    buf.append(formatter.format(Runtime.getRuntime().availableProcessors()));
    buf.append("\nmaxMemory: ");
    buf.append(formatter.format(Runtime.getRuntime().maxMemory()));
    buf.append("\ntotalMemory: ");
    buf.append(formatter.format(Runtime.getRuntime().totalMemory()));
    buf.append("\nfreeMemory: ");
    buf.append(formatter.format(Runtime.getRuntime().freeMemory()));
  }
  
  public String getSiteId() { return siteId; }
  private void setSiteId(String siteId) { this.siteId = siteId; }

  public String getSiteAdminTenantId() { return siteAdminTenantId; }
  public void setSiteAdminTenantId(String siteAdminTenantId) { this.siteAdminTenantId = siteAdminTenantId; }

  public ServiceClients getServiceClients() { return serviceClients; }
  public void setServiceClients(ServiceClients serviceClients) { this.serviceClients = serviceClients; }

  public String getTenantBaseUrl() { return this.tenantBaseUrl; }
  private void setTenantBaseUrl(String tenantBaseUrl) {
    this.tenantBaseUrl = tenantBaseUrl;
  }
  
  public String getSkSvcURL() { return skSvcURL; }
  private void setSkSvcURL(String skSvcURL) {
    this.skSvcURL = skSvcURL;
  }
  
  public String getTokenBaseUrl() { return tokenBaseUrl; }
  private void setTokenBaseUrl(String tokenBaseUrl) { this.tokenBaseUrl = tokenBaseUrl; }
  
  private void setMetaToken(String metaToken) {
    _log.debug("Token for testing : "+metaToken);
  }
  
  public String getLogDirectory() {
    return logDirectory;
  }
  private void setLogDirectory(String logDirectory) {
    this.logDirectory = logDirectory;
  }
  
  public String getLogFile() {
    return logFile;
  }
  private void setLogFile(String logFile) {
    this.logFile = logFile;
  }
  
  public String getCoreServer() { return coreServer; }
  private void setCoreServer(String coreServer) {
    this.coreServer = coreServer;
  }
  
  public String getServicePassword() { return servicePassword; }
  private void setServicePassword(String servicePassword) { this.servicePassword = servicePassword; }
  
  private void setServiceJWT(){
    _log.debug("calling setServiceJWT ...");
    ServiceJWTParms serviceJWTParms = new ServiceJWTParms();
    serviceJWTParms.setAccessTTL(43200); // 12 hrs
    serviceJWTParms.setRefreshTTL(43200);
    serviceJWTParms.setServiceName(RuntimeParameters.SERVICE_USER_NAME);
    serviceJWTParms.setTenant(RuntimeParameters.SERVICE_TENANT_NAME);
    serviceJWTParms.setTokensBaseUrl(this.getTenantBaseUrl());
    serviceJWTParms.setTargetSites(Collections.singletonList(getSiteId()));
    serviceJWT = null;
    try {
      serviceJWT = new ServiceJWT(serviceJWTParms, TapisEnv.get(TapisEnv.EnvVar.TAPIS_SERVICE_PASSWORD));
    } catch (TapisException | TapisClientException e) {
      _log.error("Service JWT initialization failed, unrecoverable error here");
      e.printStackTrace();
    }
  }

  public String getSeviceToken(){
    if((serviceJWT == null) || serviceJWT.hasExpiredAccessJWT(siteId)){
      setServiceJWT();
    }
    return serviceJWT.getAccessJWT(siteId);
  }
  

}
