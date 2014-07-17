package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.HaProxyState;

import java.net.URL;

public class AppInstModel {

    public Long id;

    public String name;
    public String server;

    public Long applicationId;
    public HaProxyState haProxyState;

    public AppInstModel() {
    }

    public AppInstModel(ApplicationInstance applicationInstance) {
        this(applicationInstance, true);
    }

    private AppInstModel(ApplicationInstance applicationInstance, Boolean summary) {

        this(applicationInstance.getId(), applicationInstance.getName(), applicationInstance.getHost() + ":" + applicationInstance.getPort() + "" + applicationInstance.getPath(), applicationInstance.getApplication().getId());

        haProxyState = applicationInstance.getHaProxyState();
        if (!summary) {
            //this.application = new AppModel(applicationInstance.getApplication());
        }
    }

    public AppInstModel(Long id, String name, String server, Long applicationId) {
        this.id = id;
        this.name = name;
        this.server=server;
        this.applicationId = applicationId;
    }

    public static AppInstModel summary(ApplicationInstance applicationInstance) {
        return new AppInstModel(applicationInstance, false);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getServer() {
        return server;
    }



    public Long getApplicationId() {
        return applicationId;
    }

    public String getHaProxyState() {
        return haProxyState.name();
    }

    public void setHaProxyState(String state) {
        this.haProxyState = HaProxyState.valueOf(state);
    }

}