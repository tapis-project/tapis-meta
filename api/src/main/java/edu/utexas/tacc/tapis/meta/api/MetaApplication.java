package edu.utexas.tacc.tapis.meta.api;

import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.shared.TapisConstants;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.shared.security.ServiceContext;
import edu.utexas.tacc.tapis.shared.security.TenantManager;
import edu.utexas.tacc.tapis.sharedapi.jaxrs.filters.JWTValidateRequestFilter;
import edu.utexas.tacc.tapis.tenants.client.gen.model.Tenant;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import java.util.Map;

@ApplicationPath("/meta")
public class MetaApplication extends ResourceConfig {
  // Tracing.
  private static final Logger _log = LoggerFactory.getLogger(MetaApplication.class);
  
  public MetaApplication() throws TapisException {
    // Log our existence.
    System.out.println("**** Starting tapis-metaapi ****");
    
    // Register the swagger resources that allow the
    // documentation endpoints to be automatically generated.
    // TODO expand to all endpoints for auto generation of openapi definition
    register(OpenApiResource.class);
    register(AcceptHeaderOpenApiResource.class);
    
    // We specify what packages JAX-RS should recursively scan
    packages("edu.utexas.tacc.tapis");
    setApplicationName("meta");
  
    RuntimeParameters runTime = RuntimeParameters.getInstance();
    // Force runtime initialization of the tenant manager.  This creates the
    // singleton instance of the TenantManager that can then be accessed by
    // all subsequent application code--including filters--without reference
    // to the tenant service base url parameter.
    // try {
      // The base url of the tenants service is a required input parameter.
      // We actually retrieve the tenant list from the tenant service now
      // to fail fast if we can't access the list.
      // String url = runTime.getTenantBaseUrl();
      // TenantManager.getInstance(url).getTenants();
  
      // ---------------- Initialize Security Filter --------------
      // Required to process any requests.
      // JWTValidateRequestFilter.setService(runTime.SERVICE_NAME_META);
      // JWTValidateRequestFilter.setSiteId(runTime.getSiteId());

      // Do we also fail if we can't get a service token?
      // runTime.setServiceJWT();
    //} catch (Exception e) {
      // We don't depend on the logging subsystem.
      // System.out.println("**** FAILURE TO INITIALIZE: tapis-metaapi ****");
      // e.printStackTrace();
      // throw e;
    //}
  
    // ---------------- Initialize Security Filter --------------
    // Required to process any requests.
    JWTValidateRequestFilter.setService(runTime.SERVICE_NAME_META);
    JWTValidateRequestFilter.setSiteId(runTime.getSiteId());

    // ------------------- Recoverable Errors -------------------
    // ----- Tenant Map Initialization
    // Force runtime initialization of the tenant manager.  This creates the
    // singleton instance of the TenantManager that can then be accessed by
    // all subsequent application code--including filters--without reference
    // to the tenant service base url parameter.
    Map<String, Tenant> tenantMap = null;
    try {
      // The base url of the tenants service is a required input parameter.
      // We actually retrieve the tenant list from the tenant service now
      // to fail fast if we can't access the list.
      String url = runTime.getTenantBaseUrl();
      // TODO this needs revisiting when JoeS deploys tenant changes.
      // TenantManager.getInstance().getSiteMasterTenantId(runTime.getSiteId()).
      tenantMap = TenantManager.getInstance(url).getTenants();
    } catch (Exception e) {
      // We don't depend on the logging subsystem.
      // errors++;
      System.out.println("**** FAILURE TO INITIALIZE: tapis-jobsapi TenantManager ****");
      e.printStackTrace();
    }
    if (tenantMap != null) {
      System.out.println("**** SUCCESS:  " + tenantMap.size() + " tenants retrieved ****");
      String s = "Tenants:\n";
      for (String tenant : tenantMap.keySet()) s += "  " + tenant + "\n";
      System.out.println(s);
    } else
      System.out.println("**** FAILURE TO INITIALIZE: tapis-jobsapi TenantManager - No Tenants ****");
  
    // ----- Service JWT Initialization
    ServiceContext serviceCxt = ServiceContext.getInstance();
    try {
      serviceCxt.initServiceJWT(runTime.getSiteId(), TapisConstants.SERVICE_NAME_META,
          runTime.getServicePassword());
    }
    catch (Exception e) {
      // errors++;
      System.out.println("**** FAILURE TO INITIALIZE: tapis-jobsapi ServiceContext ****");
      e.printStackTrace();
    }
    if (serviceCxt.getServiceJWT() != null) {
      var targetSites = serviceCxt.getServiceJWT().getTargetSites();
      int targetSiteCnt = targetSites != null ? targetSites.size() : 0;
      System.out.println("**** SUCCESS:  " + targetSiteCnt + " target sites retrieved ****");
      if (targetSites != null) {
        String s = "Target sites:\n";
        for (String site : targetSites) s += "  " + site + "\n";
        System.out.println(s);
      }
    }
  
  
    // Dump the runTime.
    StringBuilder buf = new StringBuilder(2500); // capacity to avoid resizing
    buf.append("\n------- Starting  Meta Service ");
    buf.append(" -------");
    buf.append("\nBase URL: ");
    //buf.append(requestContext.getUriInfo().getBaseUri().toString());
  
    // Dump the runtime configuration.
    runTime.getRuntimeInfo(buf);
    buf.append("\n---------------------------------------------------\n");
  
    // Write the output information.
    System.out.println(buf.toString());
  }

}
