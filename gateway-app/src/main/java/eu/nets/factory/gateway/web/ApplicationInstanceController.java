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

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Created by sleru on 18.06.2014.
 */
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
    public AppInstModel findById(@PathVariable Long id) {
        log.info("ApplicationInstanceController.findById, id={}", id);

        ApplicationInstance applicationInstance = applicationInstanceRepository.findOne(id);
        if(applicationInstance == null) { throw new EntityNotFoundException("ApplicationInstance", id); }
        return new AppInstModel(applicationInstance);
    }

    private void assertValidId(Long id) {
        findById(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/applications/{applicationId}/instances", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel create(@PathVariable long applicationId, @RequestBody AppInstModel applicationInstanceModel) {
        log.info("ApplicationInstanceController.create AppId={} host= {}",applicationId, applicationInstanceModel.host);

        assertNameUnique(applicationInstanceModel.name);

        Application application = applicationRepository.findOne(applicationId);
        ApplicationInstance applicationInstance = new ApplicationInstance(applicationInstanceModel.name, applicationInstanceModel.host, applicationInstanceModel.port, applicationInstanceModel.path, application);
        applicationInstance = applicationInstanceRepository.save(applicationInstance);

        application.addApplicationInstance(applicationInstance);
        applicationRepository.save(application);

        return new AppInstModel(applicationInstance.getId(), applicationInstance.getName(),applicationInstance.getPath(),applicationInstance.getHost(),applicationInstance.getPort(), applicationInstance.getApplication().getId());
     }

    private void assertNameUnique(String name) {
        log.info("ApplicationInstanceController.assertNameUnique, name={}", name);

        if(applicationInstanceRepository.countByName(name) > 0L) {
            throw new GatewayException("Could not create Application Instance. Name '" + name + "' already exists.");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/instances/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationInstanceController.remove, id={}", id);

        assertValidId(id);

        ApplicationInstance applicationInstance = applicationInstanceRepository.findOne(id);
        Application application = applicationInstance.getApplication();
        application.removeApplicationInstance(applicationInstance);
        applicationRepository.save(application);

        applicationInstanceRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/instances/{id}", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel update(@PathVariable Long id, @RequestBody AppInstModel appInstModel) {
        log.info("ApplicationInstanceController.update, id={}", id);

        assertValidId(id);
        if(!(applicationInstanceRepository.findOne(id).getName().equals(appInstModel.name))) { assertNameUnique(appInstModel.name); }

        ApplicationInstance applicationInstance = applicationInstanceRepository.findOne(id);
        applicationInstance.setName(appInstModel.name);
        applicationInstance.setPath(appInstModel.path);
        applicationInstance.setHost(appInstModel.host);
        applicationInstance.setPort(appInstModel.port);

        applicationInstance = applicationInstanceRepository.save(applicationInstance);
        return new AppInstModel(applicationInstance.getId(), applicationInstance.getName(),applicationInstance.getPath(),applicationInstance.getHost(),applicationInstance.getPort(), applicationInstance.getApplication().getId());

    }
}
