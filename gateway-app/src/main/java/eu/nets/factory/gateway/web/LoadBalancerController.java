package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import eu.nets.factory.gateway.model.LoadBalancer_;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
public class LoadBalancerController {

    private final Logger log = getLogger(getClass());

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/data/loadbalancers",  produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LoadBalancer> listAllLoadBalancers() {
        log.info("LoadBalancerController.list");
        return loadBalancerRepository.findAll();

    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/loadbalancers/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancer findLoadBalancerById(@PathVariable Long id) {
        log.info("LoadBalancerController.findLoadBalancerById, name={}", id);
        return loadBalancerRepository.findOne(id);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/data/loadbalancers/{sshKey}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancer findLoadBalancerBySshKey(@PathVariable String ssh) {
        log.info("LoadBalancerController.findLoadBalancerById, name={}", ssh);
        List<LoadBalancer> all = loadBalancerRepository.findAll();
        for(LoadBalancer l: all) {
            if(l.getSshKey().equals(ssh)) {
                return l;
            }
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/loadbalancers", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel create(@RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.create");
        LoadBalancer loadBalancer = new LoadBalancer(loadBalancerModel.getName(), loadBalancerModel.getHost(), loadBalancerModel.getInstallationPath(), loadBalancerModel.getSshKey());
        loadBalancer = loadBalancerRepository.save(loadBalancer);
        return new LoadBalancerModel(loadBalancer.getId(), loadBalancer.getName());
    }

    public static class LoadBalancerModel {

        Long id;
        String name;
        String host;
        String installationPath;
        String sshKey;

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

        public LoadBalancerModel() {

        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getInstallationPath() {
            return installationPath;
        }

        public void setInstallationPath(String installationPath) {
            this.installationPath = installationPath;
        }

        public String getSshKey() {
            return sshKey;
        }

        public void setSshKey(String sshKey) {
            this.sshKey = sshKey;
        }
    }
}
