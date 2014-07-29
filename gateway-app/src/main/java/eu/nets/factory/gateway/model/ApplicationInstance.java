package eu.nets.factory.gateway.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})})
public class ApplicationInstance extends AbstractEntity {

    //@NotBlank
    @Pattern(regexp = "^\\S+$")
    private String name;

    @NotBlank
    private String host;

    @NotNull
    @Min(1)
    @Max(65535)
    private int port;


    //@Pattern(regexp = "^$|^/[a-zA-Z]\\S*$")
//    @Pattern(regexp = "^/.*")
    private String path;

    @Column(nullable = false, name = "ha_proxy_state")
    private int haProxyStateValue;

    @NotNull
    @Min(0) //A value of 0 means the server will not participate in load-balancing but will still accept persistent connections. -> the config file does not specify the weight if weight is 0 - haProxys default weight value (1) will be used.
    @Max(256)
    private int weight;

    @NotNull
    @ManyToOne
    private Application application;

    private boolean backup;


    public ApplicationInstance(String name, String host, int port, String path, Application application) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.path = path;
        this.application = application;
        this.haProxyStateValue = HaProxyState.READY.ordinal();
        this.weight = 10;
        this.backup =false;
    }

    public ApplicationInstance() { }


    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getHost() { return host; }
    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() { return port; }
    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() { return path; }
    public void setPath(String path) {
        this.path = path;
    }

    public void setApplication(Application application) { this.application = application; }
    public Application getApplication() {
        return application;
    }

    public HaProxyState getHaProxyState() { return HaProxyState.values()[haProxyStateValue]; }
    public void setHaProxyStateValue(HaProxyState haProxyState) { this.haProxyStateValue = haProxyState.ordinal(); }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getHaProxyStateValue() {
        return haProxyStateValue;
    }

    public void setHaProxyStateValue(int haProxyStateValue) {
        this.haProxyStateValue = haProxyStateValue;
    }

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean isBackup) {
        this.backup = isBackup;
    }
}