package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;

public  class AppInstModel {

    public Long id;

    public String name;
    public String path;
    public String host;
    public Integer port;
    public AppModel application;


    public AppInstModel() { }

    public AppInstModel(ApplicationInstance applicationInstance) {
        this(applicationInstance.getId(), applicationInstance.getName());
    }

    public AppInstModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}