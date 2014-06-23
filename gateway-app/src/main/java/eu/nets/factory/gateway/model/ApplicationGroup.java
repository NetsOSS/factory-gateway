package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by kwlar on 19.06.2014.
 */

@Entity
public class ApplicationGroup extends AbstractEntity {

    @NotNull
    private String name;

    //@OneToMany(mappedBy = "applicationGroup")
    //private List<Application> applications;

    public ApplicationGroup(String name) {
        this.name = name;
    }

    public ApplicationGroup(){

    }


    public String getName(){ return name; }

    public void setName(String name) {
        this.name = name;
    }

    /*public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }*/
}