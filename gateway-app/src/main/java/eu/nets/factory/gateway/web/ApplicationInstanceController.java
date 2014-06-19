package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.ApplicationInstanceRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
    public List<ApplicationInstance> listAllApps() {
        log.info("ApplicationInstanceController.list");
        //List<ApplicationInstance> l = new ArrayList<ApplicationInstance>();
        //l.add(new ApplicationInstance("test"));

        // personRepository.findAll().stream().map(PersonModel::new).collect(toList());

        return  appInstRep.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApplicationInstance findApp(String name) {
        return null;
    }

    /*
    @RequestMapping(method = POST, value = "/data/persons", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonModel create(@RequestBody PersonModel personModel) {
     */

    @RequestMapping(method = RequestMethod.POST, value = "/data/newApp", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInstModel create(@RequestBody AppInstModel appModel) {
        log.info("ApplicationInstanceController.create KJJJAAAAAAAA");
        ApplicationInstance appInst = new ApplicationInstance(appModel.name, appModel.host,appModel.port,appModel.path);
        appInst = appInstRep.save(appInst);
        return new AppInstModel(appInst.getId(), appInst.getName());
        //return null;
    }

    public static class AppInstModel {

        public Long id;

        public String name;
        public String path;

        public String host;
        public Integer port;

        //public ApplicationInstance app;

        public AppInstModel(){

        }

        public AppInstModel(ApplicationInstance app) {
          //  this.app = app;
        }

        public AppInstModel(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
