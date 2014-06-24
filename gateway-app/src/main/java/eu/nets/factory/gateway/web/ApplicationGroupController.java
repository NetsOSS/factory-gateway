package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationGroupRepository;
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

@Controller
@Transactional
public class ApplicationGroupController {

    private final Logger log = getLogger(getClass());
    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppGroupModel> listAllAppGroups() {
        log.info("ApplicationGroupController.list");
        //List<ApplicationGroup> l = new ArrayList<ApplicationGroup>();
        //l.add(new ApplicationGroup("test"));

        // personRepository.findAll().stream().map(PersonModel::new).collect(toList());

        return  applicationGroupRepository.findAll().stream().map(AppGroupModel::new).collect(toList());
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
    public AppGroupModel findById(@PathVariable Long id) {
        log.info("ApplicationGroupController.findById, name={}", id);
        return new AppGroupModel(applicationGroupRepository.findOne(id));
    }

    /*
    @RequestMapping(method = POST, value = "/data/persons", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonModel create(@RequestBody PersonModel personModel) {
     */

    @RequestMapping(method = RequestMethod.POST, value = "/data/application-group", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel create(@RequestBody AppGroupModel appGroupModel) {
        log.info("ApplicationGroupController.create");

        ApplicationGroup applicationGroup = new ApplicationGroup(appGroupModel.getName());

        applicationGroup = applicationGroupRepository.save(applicationGroup);
        return new AppGroupModel(applicationGroup);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/application-groups/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationGroupController.remove");
        applicationGroupRepository.delete(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/application-groups/{id}", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel update(@PathVariable Long id, @RequestBody AppGroupModel appGroupModel) {
        log.info("ApplicationGroupController.update");

        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(id);
        applicationGroup.setName(appGroupModel.getName());

        applicationGroup = applicationGroupRepository.save(applicationGroup);
        return new AppGroupModel(applicationGroup);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups/{id}/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> getApplications(@PathVariable Long id) {
        log.info("ApplicationGroupController.getApplications() LORD   id= {}",id);
        ApplicationGroup g = applicationGroupRepository.findOne(id);
        log.info("ApplicationGroupController.getApplications() : isNull ? {} ",g==null);

        log.info("ApplicationGroupController.getApplications() : name {}",g.getName());
        return applicationGroupRepository.findOne(id).getApplications().stream().map(AppModel::new).collect(toList());


    }
}
