package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationGroup;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class AppGroupModel {

    public Long id;

    public String name;
    public int port;
    public List<AppModel> applications;

    public AppGroupModel() {
    }

    public AppGroupModel(ApplicationGroup applicationGroup) {
        this(applicationGroup, false);
    }

    private AppGroupModel(ApplicationGroup applicationGroup, Boolean summary) {
        this(applicationGroup.getId(), applicationGroup.getName(), applicationGroup.getPort());
        if (!summary) {
            this.applications = applicationGroup.getApplications().stream().map(AppModel::summary).collect(toList());
        }
    }

    public AppGroupModel(Long id, String name, int port) {
        this.id = id;
        this.name = name;
        this.port = port;
    }

    public static AppGroupModel summary(ApplicationGroup applicationGroup) {
        return new AppGroupModel(applicationGroup, false);
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public List<AppModel> getApplications() {
        return applications;
    }
}