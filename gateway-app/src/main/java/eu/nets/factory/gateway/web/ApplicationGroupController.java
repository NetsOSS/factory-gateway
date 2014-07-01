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

/**
 * Created by kwlar on 19.06.2014.
 */

@Controller
@Transactional
public class ApplicationGroupController {

    private final Logger log = getLogger(getClass());
    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    @Autowired
    private ApplicationInstanceRepository applicationInstanceRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppGroupModel> listAllAppGroups() {
        log.info("ApplicationGroupController.list");
        return  applicationGroupRepository.findAll().stream().map(AppGroupModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-group/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppGroupModel> search(@RequestParam(required = false) String name) {
        log.info("ApplicationGroupController.search, name={}", name);

        List<ApplicationGroup> applicationGroups;

        if (name == null) {
            applicationGroups = applicationGroupRepository.findAll();
        } else {
            applicationGroups = applicationGroupRepository.findByNameLike("%" + name + "%");
        }

        return applicationGroups.stream().map(AppGroupModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-group/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel findById(@PathVariable Long id) {
        log.info("ApplicationGroupController.findById, name={}", id);

        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(id);
        if(applicationGroup == null) { throw new EntityNotFoundException("ApplicationGroup", id); }
        return new AppGroupModel(applicationGroup);
    }

    private void assertValidId(Long id) {
        findById(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/application-group", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel create(@RequestBody AppGroupModel appGroupModel) {
        log.info("ApplicationGroupController.create");

        assertNameUnique(appGroupModel.name);

        ApplicationGroup applicationGroup = new ApplicationGroup(appGroupModel.getName());
        applicationGroup = applicationGroupRepository.save(applicationGroup);
        return new AppGroupModel(applicationGroup);
    }

    private void assertNameUnique(String name) {
        if(applicationGroupRepository.countByName(name) > 0L) {
            throw new GatewayException("Could not create Application Group. Name '" + name + "' already exists.");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/application-groups/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationGroupController.remove");

        assertValidId(id);

        ApplicationGroup applicationGroup =  applicationGroupRepository.findOne(id);
        List<Application> list = applicationGroup.getApplications();

        for(Iterator<Application> it = list.iterator(); it.hasNext();) {
            Application application = it.next();
            List<ApplicationInstance> instances = application.getApplicationInstances();
            for(ApplicationInstance instance: instances) {
                applicationInstanceRepository.delete(instance);

                List<LoadBalancer> loadBalancers = application.getLoadBalancers();
                for(Iterator<LoadBalancer> loadIt = loadBalancers.iterator(); loadIt.hasNext();) {
                    LoadBalancer l = loadIt.next();
                    loadIt.remove();
                    loadBalancerRepository.save(l);
                }
            }
            it.remove();
            applicationRepository.delete(application.getId());
        }

        applicationGroupRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/application-groups/{id}", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel update(@PathVariable Long id, @RequestBody AppGroupModel appGroupModel) {
        log.info("ApplicationGroupController.update");

        assertValidId(id);
        assertNameUnique(appGroupModel.name);

        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(id);
        applicationGroup.setName(appGroupModel.name);

        applicationGroup = applicationGroupRepository.save(applicationGroup);
        return new AppGroupModel(applicationGroup);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups/{id}/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> getApplications(@PathVariable Long id) {
        log.info("ApplicationGroupController.getApplications() LORD   id= {}",id);

        assertValidId(id);

        ApplicationGroup g = applicationGroupRepository.findOne(id);
        log.info("ApplicationGroupController.getApplications() : isNull ? {} ",g==null);
        log.info("ApplicationGroupController.getApplications() : name {}",g.getName());

        return applicationGroupRepository.findOne(id).getApplications().stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/application-groups/{id}/remove-application", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel removeApplication(@PathVariable Long id, @RequestBody Long appId) {
        return null;
    }
}
