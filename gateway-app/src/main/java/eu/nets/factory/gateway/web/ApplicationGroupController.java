package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationGroupRepository;
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
public class ApplicationGroupController {
    private final Logger log = getLogger(getClass());

    @Autowired
    private ApplicationGroupRepository appGroupRep;

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-groups", produces = APPLICATION_JSON_VALUE)

    @ResponseBody
    public List<ApplicationGroup> listAllAppGroups() {
        log.info("ApplicationGroupController.list");
        //List<ApplicationGroup> l = new ArrayList<ApplicationGroup>();
        //l.add(new ApplicationGroup("test"));

        // personRepository.findAll().stream().map(PersonModel::new).collect(toList());

        return  appGroupRep.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-group/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ApplicationGroup> search(@RequestParam(required = false) String name) {
        log.info("ApplicationGroupController.search, name={}", name);

        List<ApplicationGroup> applicationGroup;

        if (name == null) {
            applicationGroup = appGroupRep.findAll();
        } else {
            applicationGroup = appGroupRep.findByNameLike("%" + name + "%");
        }

        return applicationGroup;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/application-group/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApplicationGroup findById(@PathVariable Long id) {
        log.info("ApplicationGroupController.findById, name={}", id);
        return appGroupRep.findOne(id);
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
        ApplicationGroup appGroup = new ApplicationGroup(appGroupModel.name);
        appGroup = appGroupRep.save(appGroup);
        return new AppGroupModel(appGroup.getId(), appGroup.getName());
    }

    public static class AppGroupModel {

        public Long id;
        public String name;


        public AppGroupModel() { }

        public AppGroupModel(ApplicationGroup appGroup) {
            this(appGroup.getId(), appGroup.getName());
        }

        public AppGroupModel(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
