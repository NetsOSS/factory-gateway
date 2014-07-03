package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.*;
import eu.nets.factory.gateway.service.ConfigGeneratorService;
import eu.nets.factory.gateway.service.FileWriterService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.*;

@Controller
@Transactional
public class LoadBalancerController {

    private final Logger log = getLogger(getClass());

    private static final String CFG_FILE = "haproxy.cfg";
    private static final String PID_FILE = "haproxy.pid";

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ConfigGeneratorService configGeneratorService;

    @Autowired
    private FileWriterService fileWriterService;

    @Autowired
    private StatusController statusController;

    @Autowired
    private GatewaySettings settings;

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
    public LoadBalancerModel findById(@PathVariable Long id) {
        log.info("LoadBalancerController.findById, id={}", id);

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        if(loadBalancer == null) { throw new EntityNotFoundException("LoadBalancer", id); }
        return new LoadBalancerModel(loadBalancer);
    }

    private void assertValidId(Long id) {
        findById(id);
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

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel create(@RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.create");

        assertNameUnique(loadBalancerModel.name);
        assertHostInstallationPathUnique(loadBalancerModel.host, loadBalancerModel.installationPath);
        assertHostPublicPortUnique(loadBalancerModel.host, loadBalancerModel.publicPort);

        LoadBalancer loadBalancer = new LoadBalancer(loadBalancerModel.name, loadBalancerModel.host, loadBalancerModel.installationPath, loadBalancerModel.sshKey, loadBalancerModel.publicPort);
        loadBalancer = loadBalancerRepository.save(loadBalancer);

        return new LoadBalancerModel(loadBalancer); //.getId(), loadBalancer.getName());
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

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/load-balancers/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("LoadBalancerController.remove, id={}", id);

        assertValidId(id);

        List<Application> applications = loadBalancerRepository.findOne(id).getApplications();
        for(Application application : applications) {
            application.removeLoadBalancer(loadBalancerRepository.findOne(id));
        }

         loadBalancerRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel update(@PathVariable Long id, @RequestBody LoadBalancerModel loadBalancerModel) {
        log.info("LoadBalancerController.update, id={}", id);

        assertValidId(id);
        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        if(!(loadBalancer.getName().equals(loadBalancerModel.name))) { assertNameUnique(loadBalancerModel.name); }
        if(!(loadBalancer.getHost().equals(loadBalancerModel.host) && loadBalancer.getInstallationPath().equals(loadBalancerModel.installationPath))) { assertHostInstallationPathUnique(loadBalancerModel.host, loadBalancerModel.installationPath); }
        if(!(loadBalancer.getHost().equals(loadBalancerModel.host) && loadBalancer.getPublicPort() == loadBalancerModel.publicPort)) { assertHostPublicPortUnique(loadBalancerModel.host, loadBalancerModel.publicPort); }

        loadBalancer.setName(loadBalancerModel.name);
        loadBalancer.setHost(loadBalancerModel.host);
        loadBalancer.setInstallationPath(loadBalancerModel.installationPath);
        loadBalancer.setSshKey(loadBalancerModel.sshKey);
        loadBalancer.setPublicPort(loadBalancerModel.publicPort);

        loadBalancer = loadBalancerRepository.save(loadBalancer);
        return new LoadBalancerModel(loadBalancer); //.getId(), loadBalancer.getName());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}/applications", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel addApplication(@PathVariable Long id, @RequestBody Long applicationId) {
        log.info("LoadBalancerController.addApplication() LB.id={} , App.id={} ", id, applicationId);

        assertValidId(id);
        if(applicationRepository.findOne(applicationId) == null) {
            throw new EntityNotFoundException("Application", applicationId);
        }

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        loadBalancer.addApplication(applicationRepository.findOne(applicationId));
        loadBalancer = loadBalancerRepository.save(loadBalancer);
        Application application = applicationRepository.findOne(applicationId);
        application.addLoadBalancer(loadBalancer);
        return new LoadBalancerModel(loadBalancer);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> getApplications(@PathVariable Long id) {
        log.info("LoadBalancerController.getApplications, id={}", id);

        assertValidId(id);

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        return loadBalancer.getApplications().stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/load-balancers/{id}/remove-application", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public LoadBalancerModel removeApplicationFromLoadbalancer(@PathVariable Long id, @RequestBody Long applicationId) {
        log.info("LoadBalancerController.removeApplication(), id={} , App.id={}", id, applicationId);

        assertValidId(id);
        if(applicationRepository.findOne(applicationId) == null) {
            throw new EntityNotFoundException("Application", applicationId);
        }

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
    public String pushConfiguration(@PathVariable Long id) {
        log.info("LoadBalancerController.pushConfiguration() id={}", id);

        assertValidId(id);

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        String installationPath = loadBalancer.getInstallationPath();

        String strConfig = configGeneratorService.generateConfig(loadBalancer);
        fileWriterService.writeConfigFile(installationPath, CFG_FILE, strConfig);
        return strConfig;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers/{id}/start")
    public void startLoadBalancer(@PathVariable Long id) {
        log.info("LoadBalancerController.startLoadBalancer() id={}", id);

        assertValidId(id);

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);

        start(loadBalancer);
    }

    private String start(LoadBalancer loadBalancer) {
        String installationPath = loadBalancer.getInstallationPath();

        String bin = settings.getHaproxyBin();

        // Start HAProxy
        String command = bin + " -f " + installationPath + "/" + CFG_FILE + " -p " + installationPath + "/" + PID_FILE + " -sf $(cat " + installationPath + "/" + PID_FILE + ")";

        log.debug(command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            log.debug("Is process is alive? - " + process.isAlive());
        } catch (IOException e) {
            String errorMessage = "Loadbalancer could not be started: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
        log.info("started LoadBalancer");
        return "success";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/load-balancers/{id}/stats")
    public void getStatus(@PathVariable Long id) {
        log.info("LoadBalancerController.getStatus() LB.id={}",id);

        assertValidId(id);
        statusController.getStatusForLoadbalancer(id);
    }
}
