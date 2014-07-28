package eu.nets.factory.gateway.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "name"),
        @UniqueConstraint(columnNames = "port")})
public class ApplicationGroup extends AbstractEntity {

    public static final int INSTANCE_PORT_MIN = 1024;
    public static final int INSTANCE_PORT_MAX = 64999;

    @Pattern(regexp = "^\\S+$")
    private String name;

    @NotNull
    @Min(INSTANCE_PORT_MIN)
    @Max(INSTANCE_PORT_MAX)
    private int port;

    @OneToMany(mappedBy = "applicationGroup", orphanRemoval = true, cascade = CascadeType.PERSIST)
    @OrderBy("index_order")
    private List<Application> applications = new ArrayList<>();


    public ApplicationGroup(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public ApplicationGroup(){ }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public List<Application> getApplications() { return applications; }
    public void addApplication(Application application) {
        applications.add(application);
    }

    public void removeApplication(Application application) { this.applications.remove(application); }

    public int applicationCount() {
        return applications.size();
    }
}