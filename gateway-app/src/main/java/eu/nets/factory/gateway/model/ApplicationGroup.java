package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwlar on 19.06.2014.
 */

@Entity
public class ApplicationGroup extends AbstractEntity {

    @NotNull
    private String name;

    @OneToMany(mappedBy = "applicationGroup")//, fetch = FetchType.EAGER)
    private List<Application> applications = new ArrayList<>();


    public ApplicationGroup(String name) {
        this.name = name;
    }

    public ApplicationGroup(){ }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Application> getApplications() { return applications; }
    public void addApplication(Application application) { this.applications.add(application); }
    public void removeApplication(Application application) { this.applications.remove(application); }
}