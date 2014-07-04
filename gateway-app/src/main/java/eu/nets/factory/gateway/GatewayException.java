package eu.nets.factory.gateway;

import org.springframework.http.HttpStatus;

public class GatewayException extends RuntimeException {
    public GatewayException(String s) {
        super(s);
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
