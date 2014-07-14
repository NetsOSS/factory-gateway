package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationInstance;

public  class AppInstModel {

    public Long id;

    public String name;
    public String path;
    public String host;
    public Integer port;
    public Long applicationId;


    public AppInstModel() { }

    public AppInstModel(ApplicationInstance applicationInstance) {
        this(applicationInstance, true);
    }

    private AppInstModel(ApplicationInstance applicationInstance, Boolean summary) {
        this(applicationInstance.getId(), applicationInstance.getName(), applicationInstance.getPath(), applicationInstance.getHost(), applicationInstance.getPort(), applicationInstance.getApplication().getId());
        if(!summary) {
            //this.application = new AppModel(applicationInstance.getApplication());
        }
    }

    public AppInstModel(Long id, String name, String path, String host, Integer port, Long applicationId) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.host = host;
        this.port = port;
        this.applicationId = applicationId;
    }

    public static AppInstModel summary(ApplicationInstance applicationInstance) {
        return new AppInstModel(applicationInstance, false);
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getPath() { return path; }

    public String getHost() { return host; }

    public Integer getPort() {return port; }

    public Long getApplicationId() { return applicationId; }
}