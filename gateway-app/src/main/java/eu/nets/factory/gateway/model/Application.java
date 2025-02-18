package eu.nets.factory.gateway.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"}),
        @UniqueConstraint(columnNames = {"application_group", "index_order"})
})
public class Application extends AbstractEntity {

    @Pattern(regexp = "^\\S+$")
    private String name;

    @Pattern(regexp = "^/.*")
    @Column(nullable = false, name = "public_path")
    private String publicUrl;

    @Pattern(regexp = "^/.*")
    @Column(nullable = false, name = "checkpath")
    private String checkPath;

    @Pattern(regexp = "^/.*")
    @Column(nullable = false)
    private String privatePath;

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

    @Column(name = "index_order")
    @Min(0)
    private int indexOrder;

    @OneToMany(mappedBy = "application", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<HeaderRule> headerRules = new ArrayList<>();

    public Application(String name, String url, ApplicationGroup applicationGroup, String emails, String checkPath, String privatePath, int indexOrder) {
        this.name = name;
        this.publicUrl = url;
        this.applicationGroup = applicationGroup;
        this.emails = emails;
        this.checkPath = checkPath;
        this.privatePath = privatePath;
        this.indexOrder = indexOrder;

        this.stickySessionValue = StickySession.STICKY.ordinal();
    }

    public Application() {
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String url) {
        this.publicUrl = url;
    }

    public String getCheckPath() {
        return checkPath;
    }

    public void setCheckPath(String checkPath) {
        this.checkPath = checkPath;
    }

    public String getPrivatePath() {
        return privatePath;
    }

    public void setPrivatePath(String privatePath) {
        this.privatePath = privatePath;
    }

    public StickySession getStickySession() {
        return StickySession.values()[stickySessionValue];
    }

    public void setStickySession(StickySession stickySession) {
        this.stickySessionValue = stickySession.ordinal();
    }

    public ApplicationGroup getApplicationGroup() {
        return applicationGroup;
    }

    public List<LoadBalancer> getLoadBalancers() {
        return loadBalancers;
    }

    public void addLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancers.add(loadBalancer);
    }

    public void removeLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancers.remove(loadBalancer);
    }

    public List<ApplicationInstance> getApplicationInstances() {
        return applicationInstances;
    }

    public void addApplicationInstance(ApplicationInstance applicationInstance) {
        this.applicationInstances.add(applicationInstance);
    }

    public void removeApplicationInstance(ApplicationInstance applicationInstance) {
        this.applicationInstances.remove(applicationInstance);
    }

    public List<HeaderRule> getHeaderRules() {
        return headerRules;
    }

    public void addHeaderRule(HeaderRule headerRule) {
        this.headerRules.add(headerRule);
    }

    public void removeHeaderRule(HeaderRule headerRule) {
        this.headerRules.remove(headerRule);
    }

    public int getIndexOrder() {
        return indexOrder;
    }

    public void setIndexOrder(int indexOrder) {
        this.indexOrder = indexOrder;
    }

    public void moveDown() {
        this.indexOrder--;
    }

    public void moveUp() {
        this.indexOrder++;
    }
}
