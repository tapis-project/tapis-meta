package edu.utexas.tacc.tapis.meta;

import java.util.Arrays;

import edu.utexas.tacc.tapis.client.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.meta.config.RuntimeParameters;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.shared.security.ServiceJWT;
import edu.utexas.tacc.tapis.shared.security.ServiceJWTParms;

public class ServiceJWTRunnerTst {
  
  public static void main(String[] args) {
    RuntimeParameters runtime = RuntimeParameters.getInstance();
    String site = runtime.getSiteId();
    ServiceJWTParms parms = new ServiceJWTParms();
    parms.setServiceName(RuntimeParameters.SERVICE_NAME_META);
    parms.setTenant(RuntimeParameters.SERVICE_TENANT_NAME);
    parms.setTokensBaseUrl(runtime.getTokenBaseUrl());
    parms.setTargetSites(Arrays.asList(site));
  
    String servicePassword = "GZ9A9CGaTJ5MNr89BAvw2Aazw";
    try {
      ServiceJWT serviceJWT = new ServiceJWT(parms,servicePassword);
      System.out.println(serviceJWT.getAccessJWT(site));
    } catch (TapisException | TapisClientException e) {
      e.printStackTrace();
    }
  }
}
