package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;

public  class AppInstModel {

    public Long id;

    public String name;
    public String path;
    public String host;
    public Integer port;
    public Long applicationId;
    //public AppModel application;


    public AppInstModel() { }

    public AppInstModel(ApplicationInstance applicationInstance) {
        this(applicationInstance.getId(), applicationInstance.getName(), applicationInstance.getPath(), applicationInstance.getHost(), applicationInstance.getPort(), applicationInstance.getApplication().getId());
    }

    public AppInstModel(Long id, String name, String path, String host, Integer port, Long applicationId) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.host = host;
        this.port = port;
        this.applicationId = applicationId;
    }
}