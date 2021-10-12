package edu.utexas.tacc.tapis.meta.lib;

import edu.utexas.tacc.tapis.client.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.security.client.SKClient;
import edu.utexas.tacc.tapis.shared.exceptions.TapisException;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;
import edu.utexas.tacc.tapis.shared.security.ServiceClients;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PermissionsService {

    private final String metaServiceUsername = "meta";
    private final String permSpecFormat = "meta:admin:{ops}:{database}:{collection}";
    private final ServiceClients serviceClients =  ServiceClients.getInstance();

    public boolean isUserTenantAdmin(String tenantId, String username) throws IOException {
        SKClient skClient = getSkClient();
        try {
            boolean userHasRole = skClient.hasRole(tenantId, username, "tenant_admin");
            return userHasRole;
        } catch (TapisClientException ex) {
            throw new IOException("", ex);
        }
    }

    /**
     *
     * @param tenantId tenantId
     * @param username username
     * @param database name of db
     * @param collection name of collection (or null)
     * @return boolean
     */
    public boolean isUserPermitted(@NotNull String tenantId, @NotNull String username, @NotNull String database, String collection) throws IOException {
        SKClient skClient = getSkClient();
        try {
            boolean isPermitted =
        }
    }

    private SKClient getSkClient() throws IOException {
        SKClient skClient;
        try {
            skClient = serviceClients.getClient(metaServiceUsername, "admin", SKClient.class);
            return skClient;
        } catch (TapisException | ExecutionException e) {
            throw new IOException("Could not get client!", e);
        }
    }

}
