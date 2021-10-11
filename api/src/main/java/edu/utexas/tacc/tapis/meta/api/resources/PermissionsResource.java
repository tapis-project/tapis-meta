package edu.utexas.tacc.tapis.meta.api.resources;


@Path("/permissions/")
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
    @GET
    @Path("/healthcheck")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response healthCheck() {

    }
}
