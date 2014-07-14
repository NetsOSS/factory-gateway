package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationGroup;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class AppGroupModel {

    public Long id;

    public String name;
    public List<AppModel> applications;

    public AppGroupModel() { }

    public AppGroupModel(ApplicationGroup applicationGroup) {
        this(applicationGroup, false);
    }

    private AppGroupModel(ApplicationGroup applicationGroup, Boolean summary) {
        this(applicationGroup.getId(), applicationGroup.getName());
        if(!summary) {
            this.applications = applicationGroup.getApplications().stream().map(AppModel::summary).collect(toList());
        }
    }

    public AppGroupModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static AppGroupModel summary(ApplicationGroup applicationGroup) {
        return new AppGroupModel(applicationGroup, false);
    }


    public Long getId() { return id; }
    //public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    //public void setName(String name) { this.name = name; }

    public List<AppModel> getApplications() { return applications; }
}