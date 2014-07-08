package eu.nets.factory.gateway.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})})
public class ApplicationInstance extends AbstractEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String host;

    @Min(1)
    @Max(65535)
    private int port;

    @Pattern(regexp = "^/[a-zA-Z].*")
    private String path;

    @ManyToOne
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
