package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class LoadBalancerModel {

    public Long id;
    public String name;
    public String installationPath;
    public String host;
    public String sshKey;
    public int publicPort;
    public List<AppModel> applications = new ArrayList<AppModel>();


    public LoadBalancerModel() { }

    public LoadBalancerModel(LoadBalancer loadBalancer) {
        this(loadBalancer, false);
    }

    private LoadBalancerModel(LoadBalancer loadBalancer, Boolean summary) {
        this(loadBalancer.getId(), loadBalancer.getName(), loadBalancer.getHost(), loadBalancer.getInstallationPath(), loadBalancer.getSshKey(), loadBalancer.getPublicPort());
        if(!summary) {

            applications = loadBalancer.getApplications().stream().map(AppModel::summary).collect(toList());

        }
    }
/*
    public LoadBalancerModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }
*/
    public LoadBalancerModel(Long id, String name, String host, String installationPath, String sshKey, int publicPort) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
        this.publicPort = publicPort;
    }

    public static LoadBalancerModel summary(LoadBalancer loadBalancer) {
        return new LoadBalancerModel(loadBalancer, true);
    }
}
