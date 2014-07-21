package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.FailoverLoadBalancerSetup;
import eu.nets.factory.gateway.model.StickySession;

import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;

public  class AppModel {

    public Long id;

    public String name;
    public String publicUrl;
    public List<AppInstModel> applicationInstances = new ArrayList<>();
    public List<LoadBalancerModel> loadBalancers = new ArrayList<>();
    public Long applicationGroupId;
    public String emails;
    public String checkPath;
    public StickySession stickySession;
    public FailoverLoadBalancerSetup failoverLoadBalancerSetup;
    public int connectTimeout;
    public int readTimeout;
    public int retryTimeout;

    public AppModel() { }

    public AppModel(Application application) {
        this(application, false);

    }

    private AppModel(Application application, Boolean summary) {
        this(application.getId(), application.getName(), application.getPublicUrl(), application.getApplicationGroup(),application.getEmails(), application.getCheckPath());

        this.stickySession = application.getStickySession();
        this.failoverLoadBalancerSetup = application.getFailoverLoadBalancerSetup();
        this.connectTimeout = application.getConnectTimeout();
        this.readTimeout = application.getReadTimeout();
        this.retryTimeout = application.getRetryTimeout();
        this.applicationInstances = application.getApplicationInstances().stream().map(AppInstModel::summary).collect(toList());
        
        if(!summary) {
            //this.applicationInstances = application.getApplicationInstances().stream().map(AppInstModel::new).collect(toList());
            this.loadBalancers = application.getLoadBalancers().stream().map(LoadBalancerModel::summary).collect(toList());
        }
    }

    public AppModel(Long id, String name, String url, ApplicationGroup applicationGroup,String emails, String checkPath) {
        this.id = id;
        this.name = name;
        this.publicUrl = url;
        this.applicationGroupId = applicationGroup.getId();
        this.emails=emails;
        this.checkPath = checkPath;
    }

    public static AppModel summary(Application application) {
        return new AppModel(application, true);
    }


    public Long getId() { return id; }

    public String getName() { return name; }

    public String getPublicUrl() { return publicUrl; }

    public List<AppInstModel> getApplicationInstances() { return applicationInstances; }

    public List<LoadBalancerModel> getLoadBalancers() { return loadBalancers; }

    public Long getApplicationGroupId() { return applicationGroupId; }

    public String getEmails() {
        return emails;
    }

    public String getCheckPath() {
        return checkPath;
    }

    public String getStickySession() { return stickySession.name(); }
    public void setStickySession(String state) { this.stickySession = StickySession.valueOf(state); }

    public String getFailoverLoadBalancerSetup() { return failoverLoadBalancerSetup.name(); }
    public void setFailoverLoadBalancerSetup(String setup) { this.failoverLoadBalancerSetup = FailoverLoadBalancerSetup.valueOf(setup); }
}