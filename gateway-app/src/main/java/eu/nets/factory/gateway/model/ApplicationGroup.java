package eu.nets.factory.gateway.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")})
public class ApplicationGroup extends AbstractEntity {

    @Pattern(regexp = "^\\S+$")
    private String name;

    @OneToMany(mappedBy = "applicationGroup", orphanRemoval = true, cascade = CascadeType.PERSIST)
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