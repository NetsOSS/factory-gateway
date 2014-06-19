package eu.nets.factory.gateway.web;

        import eu.nets.factory.gateway.model.Application;
        import eu.nets.factory.gateway.model.ApplicationRepository;
        import org.slf4j.Logger;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;

        import static org.slf4j.LoggerFactory.getLogger;
        import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Created by kwlar on 19.06.2014.
 */

@Controller
public class ApplicationController {
    private final Logger log = getLogger(getClass());

    @Autowired
    private ApplicationRepository appRep;

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications", produces = APPLICATION_JSON_VALUE)

    @ResponseBody
    public List<Application> listAllApps() {
        log.info("ApplicationController.list");
        //List<Application> l = new ArrayList<Application>();
        //l.add(new Application("test"));

        // personRepository.findAll().stream().map(PersonModel::new).collect(toList());

        return  appRep.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Application> search(@RequestParam(required = false) String name) {
        log.info("ApplicationController.search, name={}", name);

        List<Application> application;

        if (name == null) {
            application = appRep.findAll();
        } else {
            application = appRep.findByNameLike("%" + name + "%");
        }

        return application;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Application findById(@PathVariable Long id) {
        log.info("ApplicationController.findById, name={}", id);
        return appRep.findOne(id);
    }

    /*
    @RequestMapping(method = POST, value = "/data/persons", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonModel create(@RequestBody PersonModel personModel) {
     */

    @RequestMapping(method = RequestMethod.POST, value = "/data/applications", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel create(@RequestBody AppModel appModel) {
        log.info("ApplicationController.create");
        Application app = new Application(appModel.name, appModel.publicURL);
        app = appRep.save(app);
        return new AppModel(app.getId(), app.getName(), app.getPublicURL());
    }

    public static class AppModel {

        public Long id;
        public String name;
        public String publicURL;


        public AppModel() { }

        public AppModel(Application app) { this(app.getId(), app.getName(), app.getPublicURL()); }

        public AppModel(Long id, String name, String url) {
            this.id = id;
            this.name = name;
            this.publicURL = url;
        }
    }
}