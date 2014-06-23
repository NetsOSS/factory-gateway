package eu.nets.factory.gateway.web;

        import eu.nets.factory.gateway.model.Application;
        import eu.nets.factory.gateway.model.ApplicationRepository;
        import org.slf4j.Logger;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import javax.transaction.Transactional;
        import java.util.List;

        import static java.util.stream.Collectors.toList;
        import static org.slf4j.LoggerFactory.getLogger;
        import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Created by kwlar on 19.06.2014.
 */
@Transactional
@Controller
public class ApplicationController {

    private final Logger log = getLogger(getClass());
    @Autowired
    private ApplicationRepository applicationRepository;


    @RequestMapping(method = RequestMethod.GET, value = "/data/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> listAllApps() {
        log.info("ApplicationController.list");
        //List<Application> l = new ArrayList<Application>();
        //l.add(new Application("test"));

        // personRepository.findAll().stream().map(PersonModel::new).collect(toList());

        return  applicationRepository.findAll().stream().
                map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Application> search(@RequestParam(required = false) String name) {
        log.info("ApplicationController.search, name={}", name);

        List<Application> applications;

        if (name == null) {
            applications = applicationRepository.findAll();
        } else {
            applications = applicationRepository.findByNameLike("%" + name + "%");
        }

        return applications;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Application findById(@PathVariable Long id) {
        log.info("ApplicationController.findById, name={}", id);
        return applicationRepository.findOne(id);
    }

    /*
    @RequestMapping(method = POST, value = "/data/persons", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonModel create(@RequestBody PersonModel personModel) {
     */

    @RequestMapping(method = RequestMethod.POST, value = "/data/applications", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel create(@RequestBody AppModel applicationModel) {
        log.info("ApplicationController.create");
        Application application = new Application(applicationModel.getName(), applicationModel.getPublicUrl());
        application = applicationRepository.save(application);
        return new AppModel(application.getId(), application.getName(), application.getPublicUrl());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/applications/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationController.remove");

        /* Application - ApplicationInstance relation
        List<ApplicationInstance> applicationInstances = applicationRepository.findOne(id).getApplicationInstances();
        for(ApplicationInstance applicationInstance : applicationInstances) {
            ApplicationInstanceController.remove(applicationInstance.getId());
        }
        */
        applicationRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/applications/{id}", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel update(@PathVariable Long id, @RequestBody AppModel appModel) {
        log.info("ApplicationController.update");

        Application application = applicationRepository.findOne(id);
        application.setName(appModel.getName());
        application.setPublicUrl(appModel.getPublicUrl());

        application = applicationRepository.save(application);
        return new AppModel(application.getId(), application.getName());
    }


}