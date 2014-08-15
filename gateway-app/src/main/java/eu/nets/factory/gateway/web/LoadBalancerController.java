package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import eu.nets.factory.gateway.service.ConfigGeneratorService;
import eu.nets.factory.gateway.service.HaProxyService;
import eu.nets.factory.gateway.service.StatusService;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
@Transactional
public class LoadBalancerController {

    private final Logger log = getLogger(getClass());

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private ConfigGeneratorService configGeneratorService;

    @Autowired
    private HaProxyService haProxyService;

    @Autowired
    StatusService statusService;


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

        if (id == null) {
            throw new EntityNotFoundException("LoadBalancer", id);
        }

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        if (loadBalancer == null) {
            throw new EntityNotFoundException("LoadBalancer", id);
        }

        return loadBalancer;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/models", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel findById(@PathVariable Long id) {
        log.info("LoadBalancerController.findById, id={}", id);

        return new LoadBalancerModel(findEntityById(id));
    }

    private void assertNameUnique(String name) {
        log.info("LoadBalancerController.assertNameUnique, name={}", name);

        if (loadBalancerRepository.countByName(name) > 0L) {
            throw new GatewayException("Could not create Load Balancer. Name '" + name + "' is already in use.");
        }
    }

    private void assertHostInstallationPathUnique(String host, String installationPath) {
        if (loadBalancerRepository.countByHostInstallationPath(host, installationPath) > 0L) {
            throw new GatewayException("Could not create Load Balancer. Combination host '" + host + "' - installation path '" + installationPath + "' is already in use.");
        }
    }

    private void assertValidModel(LoadBalancerModel loadBalancerModel) {
        if (loadBalancerModel == null) {
            throw new GatewayException("Could not create LoadBalancer. Invalid LoadBalancerModel.");
        }
        if (loadBalancerModel.getName() == null || !Pattern.matches("^\\S+$", loadBalancerModel.getName()))
            throw new GatewayException("Could not create Load Balancer. Name must match pattern '^\\S+$'.");
        //TODO: test Name for symbols the config file can't handle, such as æ, ø and å
        if (loadBalancerModel.getHost() == null || !Pattern.matches(".+", loadBalancerModel.getHost()))
            throw new GatewayException("Could not create Load Balancer. Host must match pattern '.+'.");
        if (loadBalancerModel.getInstallationPath() == null || !Pattern.matches("^/[a-zA-Z]\\S*$", loadBalancerModel.getInstallationPath()))
            throw new GatewayException("Could not create Load Balancer. Installation Path must match pattern '^/[a-zA-Z]\\S*$'.");
        if (loadBalancerModel.getSshKey() == null || !Pattern.matches("[\\s\\S]+", loadBalancerModel.getSshKey()))
            throw new GatewayException("Could not create Load Balancer. Ssh Key must match pattern '.+'.");
        if (loadBalancerModel.clientTimeout != loadBalancerModel.serverTimeout) {
            throw new GatewayException("Could not create LoadBalancer. Servertimeout must be equal to clientTimeout: " + loadBalancerModel.clientTimeout + "!=" + loadBalancerModel.serverTimeout);
        }
    }

    private int generatePortValue(String host) {
        Random r = new Random(System.currentTimeMillis());
        for (int port = LoadBalancer.STATS_PORT_MIN; port <= LoadBalancer.STATS_PORT_MAX; port++) {
            int newPort = r.nextInt(LoadBalancer.STATS_PORT_MAX - LoadBalancer.STATS_PORT_MIN) + LoadBalancer.STATS_PORT_MIN;
            if (loadBalancerRepository.countByHostPublicPort(host, newPort) == 0L) return newPort;
        }

        throw new GatewayException("Could not create Load Balancer. All port allocated to Load Balancer stats are already in use for host '" + host + "'.");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel create(@RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.create");


        assertValidModel(loadBalancerModel);
        assertNameUnique(loadBalancerModel.name);
        assertHostInstallationPathUnique(loadBalancerModel.host, loadBalancerModel.installationPath);
        loadBalancerModel.statsPort = generatePortValue(loadBalancerModel.host);

        LoadBalancer loadBalancer = new LoadBalancer(loadBalancerModel.name, loadBalancerModel.host, loadBalancerModel.installationPath, loadBalancerModel.sshKey, loadBalancerModel.statsPort, loadBalancerModel.userName, loadBalancerModel.checkTimeout, loadBalancerModel.connectTimeout, loadBalancerModel.serverTimeout, loadBalancerModel.clientTimeout, loadBalancerModel.retries);
        loadBalancer = loadBalancerRepository.save(loadBalancer);

        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/load-balancers/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("LoadBalancerController.remove, id={}", id);

        LoadBalancer loadBalancer = findEntityById(id);

        for (Application application : loadBalancer.getApplications()) {
            application.removeLoadBalancer((loadBalancer));
        }
        stopLoadBalancer(id);
        loadBalancerRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel update(@PathVariable Long id, @RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.update, id={}", id);

        assertValidModel(loadBalancerModel);
        if (id == null) throw new GatewayException("Could not create Load Balancer. Invalid ID: " + id);
        if (!id.equals(loadBalancerModel.getId()))
            throw new GatewayException("Could not create Load Balancer. IDs did not match: " + id + " - " + loadBalancerModel.getId());

        LoadBalancer loadBalancer = findEntityById(id);
        if (!(loadBalancer.getName().equals(loadBalancerModel.name))) {
            assertNameUnique(loadBalancerModel.name);
        }
        if (!(loadBalancer.getHost().equals(loadBalancerModel.host) && loadBalancer.getInstallationPath().equals(loadBalancerModel.installationPath))) {
            assertHostInstallationPathUnique(loadBalancerModel.host, loadBalancerModel.installationPath);
        }
        if (!loadBalancer.getHost().equals(loadBalancerModel.host) && loadBalancerRepository.countByHostPublicPort(loadBalancerModel.host, loadBalancerModel.statsPort) != 0L) {
            loadBalancer.setStatsPort(generatePortValue(loadBalancerModel.host));
        }

        loadBalancer.setName(loadBalancerModel.name);
        loadBalancer.setHost(loadBalancerModel.host);
        loadBalancer.setInstallationPath(loadBalancerModel.installationPath);
        loadBalancer.setSshKey(loadBalancerModel.sshKey);
        loadBalancer.setUserName(loadBalancerModel.userName);
        loadBalancer.setCheckTimeout(loadBalancerModel.checkTimeout);
        loadBalancer.setConnectTimeout(loadBalancerModel.connectTimeout);
        loadBalancer.setClientTimeout(loadBalancerModel.clientTimeout);
        loadBalancer.setServerTimeout(loadBalancerModel.serverTimeout);
        loadBalancer.setRetries(loadBalancerModel.retries);

        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}/applications", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel addApplication(@PathVariable Long id, @RequestBody Long applicationId) {
        log.info("LoadBalancerController.addApplication() LB.id={} , App.id={} ", id, applicationId);

        LoadBalancer loadBalancer = findEntityById(id);
        Application application = applicationController.findEntityById(applicationId);

        if (loadBalancer.getApplications().contains(application))
            throw new GatewayException("The given Application is already linked to the Load Balancer.");

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

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/getConfigString")
    @ResponseBody
    public String generateConfiguration(@PathVariable Long id) {

        LoadBalancer loadBalancer = findEntityById(id);
        return configGeneratorService.generateConfig(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers/{id}/start", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel startLoadBalancer(@PathVariable Long id) {
        log.info("LoadBalancerController.startLoadBalancer() id={}", id);

        LoadBalancer loadBalancer = findEntityById(id);
        haProxyService.pushConfigFile(loadBalancer);
        haProxyService.start(loadBalancer);
        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers/{id}/stop", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel stopLoadBalancer(@PathVariable Long id) {
        log.info("LoadBalancerController.stopLoadBalancer() id={}", id);

        LoadBalancer loadBalancer = findEntityById(id);
        if (statusService.getStatusForLoadBalancer(id) != null && statusService.getStatusForLoadBalancer(id).up) {
            haProxyService.stop(loadBalancer);
        }
        return new LoadBalancerModel(loadBalancer);
    }
}
