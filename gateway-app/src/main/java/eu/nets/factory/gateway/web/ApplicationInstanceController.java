package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.ApplicationInstanceRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Created by sleru on 18.06.2014.
 */
@Controller
public class ApplicationInstanceController {

    private final Logger log = getLogger(getClass());
    @Autowired
    private ApplicationInstanceRepository applicationInstanceRepository;


    @RequestMapping(method = RequestMethod.GET, value = "/data/instances", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ApplicationInstance> listAllAppInsts() {
        log.info("ApplicationInstanceController.list");
        //List<ApplicationInstance> l = new ArrayList<ApplicationInstance>();
        //l.add(new ApplicationInstance("test"));

        // personRepository.findAll().stream().map(PersonModel::new).collect(toList());

        return  applicationInstanceRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ApplicationInstance> search(@RequestParam(required = false) String name) {
        log.info("ApplicationInstanceController.search, name={}", name);

        List<ApplicationInstance> applicationInstances;

        if (name == null) {
            applicationInstances = applicationInstanceRepository.findAll();
        } else {
            applicationInstances = applicationInstanceRepository.findByNameLike("%" + name + "%");
        }

        return applicationInstances;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/instances/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApplicationInstance findById(@PathVariable Long id) {
        log.info("ApplicationInstanceController.findById, name={}", id);
        return applicationInstanceRepository.findOne(id);
    }

    /*
    @RequestMapping(method = POST, value = "/data/persons", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonModel create(@RequestBody PersonModel personModel) {
     */

    @RequestMapping(method = RequestMethod.POST, value = "/data/instances", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel create(@RequestBody AppInstModel applicationInstanceModel) {
        log.info("ApplicationInstanceController.create");

        ApplicationInstance applicationInstance = new ApplicationInstance(applicationInstanceModel.name, applicationInstanceModel.host, applicationInstanceModel.port, applicationInstanceModel.path, applicationInstanceModel.application);

        applicationInstance = applicationInstanceRepository.save(applicationInstance);
        /* Application - ApplicationInstance relation
        applicationInstanceModel.getApplication().addApplicationInstance(applicationInstance);
        */
        return new AppInstModel(applicationInstance.getId(), applicationInstance.getName());
     }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/instances/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationInstanceController.remove");
        /* Application - ApplicationInstance relation
        ApplicationInstance applicationInstance = applicationInstanceRepository.findOne(id);
        applicationInstance.getApplication().removeApplicationInstance(applicationInstance);
        */
        applicationInstanceRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/instances/{id}", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel update(@PathVariable Long id, @RequestBody AppInstModel applicationInstanceModel) {
        log.info("ApplicationInstanceController.update");

        ApplicationInstance applicationInstance = applicationInstanceRepository.findOne(id);
        applicationInstance.setName(applicationInstanceModel.getName());
        applicationInstance.setPath(applicationInstanceModel.getPath());
        applicationInstance.setHost(applicationInstanceModel.getHost());
        applicationInstance.setPort(applicationInstanceModel.getPort());

        applicationInstance = applicationInstanceRepository.save(applicationInstance);
        return new AppInstModel(applicationInstance.getId(), applicationInstance.getName());
    }

    public static class AppInstModel {

        public Long id;

        public String name;
        public String path;
        public String host;
        public Integer port;
        public Application application;


        public AppInstModel() { }

        public AppInstModel(ApplicationInstance applicationInstance) {
          this(applicationInstance.getId(), applicationInstance.getName());
        }

        public AppInstModel(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }

        public String getHost() {
            return host;
        }
        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }
        public void setPort(Integer port) {
            this.port = port;
        }

        public Application getApplication() {
            return application;
        }
        public void setApplication(Application application) {
            this.application = application;
        }
    }
}
