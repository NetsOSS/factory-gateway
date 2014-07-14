package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.*;
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
public class ApplicationGroupController {

    private final Logger log = getLogger(getClass());
    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;


    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppGroupModel> listAllAppGroups() {
        log.info("ApplicationGroupController.listAllAppGroups");
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
    public ApplicationGroup findEntityById(@PathVariable Long id) {
        log.info("ApplicationGroupController.findEntityById, id={}", id);

        if(id == null) { throw new EntityNotFoundException("Application", id); }

        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(id);
        if(applicationGroup == null) { throw new EntityNotFoundException("ApplicationGroup", id); }

        return applicationGroup;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-group/{id}/models", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel findById(@PathVariable Long id) {
        log.info("ApplicationGroupController.findById, id={}", id);

        return new AppGroupModel(findEntityById(id));
    }

    private void assertNameUnique(String name) {
        if(applicationGroupRepository.countByName(name) > 0L) {
            throw new GatewayException("Could not create Application Group. Name '" + name + "' already exists.");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/application-group", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel create(@RequestBody AppGroupModel appGroupModel) {
        log.info("ApplicationGroupController.create");

        if(appGroupModel == null) throw new GatewayException("Could not create ApplicationGroup. Invalid ApplicationGroupModel.");
        if(appGroupModel.getName() == null) throw new GatewayException("Could not create ApplicationGroup. Received one or more null values.");
        if(! Pattern.matches("^\\S+$", appGroupModel.getName())) throw new GatewayException("Could not create ApplicationGroup. Name must match pattern '^\\S+$'.");
        assertNameUnique(appGroupModel.name);

        ApplicationGroup applicationGroup = new ApplicationGroup(appGroupModel.getName());
        applicationGroup = applicationGroupRepository.save(applicationGroup);

        return new AppGroupModel(applicationGroup);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/application-groups/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationGroupController.remove, id={}", id);

        ApplicationGroup applicationGroup =  findEntityById(id);

        for(Application application : applicationGroup.getApplications()) {
            for(LoadBalancer loadBalancer : application.getLoadBalancers()) {
                loadBalancer.removeApplication(application);
            }
        }

        applicationGroupRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/application-groups/{id}", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel update(@PathVariable Long id, @RequestBody AppGroupModel appGroupModel) {
        log.info("ApplicationGroupController.update, id={}", id);

        if(appGroupModel == null) throw new GatewayException("Could not create ApplicationGroup. Invalid ApplicationGroupModel.");
        if(appGroupModel.getName() == null) throw new GatewayException("Could not create ApplicationGroup. Received one or more null values.");
        if(! Pattern.matches("^\\S+$", appGroupModel.getName())) throw new GatewayException("Could not create ApplicationGroup. Name must match pattern '^\\S+$'.");

        ApplicationGroup applicationGroup = findEntityById(id);
        if(!(applicationGroup.getName().equals(appGroupModel.name))) { assertNameUnique(appGroupModel.name); }

        applicationGroup.setName(appGroupModel.name);

        return new AppGroupModel(applicationGroup);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups/{id}/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> getApplications(@PathVariable Long id) {
        log.info("ApplicationGroupController.getApplications() LORD   id= {}",id);

        ApplicationGroup applicationGroup = findEntityById(id);
        log.info("ApplicationGroupController.getApplications() : name {}", applicationGroup.getName());

        return applicationGroup.getApplications().stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/application-groups/{id}/remove-application", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel removeApplication(@PathVariable Long id, @RequestBody Long appId) {
        return null;
    }
}
