package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;

import java.util.ArrayList;
import java.util.List;

public class LoadBalancerModel {

    public Long id;
    public String name;
    public String installationPath;
    public String host;
    public String sshKey;
    List<AppModel> applications = new ArrayList<AppModel>();


    public LoadBalancerModel() { }

    public LoadBalancerModel(LoadBalancer loadBalancer) {
        this(loadBalancer.getId(), loadBalancer.getName(), loadBalancer.getHost(), loadBalancer.getInstallationPath(), loadBalancer.getSshKey());
    }

    public LoadBalancerModel(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public LoadBalancerModel(Long id, String name, String host, String installationPath, String sshKey) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
    }
}