package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class ApplicationInstance extends AbstractEntity {

    @NotNull
    private String name;

    @NotNull
    private String host;

    @Min(1)
    @Max(65535)
    private int port;

    @NotNull
    private String path;

    @ManyToOne
    @JoinColumn(name = "application")
    private Application application;

    public ApplicationInstance(String name, String host, int port, String path, Application application) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.path = path;
        this.application = application;
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
}
