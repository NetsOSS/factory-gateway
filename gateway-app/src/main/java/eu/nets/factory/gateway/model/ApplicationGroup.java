package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by kwlar on 19.06.2014.
 */

@Entity
public class ApplicationGroup extends AbstractEntity {

    @NotNull
    private String name;


    public ApplicationGroup(String name) {
        this.name = name;
    }

    public ApplicationGroup() {}


    public String getName(){ return name; }

    public void setName(String name) { this.name = name; }
}