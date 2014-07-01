package eu.nets.factory.gateway;

import org.springframework.http.HttpStatus;

/**
 * Created by kwlar on 01.07.2014.
 */
public class EntityNotFoundException extends GatewayException {
    public EntityNotFoundException(String entityName, Long id) {
        super("Could not find " + entityName + " with ID '" + id + "'.");
    }

    public EntityNotFoundException(String entityName, String sshKey) {
        super("Could not find " + entityName + " with ssh key '" + sshKey + "'.");
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
