package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.web.AppInstModel;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public  class AppModel {

    public Long id;

    public String name;
    public String publicURL;
    public List<AppInstModel> applications = new ArrayList<>();
    public List<Long> instanceIds = new ArrayList<>();

    public AppModel() { }

    public AppModel(Application app) {
        this(app.getId(), app.getName(), app.getPublicUrl());
         applications=app.getApplicationInstances().stream().map(AppInstModel::new).collect(toList());
       // instanceIds=app.getApplicationInstances().stream().map(ApplicationInstance::getId).collect(toList());
    }

    public AppModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public AppModel(Long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.publicURL = url;
    }


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPublicUrl() { return publicURL; }

    public void setPublicUrl(String url) { this.publicURL = url; }
}