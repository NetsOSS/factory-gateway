package eu.nets.factory.gateway.web;

        import edu.umd.cs.findbugs.ba.bcp.Load;
        import eu.nets.factory.gateway.model.*;
        import org.slf4j.Logger;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import javax.transaction.Transactional;
        import java.util.Iterator;
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

    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;

    @Autowired
    private ApplicationInstanceRepository applicationInstanceRepository;

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;


    @RequestMapping(method = RequestMethod.GET, value = "/data/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> listAllApps() {
        log.info("ApplicationController.list");
        return  applicationRepository.findAll().stream().map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/find/{name}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> search(@PathVariable String name) {
        log.info("ApplicationController.search, name={}", name);

        List<Application> applications;

        if (name == null) {
            applications = applicationRepository.findAll();
        } else {
            applications = applicationRepository.findByNameLike("%" + name + "%");
        }

        return applications.stream().
                map(AppModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel findById(@PathVariable Long id) {
        log.info("ApplicationController.findById, name={}", id);
        return new AppModel(applicationRepository.findOne(id));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/applications", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel create(@RequestBody AppModel applicationModel) {
        log.info("ApplicationController.create");

        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(applicationModel.getApplicationGroupId());
        Application application = new Application(applicationModel.getName(), applicationModel.getPublicUrl(), applicationGroup);
        application = applicationRepository.save(application);

        applicationGroup.addApplication(application);
        applicationGroupRepository.save(applicationGroup);

        return new AppModel(application);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/applications/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationController.remove");

        Application application = applicationRepository.findOne(id);
        if(application == null) {
            return;
        }
        List<ApplicationInstance> instances = application.getApplicationInstances();
        for(ApplicationInstance instance: instances) {
            applicationInstanceRepository.delete(instance);
        }
        List<LoadBalancer> loadBalancers = application.getLoadBalancers();
        for(Iterator<LoadBalancer> it = loadBalancers.iterator(); it.hasNext();) {
            LoadBalancer l = it.next();
            l.removeApplication(application);
            //it.remove();
            loadBalancerRepository.save(l);
        }
        ApplicationGroup group = application.getApplicationGroup();
        group.removeApplication(application);

        applicationGroupRepository.save(group);
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
        return new AppModel(application);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/application-group", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel getApplicationGroup(@PathVariable Long id) {
        log.info("ApplicationController.getGroup");
        Application application = applicationRepository.findOne(id);

        return new AppGroupModel(application.getApplicationGroup());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/load-balancers", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LoadBalancerModel> getLoadBalancers(@PathVariable Long id) {
        log.info("ApplicationController.getLoadBalancers");
        Application application = applicationRepository.findOne(id);

        return application.getLoadBalancers().stream().map(LoadBalancerModel::new).collect(toList());
    }
}