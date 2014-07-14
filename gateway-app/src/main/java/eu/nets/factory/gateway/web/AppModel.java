package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;

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

    public AppModel() { }

    public AppModel(Application application) {
        this(application, false);

    }

    private AppModel(Application application, Boolean summary) {
        this(application.getId(), application.getName(), application.getPublicUrl(), application.getApplicationGroup(),application.getEmails(), application.getCheckPath());
        if(!summary) {
            this.applicationInstances = application.getApplicationInstances().stream().map(AppInstModel::new).collect(toList());
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
}