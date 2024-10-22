package eu.nets.factory.gateway.web;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationGroupRepository;
import eu.nets.factory.gateway.model.ApplicationRepository;
import eu.nets.factory.gateway.model.LoadBalancer;
import java.util.List;
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
public class ApplicationGroupController {

    private final Logger log = getLogger(getClass());
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;


    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppGroupModel> listAllAppGroups() {
        log.info("ApplicationGroupController.listAllAppGroups");
        return applicationGroupRepository.findAll().stream().map(AppGroupModel::new).collect(toList());
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

        if (id == null) {
            throw new EntityNotFoundException("Application", id);
        }

        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(id);
        if (applicationGroup == null) {
            throw new EntityNotFoundException("ApplicationGroup", id);
        }

        return applicationGroup;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-group/{id}/models", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel findById(@PathVariable Long id) {
        log.info("ApplicationGroupController.findById, id={}", id);

        return new AppGroupModel(findEntityById(id));
    }

    private void assertNameUnique(String name) {
        if (applicationGroupRepository.countByName(name) > 0L) {
            throw new GatewayException("Could not create Application Group. Name '" + name + "' is already in use.");
        }
    }

    private void assertPortUnique(int port) {
        if (applicationGroupRepository.countByPort(port) > 0L) {
            throw new GatewayException("Could not create Application Group. Port '" + port + "' is already in use.");
        }
    }

    private void assertValidModel(AppGroupModel appGroupModel) {
        if (appGroupModel == null)
            throw new GatewayException("Could not create ApplicationGroup. Invalid ApplicationGroupModel.");
        if (appGroupModel.getName() == null || !Pattern.matches("^\\S+$", appGroupModel.getName()))
            throw new GatewayException("Could not create ApplicationGroup. Name must match pattern '^\\S+$'.");
        //TODO: test Name for symbols the config file can't handle, such as æ, ø and å
        if (appGroupModel.getPort() < ApplicationGroup.INSTANCE_PORT_MIN || appGroupModel.getPort() > ApplicationGroup.INSTANCE_PORT_MAX)
            throw new GatewayException("Could not create ApplicationGroup. Port must be a number between " + ApplicationGroup.INSTANCE_PORT_MIN + " and " + ApplicationGroup.INSTANCE_PORT_MAX + ". Received: " + appGroupModel.getPort());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/application-group", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel create(@RequestBody AppGroupModel appGroupModel) {
        log.info("ApplicationGroupController.create");

        assertValidModel(appGroupModel);
        assertNameUnique(appGroupModel.name);
        assertPortUnique(appGroupModel.port);

        ApplicationGroup applicationGroup = new ApplicationGroup(appGroupModel.getName(), appGroupModel.getPort());
        applicationGroup = applicationGroupRepository.save(applicationGroup);

        return new AppGroupModel(applicationGroup);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/application-groups/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationGroupController.remove, id={}", id);

        ApplicationGroup applicationGroup = findEntityById(id);

        for (Application application : applicationGroup.getApplications()) {
            for (LoadBalancer loadBalancer : application.getLoadBalancers()) {
                loadBalancer.removeApplication(application);
            }
        }

        applicationGroupRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/application-groups/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel update(@PathVariable Long id, @RequestBody AppGroupModel appGroupModel) {
        log.info("ApplicationGroupController.update, id={}", id);

        assertValidModel(appGroupModel);
        ApplicationGroup applicationGroup = findEntityById(id);
        if (!applicationGroup.getName().equals(appGroupModel.getName())) {
            assertNameUnique(appGroupModel.name);
        }
        if (applicationGroup.getPort() != appGroupModel.getPort()) assertPortUnique(appGroupModel.port);

        applicationGroup.setName(appGroupModel.getName());
        applicationGroup.setPort(appGroupModel.getPort());

        return new AppGroupModel(applicationGroup);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups/{id}/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> getApplications(@PathVariable Long id) {
        log.info("ApplicationGroupController.getApplications() LORD   id= {}", id);

        ApplicationGroup applicationGroup = findEntityById(id);
        log.info("ApplicationGroupController.getApplications() : name {}", applicationGroup.getName());

        return applicationGroup.getApplications().stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/application-groups/{appGroupId}/changeIndexOrder", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public void changeIndexOrderOfApplications(@PathVariable Long appGroupId, @RequestBody ObjectNode body) {
        ApplicationGroup applicationGroup = findEntityById(appGroupId);
        List<Application> applications = applicationGroup.getApplications();

        int fromIndex = body.get("from").asInt();
        int toIndex = body.get("to").asInt();

        if (fromIndex < 0 || toIndex < 0 || fromIndex > applications.size() || toIndex > applications.size() || fromIndex == toIndex) {
            log.debug("Invalid indexes. Can't change order.");
            return;
        }

        Application moved = applications.get(fromIndex);

        moved.setIndexOrder(Integer.MAX_VALUE);
        applicationRepository.saveAndFlush(moved);

        if (fromIndex > toIndex) {

            for (int i = fromIndex - 1; i >= toIndex; i--) {
                Application app = applications.get(i);
                app.moveUp();
                //System.out.println("Moving app " + app.getName() + " up ->  to index=" + app.getIndexOrder());
                applicationRepository.save(app);
                applicationRepository.flush();
            }
        }

        if (fromIndex < toIndex) {

            for (int i = fromIndex + 1; i <= toIndex; i++) {
                Application app = applications.get(i);
                app.moveDown();
            }
        }
        applicationRepository.save(applications);
        applicationRepository.flush();
        moved.setIndexOrder(toIndex);
        applicationRepository.save(applications);
    }
}
