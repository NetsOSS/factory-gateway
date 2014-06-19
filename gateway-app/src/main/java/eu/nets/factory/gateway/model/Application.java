package eu.nets.factory.gateway.model;

import javax.persistence.Entity;

/**
 * Created by kwlar on 19.06.2014.
 */

@Entity
public class Application extends AbstractEntity {

    private String name;
    private String publicURL;

    public Application(String name, String url) {
        this.name = name;
        this.publicURL = url;
    }

    public Application(){};


    public String getName() { return name; }

    public String getPublicURL(){ return publicURL; }

    public void setPublicURL(String url) { this.publicURL = url; }
}