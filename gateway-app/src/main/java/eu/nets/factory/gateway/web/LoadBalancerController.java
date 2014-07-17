package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import eu.nets.factory.gateway.service.ConfigGeneratorService;
import eu.nets.factory.gateway.service.FileWriterService;
import eu.nets.factory.gateway.service.HaProxyService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
@Transactional
public class LoadBalancerController {

    private final Logger log = getLogger(getClass());

    private static final String CFG_FILE = "haproxy.cfg";

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private ConfigGeneratorService configGeneratorService;

    @Autowired
    private FileWriterService fileWriterService;

    @Autowired
    private HaProxyService haProxyService;


    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LoadBalancerModel> listAllLoadBalancers() {
        log.info("LoadBalancerController.listAllLoadBalancers");
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
    public LoadBalancer findEntityById(@PathVariable Long id) {
        log.info("LoadBalancerController.findEntityById, id={}", id);

        if(id == null) { throw new EntityNotFoundException("LoadBalancer", id); }

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        if(loadBalancer == null) { throw new EntityNotFoundException("LoadBalancer", id); }

        return loadBalancer;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/models", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel findById(@PathVariable Long id) {
        log.info("LoadBalancerController.findById, id={}", id);

        return new LoadBalancerModel(findEntityById(id));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/findBySsh/{sshKey}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel findBySshKey(@PathVariable String sshKey) {
        log.info("LoadBalancerController.findBySshKey, sshKey={}", sshKey);
        List<LoadBalancer> all = loadBalancerRepository.findAll();
        for (LoadBalancer l : all) {
            if (l.getSshKey().equals(sshKey)) {
                return new LoadBalancerModel(l);
            }
        }

        throw new EntityNotFoundException("LoadBalancer", sshKey);
    }

    private void assertNameUnique(String name) {
        log.info("LoadBalancerController.assertNameUnique, name={}", name);

        if(loadBalancerRepository.countByName(name) > 0L) {
            throw new GatewayException("Could not create Load Balancer. Name '" + name + "' already exists.");
        }
    }

    private void assertHostInstallationPathUnique(String host, String installationPath) {
        log.info("LoadBalancerController.assertHostInstallationPathUnique, host={}, installationPath={}", host, installationPath);

        if(loadBalancerRepository.countByHostInstallationPath(host, installationPath) > 0L) {
            throw new GatewayException("Could not create Load Balancer. Combination host '" + host + "' - installation path '" + installationPath + "' already exists.");
        }
    }

    private void assertHostPublicPortUnique(String host, int publicPort) {
        log.info("LoadBalancerController.assertHostPublicPortUnique, host={}, publicPort={}", host, publicPort);

        if(loadBalancerRepository.countByHostPublicPort(host, publicPort) > 0L) {
            throw new GatewayException("Could not create Load Balancer. Combination host '" + host + "' - public port '" + publicPort + "' already exists.");
        }
    }

    private void assertValidModel(LoadBalancerModel loadBalancerModel) {
        if(loadBalancerModel == null) { throw new GatewayException("Could not create LoadBalancer. Invalid LoadBalancerModel."); }
        /*
        if(loadBalancerModel.getName() == null) throw new GatewayException("Could not create Load Balancer. Invalid name: " + loadBalancerModel.getName());
        if(loadBalancerModel.getHost() == null) throw new GatewayException("Could not create Load Balancer. Invalid host: " + loadBalancerModel.getHost());
        if(loadBalancerModel.getInstallationPath() == null) throw new GatewayException("Could not create Load Balancer. Invalid installation path: " + loadBalancerModel.getInstallationPath());
        if(loadBalancerModel.getSshKey() == null) throw new GatewayException("Could not create Load Balancer. invalid ssh key: " + loadBalancerModel.getSshKey());
        */
        if(loadBalancerModel.getName() == null  || ! Pattern.matches("^\\S+$", loadBalancerModel.getName())) throw new GatewayException("Could not create Load Balancer. Name must match pattern '^\\S+$'.");
        if(loadBalancerModel.getHost() == null || ! Pattern.matches(".+", loadBalancerModel.getHost())) throw new GatewayException("Could not create Load Balancer. Host must match pattern '.+'.");
        if(loadBalancerModel.getInstallationPath() == null  || ! Pattern.matches("^/[a-zA-Z]\\S*$", loadBalancerModel.getInstallationPath())) throw new GatewayException("Could not create Load Balancer. Installation Path must match pattern '^/[a-zA-Z]\\S*$'.");
        if(loadBalancerModel.getSshKey() == null || ! Pattern.matches(".+", loadBalancerModel.getSshKey())) throw new GatewayException("Could not create Load Balancer. Ssh Key must match pattern '.+'.");
        if(loadBalancerModel.publicPort < 1 || loadBalancerModel.publicPort > 65535) throw new GatewayException("Could not create ApplicationInstance. Public Port must be a number between 1 and 65535. Received: " + loadBalancerModel.publicPort);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel create(@RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.create");

        assertValidModel(loadBalancerModel);
        assertNameUnique(loadBalancerModel.name);
        assertHostInstallationPathUnique(loadBalancerModel.host, loadBalancerModel.installationPath);
        assertHostPublicPortUnique(loadBalancerModel.host, loadBalancerModel.publicPort);

        LoadBalancer loadBalancer = new LoadBalancer(loadBalancerModel.name, loadBalancerModel.host, loadBalancerModel.installationPath, loadBalancerModel.sshKey, loadBalancerModel.publicPort);
        loadBalancer = loadBalancerRepository.save(loadBalancer);

        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/load-balancers/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("LoadBalancerController.remove, id={}", id);

        LoadBalancer loadBalancer = findEntityById(id);

        for(Application application : loadBalancer.getApplications()) {
            application.removeLoadBalancer((loadBalancer));
        }

         loadBalancerRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel update(@PathVariable Long id, @RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.update, id={}", id);

        assertValidModel(loadBalancerModel);
        if(id == null) throw new GatewayException("Could not create Load Balancer. Invalid ID: " + id);
        if(! id.equals(loadBalancerModel.getId())) throw new GatewayException("Could not create Load Balancer. IDs did not match: " + id + " - " + loadBalancerModel.getId());

        LoadBalancer loadBalancer = findEntityById(id);
        if(!(loadBalancer.getName().equals(loadBalancerModel.name))) { assertNameUnique(loadBalancerModel.name); }
        if(!(loadBalancer.getHost().equals(loadBalancerModel.host) && loadBalancer.getInstallationPath().equals(loadBalancerModel.installationPath))) { assertHostInstallationPathUnique(loadBalancerModel.host, loadBalancerModel.installationPath); }
        if(!(loadBalancer.getHost().equals(loadBalancerModel.host) && loadBalancer.getPublicPort() == loadBalancerModel.publicPort)) { assertHostPublicPortUnique(loadBalancerModel.host, loadBalancerModel.publicPort); }

        loadBalancer.setName(loadBalancerModel.name);
        loadBalancer.setHost(loadBalancerModel.host);
        loadBalancer.setInstallationPath(loadBalancerModel.installationPath);
        loadBalancer.setSshKey(loadBalancerModel.sshKey);
        loadBalancer.setPublicPort(loadBalancerModel.publicPort);

        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}/applications", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel addApplication(@PathVariable Long id, @RequestBody Long applicationId) {
        log.info("LoadBalancerController.addApplication() LB.id={} , App.id={} ", id, applicationId);

        LoadBalancer loadBalancer = findEntityById(id);
        Application application = applicationController.findEntityById(applicationId);

        if(loadBalancer.getApplications().contains(application)) throw new GatewayException("The given Application is already linked to the Load Balancer.");

        loadBalancer.addApplication(application);
        application.addLoadBalancer(loadBalancer);

        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> getApplications(@PathVariable Long id) {
        log.info("LoadBalancerController.getApplications, id={}", id);

        LoadBalancer loadBalancer = findEntityById(id);
        return loadBalancer.getApplications().stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}/remove-application", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel removeApplicationFromLoadbalancer(@PathVariable Long id, @RequestBody Long applicationId) {
        log.info("LoadBalancerController.removeApplication(), id={} , App.id={}", id, applicationId);

        LoadBalancer loadBalancer = findEntityById(id);
        Application application = applicationController.findEntityById(applicationId);

        application.removeLoadBalancer(loadBalancer);
        loadBalancer.removeApplication(application);

        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/config")
    @ResponseBody
    public String pushConfiguration(@PathVariable Long id) {
        log.info("LoadBalancerController.pushConfiguration() id={}", id);

        LoadBalancer loadBalancer = findEntityById(id);
        String installationPath = loadBalancer.getInstallationPath();

        String strConfig = configGeneratorService.generateConfig(loadBalancer);
        fileWriterService.writeConfigFile(installationPath, CFG_FILE, strConfig);

        return strConfig;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers/{id}/start", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel startLoadBalancer(@PathVariable Long id) {
        log.info("LoadBalancerController.startLoadBalancer() id={}", id);

        LoadBalancer loadBalancer = findEntityById(id);
        haProxyService.start(loadBalancer);
        return new LoadBalancerModel(loadBalancer);
    }
}
