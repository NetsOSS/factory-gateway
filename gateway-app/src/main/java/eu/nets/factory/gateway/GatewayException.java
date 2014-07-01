package eu.nets.factory.gateway;

import org.springframework.http.HttpStatus;

/**
 * Created by kwlar on 30.06.2014.
 */
public class GatewayException extends RuntimeException {
    public GatewayException(String s) {
        super(s);
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
