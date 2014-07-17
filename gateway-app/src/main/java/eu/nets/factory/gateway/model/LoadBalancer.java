package eu.nets.factory.gateway.model;

import org.hibernate.validator.constraints.NotBlank;

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
        @UniqueConstraint(columnNames = {"host", "installationPath"}),
        @UniqueConstraint(columnNames = {"host", "publicPort"})})
public class LoadBalancer extends AbstractEntity{

    @NotBlank
    @Pattern(regexp = "^\\S+$")
    private String name;

    @NotBlank
    private String host;

    @Pattern(regexp = "^/[a-zA-Z]\\S*$")
    private String installationPath;

    @NotBlank
    private String sshKey;

    @NotNull
    @Min(1)
    @Max(65535)
    private int publicPort;

    @ManyToMany(targetEntity = Application.class, mappedBy = "loadBalancers")
    private List<Application> applications;


    public LoadBalancer(String name, String host, String installationPath, String sshKey, int publicPort) {
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
        this.publicPort = publicPort;
        this.applications = new ArrayList<>();
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
    public void addApplication(Application application) { this.applications.add(application); }
    public void removeApplication(Application application) {
        this.applications.remove(application);
    }
}
