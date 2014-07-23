package eu.nets.factory.gateway.model;

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

    @Pattern(regexp = "^\\S+$")
    private String name;

    @Pattern(regexp = "^/[a-zA-Z]\\S*$")
    @Column(nullable = false, name = "publicurl")
    private String publicUrl;

    @Pattern(regexp = "^/[a-zA-Z]\\S*$")
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

    @Column(nullable = false, name = "sticky_session")
    private int stickySessionValue;

    @Column(nullable = false, name = "failover_load_balancer_setup")
    private int failoverLoadBalancerSetupValue;

    @Column(nullable = false, name = "connect_timeout")
    private int connectTimeout;

    @Column(nullable = false, name = "read_timeout")
    private int readTimeout;

    @Column(nullable = false, name = "retry_timeout")
    private int retryTimeout;

    @OneToMany(mappedBy = "application", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<HeaderRule> headerRules = new ArrayList<>();

    public Application(String name, String url, ApplicationGroup applicationGroup, String emails, String checkPath) {
        this.name = name;
        this.publicUrl = url;
        this.applicationGroup = applicationGroup;
        this.emails=emails;
        this.checkPath = checkPath;

        this.stickySessionValue = StickySession.STICKY.ordinal();
        this.failoverLoadBalancerSetupValue = FailoverLoadBalancerSetup.HOT_HOT.ordinal();
        this.connectTimeout = 5000;
        this.readTimeout = 5000;
        this.retryTimeout = 5;
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

    public StickySession getStickySession() {
        return StickySession.values()[stickySessionValue];
    }
    public void setStickySession(StickySession stickySession) { this.stickySessionValue = stickySession.ordinal(); }

    public FailoverLoadBalancerSetup getFailoverLoadBalancerSetup() { return FailoverLoadBalancerSetup.values()[failoverLoadBalancerSetupValue]; }
    public void setFailoverLoadBalancerSetup(FailoverLoadBalancerSetup failoverLoadBalancerSetup) { this.failoverLoadBalancerSetupValue = failoverLoadBalancerSetup.ordinal(); }

    public ApplicationGroup getApplicationGroup() {
        return applicationGroup;
    }

    public List<LoadBalancer> getLoadBalancers() { return loadBalancers; }
    public void addLoadBalancer(LoadBalancer loadBalancer) { this.loadBalancers.add(loadBalancer); }
    public void removeLoadBalancer(LoadBalancer loadBalancer) { this.loadBalancers.remove(loadBalancer); }

    public List<ApplicationInstance> getApplicationInstances() { return applicationInstances; }
    public void addApplicationInstance(ApplicationInstance applicationInstance) { this.applicationInstances.add(applicationInstance); }
    public void removeApplicationInstance(ApplicationInstance applicationInstance) { this.applicationInstances.remove(applicationInstance); }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getRetryTimeout() {
        return retryTimeout;
    }

    public void setRetryTimeout(int retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    public List<HeaderRule> getHeaderRules() {
        return headerRules;
    }
    public void addHeaderRule(HeaderRule headerRule) { this.headerRules.add(headerRule); }
    public void removeHeaderRule(HeaderRule headerRule) {
        this.headerRules.remove(headerRule); }

}