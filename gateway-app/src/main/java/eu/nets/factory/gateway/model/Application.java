package eu.nets.factory.gateway.model;

import javax.persistence.Entity;

/**
 * Created by sleru on 18.06.2014.
 */
@Entity
public class Application {

    private String name;
    private String version;
    private String URL;

    public Application(String n) {
        this.name = n;
    }

    public String getName(){
        return name;
    }

    public void setVersion(String v) {
        this.version = v;
    }
    public String getVersion() {
        return version;
    }
    public void setURL(String u) {
        this.URL = u;
    }
}
