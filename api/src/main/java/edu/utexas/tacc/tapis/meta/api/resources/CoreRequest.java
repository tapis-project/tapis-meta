package edu.utexas.tacc.tapis.meta.api.resources;

import edu.utexas.tacc.tapis.meta.config.OkSingleton;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.meta.utils.MetaAppConstants;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;


/**
 * this class is responsible for proxying the requests from the security container sidecar to the core container
 *
 */
public class CoreRequest {
  // private static final long serialVersionUID = 2267124925495540982L;
  
  // Local logger.
  private static final Logger _log = LoggerFactory.getLogger(CoreRequest.class);
  
  // fields
  // private static String coreServiceUrl;  // look at a static init block
  private final OkHttpClient okHttpClient = OkSingleton.getInstance();
  private final String pathURL;
  
  // constructor(s)
  public CoreRequest(String _pathUri){
    // The core server doesn't understand the /meta/v3 prefix on the URI path.
    // it only wants to see /{db/{collection}...
    
    _log.info("constructed with path Uri : "+_pathUri);
  
    // This gives us the valid Core server path URI by stripping the service
    // prefix from the beginning of the path
    String pathUri = _pathUri.replace(MetaAppConstants.META_REQUEST_PREFIX, "");
    pathURL = RuntimeParameters.getInstance().getCoreServer()+ pathUri;
  
    _log.info("constructed with path URL : "+pathURL);
  }
  
  /*------------------------------------------------------------------------
   * proxyGetRequest
   * -----------------------------------------------------------------------*/
  public CoreResponse proxyGetRequest(){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .build();
    
    CoreResponse coreResponse = createResponse(coreRequest);
  
    _log.debug("call to host : GET "+pathURL+"\nresponse : \n"+coreResponse.getCoreResponsebody());
    
    return coreResponse;
  }
  
  private CoreResponse createResponse(Request coreRequest) {
    Response response;
    CoreResponse coreResponse = new CoreResponse();
    try {
      response = okHttpClient.newCall(coreRequest).execute();
      coreResponse.mapResponse(response);
      response.close();
    } catch (IOException e) {
      String msg = "Connection to core server failed : " +
          e.getMessage();
      _log.info(msg);
      // set a response to indicate server 500 error
      coreResponse.setStatusCode(500);
      coreResponse.setCoreMsg("Connection to core server failed");
      coreResponse.setCoreResponsebody(coreResponse.getBasicResponse());
    }
    return coreResponse;
  }
  
  // proxy PUT request
  public CoreResponse  proxyPutRequest(String json){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(json, JSON);
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .put(body)
        .build();
  
    CoreResponse coreResponse = createResponse(coreRequest);
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
  
    return coreResponse;
  }
  
  /*------------------------------------------------------------------------
   * proxyPostRequest
   * -----------------------------------------------------------------------*/
  public CoreResponse proxyPostRequest(String json){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(json, JSON);
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .post(body)
        .build();
  
    CoreResponse coreResponse = createResponse(coreRequest);
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
  
    return coreResponse;
  }
  
  /*------------------------------------------------------------------------
   * proxyDeleteRequest
   * -----------------------------------------------------------------------*/
  public CoreResponse  proxyDeleteRequest(HttpHeaders _httpHeaders){
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
  
    String headerValue;
    okhttp3.Request coreRequest;
    // MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    if(_httpHeaders.getRequestHeaders().containsKey("If-Match")){
      headerValue = _httpHeaders.getHeaderString("If-Match");
      coreRequest = new Request.Builder()
          .url(pathURL)
          .addHeader("If-Match",headerValue)
          .delete()
          .build();
    }else {
      coreRequest = new Request.Builder()
          .url(pathURL)
          .delete()
          .build();
    }
  
    CoreResponse coreResponse = createResponse(coreRequest);
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
  
    return coreResponse;
  }
  
  /*------------------------------------------------------------------------
   * proxyPatchRequest
   * -----------------------------------------------------------------------*/
  public CoreResponse proxyPatchRequest(String json) {
    // path url here has stripped out /v3/meta to make the correct path request
    //  to core server
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(json, JSON);
    okhttp3.Request coreRequest = new Request.Builder()
        .url(pathURL)
        .patch(body)
        .build();
  
    CoreResponse coreResponse = createResponse(coreRequest);
  
    _log.debug("call to host : "+pathURL+"\n"+"response : \n"+coreResponse.getCoreResponsebody());
  
    return coreResponse;
  }
}
