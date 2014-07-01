package eu.nets.factory.gateway.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "name"),
        @UniqueConstraint(columnNames = {"host", "installationPath"}),
        @UniqueConstraint(columnNames = {"host", "publicPort"})})
public class LoadBalancer extends AbstractEntity{

    @NotBlank
    private String name;

    @NotBlank
    private String host;

    @NotBlank
    private String installationPath;

    @NotBlank
    private String sshKey;

    private int publicPort;

    @ManyToMany(targetEntity = Application.class, mappedBy = "loadBalancers")//(fetch = FetchType.EAGER)
//    @JoinTable(name = "load_balancer_application",
//            joinColumns = {@JoinColumn(name = "load_balancer_id")},
//            inverseJoinColumns = {@JoinColumn(name = "application_id")})
    private List<Application> applications;


    public LoadBalancer(String name, String host, String installationPath, String sshKey, int publicPort) {
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
        this.publicPort = publicPort;
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

    public int getPublicPort() {
        return publicPort;
    }

    public void setPublicPort(int publicPort) {
        this.publicPort = publicPort;
    }

    public List<Application> getApplications() {
        return applications;
    }
    // public void setApplications(List<Application> applications) { this.applications = applications; }
    public void addApplication(Application application) {
        this.applications.add(application);
    }
    public void removeApplication(Application application) {
        this.applications.remove(application);
    }
}
