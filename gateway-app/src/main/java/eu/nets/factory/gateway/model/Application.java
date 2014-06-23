package eu.nets.factory.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kwlar on 19.06.2014.
 */

@Entity
public class Application extends AbstractEntity {

    @NotNull
    private String name;

    @NotNull
    @Column(nullable = false)
    private String publicURL;

    @ManyToMany
    @JoinTable(name = "load_balancer_application",
                joinColumns = {@JoinColumn(name = "application_id")},
                inverseJoinColumns = {@JoinColumn(name = "load_balancer_id")})
    private List<LoadBalancer> loadBalancerList = new ArrayList<LoadBalancer>();

    @OneToMany(mappedBy = "application")
    private List<ApplicationInstance> applicationInstances = new ArrayList<>();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_group")
    private ApplicationGroup applicationGroup;


    public Application(String name, String url, ApplicationGroup applicationGroup) {
        this.name = name;
        this.publicURL = url;
        this.applicationGroup = applicationGroup;
    }

    public Application(){}


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPublicUrl(){ return publicURL; }
    public void setPublicUrl(String url) { this.publicURL = url; }

    public ApplicationGroup getApplicationGroup() {
        return applicationGroup;
    }

    public List<LoadBalancer> getLoadBalancerList() { return loadBalancerList; }
    public void addLoadBalancer(LoadBalancer loadBalancer) { this.loadBalancerList.add(loadBalancer); }
    public void removeLoadBalancer(LoadBalancer loadBalancer) { this.loadBalancerList.remove(loadBalancer); }

    public List<ApplicationInstance> getApplicationInstances() { return applicationInstances; }
    public void addApplicationInstance(ApplicationInstance applicationInstance) { this.applicationInstances.add(applicationInstance); }
    public void removeApplicationInstance(ApplicationInstance applicationInstance) { this.applicationInstances.remove(applicationInstance); }
}