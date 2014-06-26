package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public  class AppModel {

    public Long id;

    public String name;
    public String publicURL;
    public List<AppInstModel> applicationInstances = new ArrayList<>();
    public List<LoadBalancerModel> loadBalancers = new ArrayList<>();
    public Long applicationGroupId;

    public AppModel() { }

    public AppModel(Application application) {
        this(application, false);

    }

    private AppModel(Application application, Boolean summary) {
        this(application.getId(), application.getName(), application.getPublicUrl(), application.getApplicationGroup());
        if(!summary) {
            this.applicationInstances = application.getApplicationInstances().stream().map(AppInstModel::new).collect(toList());
            this.loadBalancers = application.getLoadBalancers().stream().map(LoadBalancerModel::summary).collect(toList());
        }
    }

    public AppModel(Long id, String name, String url, ApplicationGroup applicationGroup) {
        this.id = id;
        this.name = name;
        this.publicURL = url;
        this.applicationGroupId = applicationGroup.getId();
    }

    public static AppModel summary(Application application) {
        return new AppModel(application, true);
    }

     public Long getId() { return id; }
    //public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    //public void setName(String name) { this.name = name; }

    public String getPublicUrl() { return publicURL; }
    //public void setPublicUrl(String url) { this.publicURL = url; }

    public Long getApplicationGroupId() { return applicationGroupId; }

}