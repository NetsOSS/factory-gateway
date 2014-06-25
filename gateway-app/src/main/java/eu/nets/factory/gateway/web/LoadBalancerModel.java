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
        this(loadBalancer.getId(), loadBalancer.getName(), loadBalancer.getHost(), loadBalancer.getInstallationPath(), loadBalancer.getSshKey(), loadBalancer.getPublicPort(), loadBalancer.getApplications());
    }

    public LoadBalancerModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public LoadBalancerModel(Long id, String name, String host, String installationPath, String sshKey, int publicPort, List<Application> applications) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
        this.publicPort = publicPort;
        this.applications = applications.stream().map(AppModel::new).collect(toList());
    }
}