package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class LoadBalancer extends AbstractEntity{

    @NotNull
    private String name;

    @NotNull
    private String host;

    @NotNull
    private String installationPath;

    @NotNull
    private String sshKey;

    @ManyToMany
    @JoinTable(name = "load_balancer_application",
            joinColumns = {@JoinColumn(name = "load_balancer_id")},
            inverseJoinColumns = {@JoinColumn(name = "application_id")})
    private List<Application> applications;


    public LoadBalancer(String name, String host, String installationPath, String sshKey) {
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
        this.applications = new ArrayList<Application>();
    }

    public LoadBalancer() { }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getInstallationPath() {
        return installationPath;
    }

    public void setInstallationPath(String installationPath) {
        this.installationPath = installationPath;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public void addApplication(Application application) {
        this.applications.add(application);
    }

    public void removeApplication(Application application) {
        this.applications.remove(application);
    }
}
