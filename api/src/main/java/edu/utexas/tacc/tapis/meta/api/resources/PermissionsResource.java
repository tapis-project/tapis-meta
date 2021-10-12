package edu.utexas.tacc.tapis.meta.api.resources;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

@Path("/permissions")
public class PermissionsResource {

    // Local logger.
    private static final Logger _log = LoggerFactory.getLogger(ResourceBucket.class);

    @Context
    private HttpHeaders _httpHeaders;

    @Context
    private Application _application;

    @Context
    private UriInfo _uriInfo;

    @Context
    private SecurityContext _securityContext;

    @Context
    private ServletContext _servletContext;

    @Context
    private HttpServletRequest _request;

    /*************************************************
     *    Information unauthenticated endpoints
     *************************************************/

    //----------------  health check ----------------
    @POST
    @Path("/{dataBaseName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response grantPermissions() {
        return null;
    }
}
