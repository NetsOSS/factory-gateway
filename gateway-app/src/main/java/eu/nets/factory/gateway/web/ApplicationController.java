package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Transactional
@Controller
public class ApplicationController {

    private final Logger log = getLogger(getClass());

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;

    @Autowired
    private ApplicationInstanceRepository applicationInstanceRepository;

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;


    @RequestMapping(method = RequestMethod.GET, value = "/data/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> listAllApps() {
        log.info("ApplicationController.listAllApps");
        return  applicationRepository.findAll().stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/find/{name}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> search(@PathVariable String name) {
        log.info("ApplicationController.search, name={}", name);

        List<Application> applications;

        if (name == null) {
            applications = applicationRepository.findAll();
        } else {
            applications = applicationRepository.findByNameLike("%" + name + "%");
        }

        return applications.stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Application findEntityById(@PathVariable Long id) {
        log.info("ApplicationController.findEntityById, id={}", id);

        Application application = applicationRepository.findOne(id);
        if(application == null) { throw new EntityNotFoundException("Application", id); }

        return application;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/models", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel findById(@PathVariable Long id) {
        log.info("ApplicationController.findById, id={}", id);

        return new AppModel(findEntityById(id));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/applications", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel create(@RequestBody AppModel applicationModel) {
        log.info("ApplicationController.create");

        assertNameUnique(applicationModel.name);

        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(applicationModel.getApplicationGroupId());
        Application application = new Application(applicationModel.getName(), applicationModel.getPublicUrl(), applicationGroup,applicationModel.getEmails());
        application = applicationRepository.save(application);

        applicationGroup.addApplication(application);
        applicationGroupRepository.save(applicationGroup);

        return new AppModel(application);
    }

    private void assertNameUnique(String name) {
        if(applicationRepository.countByName(name) > 0L) {
            throw new GatewayException("Could not create Application. Name '" + name + "' already exists.");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/applications/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationController.remove, id={}", id);

        Application application = findEntityById(id);
        List<ApplicationInstance> instances = application.getApplicationInstances();
        for(ApplicationInstance instance: instances) {
            applicationInstanceRepository.delete(instance);
        }
        List<LoadBalancer> loadBalancers = application.getLoadBalancers();
        for(Iterator<LoadBalancer> it = loadBalancers.iterator(); it.hasNext();) {
            LoadBalancer l = it.next();
            l.removeApplication(application);
            //it.remove();
            loadBalancerRepository.save(l);
        }
        ApplicationGroup group = application.getApplicationGroup();
        group.removeApplication(application);

        applicationGroupRepository.save(group);
        applicationRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/applications/{id}", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel update(@PathVariable Long id, @RequestBody AppModel appModel) {
        log.info("ApplicationController.update, id={}", id);

        Application application = findEntityById(id);
        if(!(application.getName().equals(appModel.name))) { assertNameUnique(appModel.name); }

        application.setName(appModel.getName());
        application.setPublicUrl(appModel.getPublicUrl());
        application.setEmails(appModel.getEmails());

        application = applicationRepository.save(application);
        return new AppModel(application);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/application-group", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel getApplicationGroup(@PathVariable Long id) {
        log.info("ApplicationController.getApplicationGroup, id={}", id);

        Application application = findEntityById(id);
        return new AppGroupModel(application.getApplicationGroup());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/load-balancers", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LoadBalancerModel> getLoadBalancers(@PathVariable Long id) {
        log.info("ApplicationController.getLoadBalancers, id={}", id);

        Application application = findEntityById(id);
        return application.getLoadBalancers().stream().map(LoadBalancerModel::new).collect(toList());
    }
}