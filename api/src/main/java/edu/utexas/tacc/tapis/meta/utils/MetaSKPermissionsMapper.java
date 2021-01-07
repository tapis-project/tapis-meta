package edu.utexas.tacc.tapis.meta.utils;

import java.util.StringTokenizer;

public class MetaSKPermissionsMapper {
  private final String uriPath;
  public final String meta = "meta";
  // public String op = "GET";
  public final String tenant;
  public String db="";
  public String collection="";
  public String document="";
  // public String
  
  /**
   * constructor for uripath mapping to sk permission spec
   * @param _uriPath   ie. /v3/meta/"db"/"collection"/"document"
   * @param _tenant    tenant id of the user for checking permissions
   */
  public MetaSKPermissionsMapper(String _uriPath, String _tenant){
    this.uriPath = _uriPath;
    this.tenant = _tenant;
  }
  
  /**
   * Converts the uri path into Shiro permissions format to use in an isPermitted
   * check against the SK.
   * @return  Shiro formatted permission string
   * @param op the operation requested
   */
  public String convert(String op){
    String pems = "";
    if(notNull(uriPath )&& notEmpty(tenant)){
      String processed = uriPath.replace("/v3/meta/", "");
      
      
      StringTokenizer st = new StringTokenizer(processed,"/");
      
      int resources = st.countTokens();
      switch(resources){
        case 0 : {
          return meta+":"+tenant+":"+op+":"+db+":"+collection+":"+document;
        }
        case 1 : {
          db = st.nextToken();
          return meta+":"+tenant+":"+op+":"+db+":"+collection+":"+document;
        }
        case 2 : {
          db = st.nextToken();
          collection = st.nextToken();
          return meta+":"+tenant+":"+op+":"+db+":"+collection+":"+document;
        }
        case 3 :
        case 4 : {
          db = st.nextToken();
          collection = st.nextToken();
          document = st.nextToken();
          return meta+":"+tenant+":"+op+":"+db+":"+collection+":"+document;
        }
      }
    }
    return pems;
  }
  
  /**
   * Simple check to see if header values returned actually have something in them
   * @param str String to check
   * @return true or false
   */
  private static boolean notEmpty(String str){
    return str != null && !str.trim().isEmpty();
  }
  
  private static boolean notNull(String str){
    return str != null;
  }
  
}
