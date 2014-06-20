package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

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

    public LoadBalancer(String name, String host, String installationPath, String sshKey) {
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
    }

    public LoadBalancer() {
    }

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
}
