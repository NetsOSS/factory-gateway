package eu.nets.factory.gateway.model;

import javax.persistence.Entity;

/**
 * Created by kwlar on 19.06.2014.
 */

@Entity
public class ApplicationGroup extends AbstractEntity {

    private String name;

    public ApplicationGroup(String name) {
        this.name = name;
    }

    public ApplicationGroup(){};

    public String getName(){ return name; }
}