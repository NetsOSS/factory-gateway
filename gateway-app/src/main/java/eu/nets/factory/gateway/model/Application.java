package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by kwlar on 19.06.2014.
 */

@Entity
public class Application extends AbstractEntity {

    @NotNull
    private String name;

    @NotNull
    private String publicURL;


    public Application(String name, String url) {
        this.name = name;
        this.publicURL = url;
    }

    public Application(){}


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPublicUrl(){ return publicURL; }

    public void setPublicUrl(String url) { this.publicURL = url; }
}