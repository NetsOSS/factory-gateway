package eu.nets.factory.gateway.web;

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
    public String userName;
    public int publicPort;
    public List<AppModel> applications = new ArrayList<>();


    public LoadBalancerModel() { }

    public LoadBalancerModel(LoadBalancer loadBalancer) {
        this(loadBalancer, false);
    }

    private LoadBalancerModel(LoadBalancer loadBalancer, Boolean summary) {
        this(loadBalancer.getId(), loadBalancer.getName(), loadBalancer.getHost(), loadBalancer.getInstallationPath(), loadBalancer.getSshKey(), loadBalancer.getPublicPort(), loadBalancer.getUserName());
        if(!summary) {
            this.applications = loadBalancer.getApplications().stream().map(AppModel::summary).collect(toList());
        }
    }

    public LoadBalancerModel(Long id, String name, String host, String installationPath, String sshKey, int publicPort, String userName) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.installationPath = installationPath;
        this.sshKey = sshKey;
        this.userName = userName;
        this.publicPort = publicPort;
    }

    public static LoadBalancerModel summary(LoadBalancer loadBalancer) {
        return new LoadBalancerModel(loadBalancer, true);
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getInstallationPath() { return installationPath; }

    public String getHost() { return host; }

    public String getSshKey() { return sshKey; }

    public String getUserName() { return userName; }

    public List<AppModel> getApplications() { return applications; }
}
