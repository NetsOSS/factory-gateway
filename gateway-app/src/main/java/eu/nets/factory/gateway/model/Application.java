package eu.nets.factory.gateway.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})})
public class Application extends AbstractEntity {

    @NotBlank
    private String name;

    @Pattern(regexp = "^/[a-zA-Z].*")
    @Column(nullable = false, name = "publicurl")
    private String publicUrl;

    @Pattern(regexp = "^/[a-zA-Z].*")
    @Column(nullable = false, name = "checkpath")
    private String checkPath;

    @ManyToMany
    @JoinTable(name = "load_balancer_application",
                joinColumns = {@JoinColumn(name = "application_id")},
                inverseJoinColumns = {@JoinColumn(name = "load_balancer_id")})
    private List<LoadBalancer> loadBalancers = new ArrayList<>();

    @OneToMany(mappedBy = "application", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<ApplicationInstance> applicationInstances = new ArrayList<>();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_group")
    private ApplicationGroup applicationGroup;

    private String emails;

    public Application(String name, String url, ApplicationGroup applicationGroup, String emails, String checkPath) {
        this.name = name;
        this.publicUrl = url;
        this.applicationGroup = applicationGroup;
        this.emails=emails;
        this.checkPath = checkPath;
    }

    public Application(){}

    public String getEmails() {
        return emails;
    }
    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPublicUrl(){ return publicUrl; }
    public void setPublicUrl(String url) { this.publicUrl = url; }

    public String getCheckPath() {
        return checkPath;
    }

    public void setCheckPath(String checkPath) {
        this.checkPath = checkPath;
    }

    public ApplicationGroup getApplicationGroup() {
        return applicationGroup;
    }

    public List<LoadBalancer> getLoadBalancers() { return loadBalancers; }
    public void addLoadBalancer(LoadBalancer loadBalancer) { this.loadBalancers.add(loadBalancer); }
    public void removeLoadBalancer(LoadBalancer loadBalancer) { this.loadBalancers.remove(loadBalancer); }

    public List<ApplicationInstance> getApplicationInstances() { return applicationInstances; }
    public void addApplicationInstance(ApplicationInstance applicationInstance) { this.applicationInstances.add(applicationInstance); }
    public void removeApplicationInstance(ApplicationInstance applicationInstance) { this.applicationInstances.remove(applicationInstance); }
}