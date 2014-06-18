package eu.nets.factory.gateway.model;

import javax.persistence.Entity;

/**
 * Created by sleru on 18.06.2014.
 */
@Entity
public class ApplicationInstance extends AbstractEntity{

    private String name;
    private String host;
    private int  port;
    private String path;

    public ApplicationInstance(String n) {
        this.name = n;
    }

    public String getName(){
        return name;
    }

    public void setHost(String host){this.host = host; }

    public String getHost() {return host; }

    public void setPort(int port) { this.port = port; }

    public int getPort() {return port; }

    public void setPath(String path) {this.path = path; }

    public String getPath() {return path;}
}
