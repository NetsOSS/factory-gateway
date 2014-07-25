package eu.nets.factory.gateway.model;

import org.hibernate.annotations.Check;
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

    @NotBlank
    private String userName;

    @NotNull
    @Min(20000)
    @Max(65535)
    private int publicPort;

    @Column(nullable = false, name = "check_timeout")
    private int checkTimeout;

    @Column(nullable = false, name = "connect_timeout")
    private int connectTimeout;

    @Check(constraints = "chk_server_client")
    @Column(nullable = false, name = "server_timeout")
    private int serverTimeout;

    @Check(constraints = "chk_server_client")
    @Column(nullable = false, name = "client_timeout")
    private int clientTimeout;

    @Column(nullable = false, name = "retries")
    private int retries;
    /*
    public int checkTimeout;
    public int connectTimeout;
    public int serverTimeout;
    public int clientTimeout;
    public int retries;
     */

    @ManyToMany(targetEntity = Application.class, mappedBy = "loadBalancers")
    @OrderBy("index_order")
    private List<Application> applications;


    public LoadBalancer(String name, String host, String installationPath, String sshKey, int statsPort, String userName, int checkTimeout, int connectTimeout, int serverTimeout, int clientTimeout, int retries) {
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
        this.userName = userName;
        this.publicPort = statsPort;
        this.checkTimeout = checkTimeout;
        this.connectTimeout = connectTimeout;
        this.serverTimeout = serverTimeout;
        this.clientTimeout = clientTimeout;
        this.retries = retries;
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

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getStatsPort() {
        return publicPort;
    }
    public void setStatsPort(int statsPort) {
        this.publicPort = statsPort;
    }

    public List<Application> getApplications() {
        return applications;
    }
    public void addApplication(Application application) { this.applications.add(application); }
    public void removeApplication(Application application) {
        this.applications.remove(application);
    }

    public int getRetries() {
        return retries;
    }
    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public int getServerTimeout() {
        return serverTimeout;
    }
    public void setServerTimeout(int serverTimeout) {
        this.serverTimeout = serverTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getCheckTimeout() {
        return checkTimeout;
    }
    public void setCheckTimeout(int checkTimeout) {
        this.checkTimeout = checkTimeout;
    }
}
