package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class ApplicationInstance extends AbstractEntity {

    @NotNull
    private String name;

    @NotNull
    private String host;

    @Min(1)
    @Max(65535)
    private int port;

    @NotNull
    private String path;

    public ApplicationInstance(String name, String host, int port, String path) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.path = path;
    }

    public ApplicationInstance() {
    }

    public String getName() {
        return name;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
