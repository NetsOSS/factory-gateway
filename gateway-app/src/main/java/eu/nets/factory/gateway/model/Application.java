package eu.nets.factory.gateway.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwlar on 19.06.2014.
 */

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})})
public class Application extends AbstractEntity {

    @NotBlank
    private String name;

    @Pattern(regexp = "^/[a-zA-Z].*")
    @Column(nullable = false, name = "publicurl")
    private String publicUrl;

    @ManyToMany//(fetch = FetchType.EAGER)
    //@Cascade(CascadeType.DELETE)
    @JoinTable(name = "load_balancer_application",
                joinColumns = {@JoinColumn(name = "application_id")},
                inverseJoinColumns = {@JoinColumn(name = "load_balancer_id")})
    private List<LoadBalancer> loadBalancers = new ArrayList<>(); //<LoadBalancer>();

    //@Cascade(CascadeType.DELETE)
    @OneToMany(mappedBy = "application")//, fetch = FetchType.EAGER)
    private List<ApplicationInstance> applicationInstances = new ArrayList<>();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_group")
    //@Cascade(CascadeType.DELETE)
    private ApplicationGroup applicationGroup;

    private String emails;

    public Application(String name, String url, ApplicationGroup applicationGroup, String emails) {
        this.name = name;
        this.publicUrl = url;
        this.applicationGroup = applicationGroup;
        this.emails=emails;
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