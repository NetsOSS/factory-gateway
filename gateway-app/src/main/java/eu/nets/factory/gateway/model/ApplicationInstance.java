package eu.nets.factory.gateway.model;

import javax.persistence.Entity;

@Entity
public class ApplicationInstance extends AbstractEntity {

    private String name;
    private String host;
    private int port;
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
