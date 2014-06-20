package eu.nets.factory.gateway.model;

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
    private String publicURL;

    @ManyToMany
    @JoinTable(name = "load_balancer_application",
                joinColumns = {@JoinColumn(name = "application_id")},
                inverseJoinColumns = {@JoinColumn(name = "load_balancer_id")})
    private List<LoadBalancer> loadBalancerList;

    @OneToMany(mappedBy = "application")
    private List<ApplicationInstance> applicationInstances;

    @ManyToOne
    @JoinColumn(name = "application_group")
    private ApplicationGroup applicationGroup;


    public Application(String name, String url) {
        this.name = name;
        this.publicURL = url;
        loadBalancerList = new ArrayList<LoadBalancer>();
        applicationInstances = new ArrayList<ApplicationInstance>();
    }

    public Application(){}


    public String getName() { return name; }

    public String getPublicUrl(){ return publicURL; }

    public void setPublicUrl(String url) { this.publicURL = url; }


    public void setName(String name) {
        this.name = name;
    }

    public List<LoadBalancer> getLoadBalancerList() {
        return loadBalancerList;
    }

    public void setLoadBalancerList(List<LoadBalancer> loadBalancerList) {
        this.loadBalancerList = loadBalancerList;
    }

    public List<ApplicationInstance> getApplicationInstances() {
        return applicationInstances;
    }

    public void setApplicationInstances(List<ApplicationInstance> applicationInstances) {
        this.applicationInstances = applicationInstances;
    }

    public ApplicationGroup getApplicationGroup() {
        return applicationGroup;
    }

    public void setApplicationGroup(ApplicationGroup applicationGroup) {
        this.applicationGroup = applicationGroup;
    }

    public void addLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancerList.add(loadBalancer);
    }

    public void removeLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancerList.remove(loadBalancer);
    }

    public void addApplicationInstance(ApplicationInstance applicationInstance) {
        this.applicationInstances.add(applicationInstance);
    }

    public void removeApplicationInstance(ApplicationInstance applicationInstance) {
        this.applicationInstances.remove(applicationInstance);
    }
}