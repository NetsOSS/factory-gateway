package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.ApplicationInstanceRepository;
import eu.nets.factory.gateway.model.ApplicationRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
@Transactional
public class ApplicationInstanceController {

    private final Logger log = getLogger(getClass());
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationInstanceRepository applicationInstanceRepository;


    @RequestMapping(method = RequestMethod.GET, value = "/data/instances", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppInstModel> listAllAppInsts() {
        log.info("ApplicationInstanceController.listAllAppInsts");
        return  applicationInstanceRepository.findAll().stream().map(AppInstModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/instances/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppInstModel> search(@RequestParam(required = false) String name) {
        log.info("ApplicationInstanceController.search, name={}", name);

        List<ApplicationInstance> applicationInstances;

        if (name == null) {
            applicationInstances = applicationInstanceRepository.findAll();
        } else {
            applicationInstances = applicationInstanceRepository.findByNameLike("%" + name + "%");
        }

        return applicationInstances.stream().map(AppInstModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/instances/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApplicationInstance findEntityById(@PathVariable Long id) {
        log.info("ApplicationInstanceController.findEntityById, id={}", id);

        if(id == null) { throw new EntityNotFoundException("ApplicationInstance", id); }

        ApplicationInstance applicationInstance = applicationInstanceRepository.findOne(id);
        if(applicationInstance == null) throw new EntityNotFoundException("ApplicationInstance", id);

        return applicationInstance;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/instances/{id}/models", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel findById(@PathVariable Long id) {
        log.info("ApplicationInstanceController.findById, id={}", id);

        return new AppInstModel(findEntityById(id));
    }

    private void assertNameUnique(String name) {
        log.info("ApplicationInstanceController.assertNameUnique, name={}", name);

        if(applicationInstanceRepository.countByName(name) > 0L) throw new GatewayException("Could not create Application Instance. Name '" + name + "' already exists.");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/applications/{applicationId}/instances", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel create(@PathVariable Long applicationId, @RequestBody AppInstModel appInstModel) {
        log.info("ApplicationInstanceController.create AppId={}",applicationId);

        if(appInstModel == null) throw new GatewayException("Could not create ApplicationInstance. Invalid ApplicationInstanceModel.");
        if(appInstModel.getApplicationId() == null || appInstModel.getApplicationId() != applicationId || applicationRepository.findOne(applicationId) == null) throw new GatewayException("Could not create ApplicationInstance. Invalid ApplicationID.");
        if(appInstModel.getName() == null || appInstModel.getHost() == null || appInstModel.getPath() == null) throw new GatewayException("Could not create ApplicationInstance. Received one or more null values.");
        if(! Pattern.matches("^\\S+$", appInstModel.getName())) throw new GatewayException("Could not create ApplicationInstance. Name must match pattern '^\\S+$'.");
        if(! Pattern.matches(".++", appInstModel.getHost())) throw new GatewayException("Could not create ApplicationInstance. Host must match pattern '.*+'.");
        if(! Pattern.matches("^/[a-zA-Z]\\S*$", appInstModel.getPath())) throw new GatewayException("Could not create ApplicationInstance. Path must match pattern '^/[a-zA-Z]\\S*$'.");
        if(appInstModel.port == null || appInstModel.port < 1 || appInstModel.port > 65535) throw new GatewayException("Could not create ApplicationInstance. Port must be a number between 1 and 65535.");
        assertNameUnique(appInstModel.name);

        Application application = applicationRepository.findOne(applicationId);
        ApplicationInstance applicationInstance = new ApplicationInstance(appInstModel.name, appInstModel.host, appInstModel.port, appInstModel.path, application);
        applicationInstance = applicationInstanceRepository.save(applicationInstance);

        application.addApplicationInstance(applicationInstance);

        return new AppInstModel(applicationInstance.getId(), applicationInstance.getName(),applicationInstance.getPath(),applicationInstance.getHost(),applicationInstance.getPort(), applicationInstance.getApplication().getId());
     }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/instances/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationInstanceController.remove, id={}", id);

        if(id == null) throw new GatewayException("Could not remove ApplicationInstance. Invalid id.");
        ApplicationInstance applicationInstance = findEntityById(id);

        Application application = applicationInstance.getApplication();
        application.removeApplicationInstance(applicationInstance);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/instances/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel update(@PathVariable Long id, @RequestBody AppInstModel appInstModel) {
        log.info("ApplicationInstanceController.update, id={}", id);

        if(appInstModel == null) throw new GatewayException("Could not create ApplicationInstance. Invalid ApplicationInstanceModel.");
        if(appInstModel.getId() == null || appInstModel.getId() != id || applicationInstanceRepository.findOne(id) == null) throw new GatewayException("Could not create ApplicationInstance. Invalid ID.");
        if(appInstModel.getApplicationId() == null || applicationRepository.findOne(appInstModel.getApplicationId()) == null) throw new GatewayException("Could not create ApplicationInstance. Invalid Application ID.");
        if(appInstModel.getName() == null || appInstModel.getHost() == null || appInstModel.getPath() == null) throw new GatewayException("Could not create ApplicationInstance. Received one or more null values.");
        if(! Pattern.matches("^\\S+$", appInstModel.getName())) throw new GatewayException("Could not create ApplicationInstance. Name must match pattern '^\\S+$'.");
        if(! Pattern.matches(".++", appInstModel.getHost())) throw new GatewayException("Could not create ApplicationInstance. Host must match pattern '.*+'.");
        if(! Pattern.matches("^/[a-zA-Z]\\S*$", appInstModel.getPath())) throw new GatewayException("Could not create ApplicationInstance. Path must match pattern '^/[a-zA-Z]\\S*$'.");
        if(appInstModel.port == null || appInstModel.port < 1 || appInstModel.port > 65535) throw new GatewayException("Could not create ApplicationInstance. Port must be a number between 1 and 65535.");

        ApplicationInstance applicationInstance = findEntityById(id);
        if(!(applicationInstance.getName().equals(appInstModel.name))) { assertNameUnique(appInstModel.name); }

        applicationInstance.setName(appInstModel.name);
        applicationInstance.setPath(appInstModel.path);
        applicationInstance.setHost(appInstModel.host);
        applicationInstance.setPort(appInstModel.port);

        return new AppInstModel(applicationInstance.getId(), applicationInstance.getName(),applicationInstance.getPath(),applicationInstance.getHost(),applicationInstance.getPort(), applicationInstance.getApplication().getId());

    }
}
