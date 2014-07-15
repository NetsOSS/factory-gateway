package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.*;
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
        return applicationInstanceRepository.findAll().stream().map(AppInstModel::new).collect(toList());
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

        if (id == null) {
            throw new EntityNotFoundException("ApplicationInstance", id);
        }

        ApplicationInstance applicationInstance = applicationInstanceRepository.findOne(id);
        if (applicationInstance == null) throw new EntityNotFoundException("ApplicationInstance", id);

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

        if (applicationInstanceRepository.countByName(name) > 0L)
            throw new GatewayException("Could not create Application Instance. Name '" + name + "' already exists.");
    }

    private void assertValidModel(AppInstModel appInstModel) {
        if (appInstModel == null)
            throw new GatewayException("Could not create ApplicationInstance. Invalid ApplicationInstanceModel: " + appInstModel);
        if (appInstModel.getName() == null || !Pattern.matches("^\\S+$", appInstModel.getName()))
            throw new GatewayException("Could not create ApplicationInstance. Name must match pattern '^\\S+$'.  Received: " + appInstModel.name);
        if (appInstModel.getHost() == null || !Pattern.matches(".++", appInstModel.getHost()))
            throw new GatewayException("Could not create ApplicationInstance. Host must match pattern '.*+'. Received: " + appInstModel.host);
        if (appInstModel.getPath() == null || (!Pattern.matches("^$|^/[a-zA-Z]\\S*$", appInstModel.getPath())))
            throw new GatewayException("Could not create ApplicationInstance. Path must match pattern '^/[a-zA-Z]\\S*$'. Received: " + appInstModel.path);
        if (appInstModel.port == null || appInstModel.port < 1 || appInstModel.port > 65535)
            throw new GatewayException("Could not create ApplicationInstance. Port must be a number between 1 and 65535. Received: " + appInstModel.port);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/applications/{applicationId}/instances", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel create(@PathVariable Long applicationId, @RequestBody AppInstModel appInstModel) {
        log.info("ApplicationInstanceController.create AppId={}", applicationId);

        assertValidModel(appInstModel);
        if (appInstModel.getApplicationId() == null)
            throw new GatewayException("Could not create ApplicationInstance. Invalid ApplicationID: " + applicationId);
        if (!appInstModel.getApplicationId().equals(applicationId))
            throw new GatewayException("Could not create ApplicationInstance. ApplicationIDs did not match: " + applicationId + " - " + appInstModel.applicationId);
        if (applicationRepository.findOne(applicationId) == null)
            throw new GatewayException("Could not create ApplicationInstance. ApplicationID did not match the ID of any known application.");
        assertNameUnique(appInstModel.name);

        Application application = applicationRepository.findOne(applicationId);
        ApplicationInstance applicationInstance = new ApplicationInstance(appInstModel.name, appInstModel.host, appInstModel.port, appInstModel.path, application);
        applicationInstance = applicationInstanceRepository.save(applicationInstance);

        application.addApplicationInstance(applicationInstance);

        return new AppInstModel(applicationInstance);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/instances/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationInstanceController.remove, id={}", id);

        if (id == null) throw new GatewayException("Could not remove ApplicationInstance. Invalid id.");
        ApplicationInstance applicationInstance = findEntityById(id);

        Application application = applicationInstance.getApplication();
        application.removeApplicationInstance(applicationInstance);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/instances/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel update(@PathVariable Long id, @RequestBody AppInstModel appInstModel) {
        log.info("ApplicationInstanceController.update, id={}", id);

        assertValidModel(appInstModel);
        if (id == null) throw new GatewayException("Could not create ApplicationInstance. Invalid ID: " + id);
        if (!id.equals(appInstModel.getId()))
            throw new GatewayException("Could not create ApplicationInstance. IDs did not match:\t" + id + " - " + appInstModel.id);
        if (applicationInstanceRepository.findOne(id) == null)
            throw new GatewayException("Could not create ApplicationInstance. ID sis not match the ID of any known ApplicationInstance.");
        if (appInstModel.getApplicationId() == null)
            throw new GatewayException("Could not create ApplicationInstance. Invalid ApplicationID: " + appInstModel.getApplicationId());
        if (applicationRepository.findOne(appInstModel.getApplicationId()) == null)
            throw new GatewayException("Could not create ApplicationInstance. ApplicationID did not match the ID of any known application.");
        //if(appInstModel.haProxyState == null) throw new GatewayException("Could not create ApplicationInstance. Sticky Session must be a StickySession element. Received: " + appInstModel.haProxyState);

        ApplicationInstance applicationInstance = findEntityById(id);
        if (!applicationInstance.getName().equals(appInstModel.name)) {
            assertNameUnique(appInstModel.name);
        }

        applicationInstance.setName(appInstModel.name);
        applicationInstance.setPath(appInstModel.path);
        applicationInstance.setHost(appInstModel.host);
        applicationInstance.setPort(appInstModel.port);
        applicationInstance.setHaProxyStateValue(appInstModel.haProxyState);

        return new AppInstModel(applicationInstance);

    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/instancesByName/{name}/state/{proxyState}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel setProxyStateByInstanceName(@PathVariable String name, @PathVariable String proxyState) {

        if (name == null) {
            throw new GatewayException("ApplicationInstance name can not be null: " + name);
        }
        if (proxyState == null) {
            throw new GatewayException("ProxyState can not be null: " + proxyState);
        }

        boolean found = false;
        for (int i = 0; i < HaProxyState.values().length; i++) {
            if (proxyState.equals(HaProxyState.values()[i].name())) {
                found = true;
            }
        }
        if (!found) {
            throw new GatewayException("Detected non-valid enum-value for Haproxystate: " + proxyState);
        }

        List<ApplicationInstance> applicationInstances = applicationInstanceRepository.findAll();
        ApplicationInstance applicationInstance = null;

        for (ApplicationInstance appInst : applicationInstances) {
            if (appInst.getName().equals(name)) {
                applicationInstance = appInst;
                break;
            }
        }

        if(applicationInstance == null) {
            throw new GatewayException("ApplicationInstance with this name not found: " + name);
        }
        AppInstModel appInstModel = new AppInstModel(applicationInstance);
        appInstModel.setHaProxyState(proxyState);
        appInstModel = update(appInstModel.getId(), appInstModel);
        return appInstModel;
    }
}
