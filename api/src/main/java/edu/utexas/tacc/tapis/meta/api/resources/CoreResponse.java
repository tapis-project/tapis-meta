package edu.utexas.tacc.tapis.meta.api.resources;

// import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.shared.utils.TapisUtils;
import edu.utexas.tacc.tapis.sharedapi.responses.RespBasic;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class CoreResponse {
  
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(CoreResponse.class);

  // private boolean isResponseValid;
  private Map<String, List<String>> headers;
  private String coreResponsebody;
  private String coreMsg;
  private int statusCode;
  private String etag;
  private String location;
  // private boolean basicResponse;
  private String documentId;
  
  /**
   * map the response from the core server request to our jaxrs response framework.
   * @param coreResponse takes an okhttp response object and maps to jersey response framework
   */
  public void mapResponse(okhttp3.Response coreResponse) {
    // http method
    // captureResponseMethod(coreResponse);
    
    // collect all the response headers
    captureCoreResponseHeaders(coreResponse);
    
    // collect the message returned from core
    captureResponseMsg(coreResponse);
    
    // collect the response body for return back to requestor
    captureResponseBody(coreResponse);
    
    // what was our status code returned
    statusCode = coreResponse.code();
    
  }
  
  /*************************************************
   *    Capture methods for core server Response
   *************************************************/
  private void captureCoreResponseHeaders(okhttp3.Response coreResponse) {
    _log.debug("Capture Headers from core response ...");
    headers = coreResponse.headers().toMultimap();
    this.etag = coreResponse.header("ETag");
    this.location = coreResponse.header("Location");
    
    _log.debug(logResponseHeaders());
  }
  
  private void captureResponseBody(okhttp3.Response coreResponse) {
    _log.debug("response body output ");
    ResponseBody responseBody = coreResponse.body();
    try {
      if(responseBody == null){
        coreResponsebody = "";
      }else{
        coreResponsebody = responseBody.string();
      }
    } catch (IOException e) {
      _log.debug("response body exception thrown");
      e.printStackTrace();
    }
  }
  
  // TODO use or discard
  
  private void captureResponseMsg(okhttp3.Response coreResponse){
    coreMsg = coreResponse.message();
  }
  
  // TODO use or discard
  
  // TODO use or discard
  
  public String getLocationFromHeaders(){
    String coreLocation = null;
    // Our location header from the response will have the id of the resouce (document) at the
    // trailing end of the url location.
    // We should rewrite the location for the final response to our tenantbase
    // and capture the id to be used for basic tapis response
    if(headers.containsKey("Location")){
      List<String> locationList = headers.get("Location");
      coreLocation = locationList.get(0);
    }
    // rewrite location
    try {
      if(coreLocation == null)
        coreLocation = "http://nolocation.info";
      URL coreLocationUrl = new URL(coreLocation);
      String corePath = coreLocationUrl.getPath();
  
      documentId = getOidFromLocation(corePath);
      location = removeLastSlash(RuntimeParameters.getInstance().getTenantBaseUrl()) +
          corePath;
      
    } catch (MalformedURLException e) {
      _log.error("The location URL was malformed : "+coreLocation);
      e.printStackTrace();
    }
  
    return location;
  }
  
  private String removeLastSlash(String url) {
    if(url.endsWith("/")) {
      return url.substring(0, url.lastIndexOf("/"));
    }
    return url;
  }
  
  /*------------------------------------------------------------------------
   *                 Print functions for core server Response
   * -----------------------------------------------------------------------*/
  private String logResponseHeaders() {
    String sb = "Headers from core ...";
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      _log.debug(entry.getKey() + ":" + entry.getValue());
    }
    return sb;
  }
  
  // TODO use or lose
  
  protected String getBasicResponse(String location){
    _log.debug("Location for basic response "+location);
    RespBasic resp = new RespBasic();
    resp.status = String.valueOf(this.getStatusCode());
    resp.message = this.coreMsg;
    resp.version = TapisUtils.getTapisVersion();
    resp.result = JsonParser.parseString("{\"_id\":" + documentId + "}").getAsJsonObject();
    return TapisGsonUtils.getGson().toJson(resp);
  }
  
  // the results from this call become the response body CoreResponse
  // which in turn is sent back to user
  protected String getBasicResponse(){
    RespBasic resp = new RespBasic();
    resp.status = String.valueOf(this.getStatusCode());
    resp.message = this.coreMsg;
    resp.version = TapisUtils.getTapisVersion();
    resp.result = "";
    return TapisGsonUtils.getGson().toJson(resp);
  }
  
  private String getOidFromLocation(String location){
    // need to parse path with ending id which looks like this
    // /StreamsTACCDB/sltCollectionTst/5ea5bf3ca93eebf39fcc563b
    StringTokenizer st = new StringTokenizer(location,"/");
    
    // make the assumption this a a URL to a resource location with the ending value
    // the oid of the created document
    String oid = "";
    while (st.hasMoreElements()) {
      oid = st.nextToken();
      // we just need the last token which should be the oid
    }
    return oid;
  }
  
  // TODO use or lose
  
  // TODO use or lose
  
  /*************************************************
   *   Getters and Setters
   *************************************************/

  // public Map<String, List<String>> getHeaders() { return headers; }
  
  public String getCoreResponsebody() {
    return coreResponsebody;
  }
  
  public void setCoreResponsebody(String coreResponsebody) {
    this.coreResponsebody = coreResponsebody;
  }
  
  public int getStatusCode() {
    return statusCode;
  }
  
  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }
  
  public String getEtag() { return etag; }
  
  public void setCoreMsg(String coreMsg) { this.coreMsg = coreMsg; }
}
