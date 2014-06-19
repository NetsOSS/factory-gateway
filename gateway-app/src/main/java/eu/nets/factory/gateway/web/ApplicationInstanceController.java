package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.ApplicationInstanceRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
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
    private ApplicationInstanceRepository appInstRep;


    @RequestMapping(method = RequestMethod.GET, value = "/data/instances", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ApplicationInstance> listAllAppInsts() {
        log.info("ApplicationInstanceController.list");
        //List<ApplicationInstance> l = new ArrayList<ApplicationInstance>();
        //l.add(new ApplicationInstance("test"));

        // personRepository.findAll().stream().map(PersonModel::new).collect(toList());

        return  appInstRep.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ApplicationInstance> search(@RequestParam(required = false) String name) {
        log.info("ApplicationInstanceController.search, name={}", name);

        List<ApplicationInstance> applicationInstances;

        if (name == null) {
            applicationInstances = appInstRep.findAll();
        } else {
            applicationInstances = appInstRep.findByNameLike("%" + name + "%");
        }

        return applicationInstances;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/instances/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApplicationInstance findById(@PathVariable Long id) {
        log.info("ApplicationInstanceController.findById, name={}", id);
        return appInstRep.findOne(id);
    }

    /*
    @RequestMapping(method = POST, value = "/data/persons", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonModel create(@RequestBody PersonModel personModel) {
     */

    @RequestMapping(method = RequestMethod.POST, value = "/data/newApp", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel create(@RequestBody AppInstModel appInstModel) {
        log.info("ApplicationInstanceController.create");
        ApplicationInstance appInst = new ApplicationInstance(appInstModel.name, appInstModel.host,appInstModel.port,appInstModel.path);
        appInst = appInstRep.save(appInst);
        return new AppInstModel(appInst.getId(), appInst.getName());
     }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/instances/{id}")
    public void remove(@PathVariable Long id) {
        log.info("ApplicationInstanceController.remove");
        appInstRep.delete(id);
    }

    public static class AppInstModel {

        public Long id;

        public String name;
        public String path;

        public String host;
        public Integer port;


        public AppInstModel() { }

        public AppInstModel(ApplicationInstance appInst) {
          this(appInst.getId(), appInst.getName());
        }

        public AppInstModel(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
