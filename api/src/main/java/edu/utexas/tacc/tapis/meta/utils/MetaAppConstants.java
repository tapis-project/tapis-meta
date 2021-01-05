package edu.utexas.tacc.tapis.meta.utils;

import edu.utexas.tacc.tapis.shared.TapisConstants;

public class MetaAppConstants {
  
  public final static int META_FILTER_PRIORITY_PERMISSIONS =
      TapisConstants.JAXRS_FILTER_PRIORITY_AFTER_AUTHENTICATION_3+100;
  
  public final static String META_REQUEST_PREFIX = "/v3/meta/";
  
  public final static String TAPIS_USER_HEADER_NAME    = "X-Tapis-User";
  public final static String TAPIS_TENANT_HEADER_NAME  = "X-Tapis-Tenant";
  
  
  // Set Permissions checking
  public static final boolean TAPIS_ENVONLY_META_PERMISSIONS_CHECK = true;
  
  
}
