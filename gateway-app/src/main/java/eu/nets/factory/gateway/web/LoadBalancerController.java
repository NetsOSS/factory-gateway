package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.*;
import eu.nets.factory.gateway.service.ConfigGeneratorService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.*;

@Controller
@Transactional
public class LoadBalancerController {

    private final Logger log = getLogger(getClass());

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;


    @Autowired
    private ConfigGeneratorService configGeneratorService;

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LoadBalancerModel> listAllLoadBalancers() {
        log.info("LoadBalancerController.list");
        return loadBalancerRepository.findAll().stream().map(LoadBalancerModel::new).collect(toList());

    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LoadBalancerModel> search(@RequestParam(required = false) String name) {
        log.info("LoadBalancerController.search, name={}", name);

        List<LoadBalancer> loadBalancers;

        if (name == null) {
            loadBalancers = loadBalancerRepository.findAll();
        } else {
            loadBalancers = loadBalancerRepository.findByNameLike("%" + name + "%");
        }

        return loadBalancers.stream().map(LoadBalancerModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel findById(@PathVariable Long id) {
        log.info("LoadBalancerController.findById, name={}", id);
        return new LoadBalancerModel(loadBalancerRepository.findOne(id));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/findBySsh/{sshKey}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel findBySshKey(@PathVariable String ssh) {
        log.info("LoadBalancerController.findBySshKey, name={}", ssh);
        List<LoadBalancer> all = loadBalancerRepository.findAll();
        for (LoadBalancer l : all) {
            if (l.getSshKey().equals(ssh)) {
                return new LoadBalancerModel(l);
            }
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel create(@RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.create");

        LoadBalancer loadBalancer = new LoadBalancer(loadBalancerModel.name, loadBalancerModel.host, loadBalancerModel.installationPath, loadBalancerModel.sshKey);
        loadBalancer = loadBalancerRepository.save(loadBalancer);

        return new LoadBalancerModel(loadBalancer.getId(), loadBalancer.getName());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/load-balancers/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("LoadBalancerController.remove");

         loadBalancerRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel update(@PathVariable Long id, @RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.update");

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        loadBalancer.setName(loadBalancerModel.name);
        loadBalancer.setHost(loadBalancerModel.host);
        loadBalancer.setInstallationPath(loadBalancerModel.installationPath);
        loadBalancer.setSshKey(loadBalancerModel.sshKey);

        loadBalancer = loadBalancerRepository.save(loadBalancer);
        return new LoadBalancerModel(loadBalancer.getId(), loadBalancer.getName());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}/applications", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel addApplication(@PathVariable Long id, @RequestBody Long applicationId) {
        log.info("LoadBalancerController.addApplication() LB.id={} , App.id={} ",id,applicationId);
        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        loadBalancer.addApplication(applicationRepository.findOne(applicationId));
        loadBalancer = loadBalancerRepository.save(loadBalancer);
        return new LoadBalancerModel(loadBalancer);
    }
    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> getApplications(@PathVariable Long id) {
        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        return loadBalancer.getApplications().stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}/remove-application", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel removeApplicationFromLoadbalancer(@PathVariable Long id, @RequestBody Long applicationId) {
        log.info("LoadBalancerController.removeApplication() LB.id={} , App.id={} ",id,applicationId);
        Application application = applicationRepository.findOne(applicationId);
        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);

        application.removeLoadBalancer(loadBalancer);
        loadBalancer.removeApplication(application);
        applicationRepository.save(application);
        loadBalancerRepository.save(loadBalancer);
        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/config")
    @ResponseBody
    public String pushConfiguration(HttpServletResponse response, @PathVariable Long id) {
        return configGeneratorService.generateConfig(loadBalancerRepository.findOne(id));
    }
}
