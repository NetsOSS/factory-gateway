package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.*;
import eu.nets.factory.gateway.service.HaProxyService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Transactional
@Controller
public class ApplicationController {

    private final Logger log = getLogger(getClass());

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;

    @Autowired
    private HeaderRuleRepository headerRuleRepository;

    @Autowired
    HaProxyService haProxyService;


    @RequestMapping(method = RequestMethod.GET, value = "/data/applications", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppModel> listAllApps() {
        log.info("ApplicationController.listAllApps");
        return applicationRepository.findAll().stream().map(AppModel::new).collect(toList());
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

        return applications.stream().map(AppModel::new).collect(toList());
    }


    public Application getApplicationByExactName(String name) {
        List<Application> applications;

        if (name == null) {
            applications = applicationRepository.findAll();
        } else {
            applications = applicationRepository.findByNameLike(name);
        }

        if (applications.size() == 0)
            return null;

        if (applications.size() == 1)
            return applications.get(0);

        for (Application app : applications) {
            if (app.getName().equals(name)) //name.equals(app.getName())) // nullPointerException if name == null
                return app;
        }

        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Application findEntityById(@PathVariable Long id) {
        log.info("ApplicationController.findEntityById, id={}", id);

        if (id == null) {
            throw new EntityNotFoundException("Application", id);
        }

        Application application = applicationRepository.findOne(id);
        if (application == null) {
            throw new EntityNotFoundException("Application", id);
        }

        return application;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/models", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel findById(@PathVariable Long id) {
        log.info("ApplicationController.findById, id={}", id);

        return new AppModel(findEntityById(id));
    }

    private void assertNameUnique(String name) {
        if (applicationRepository.countByName(name) > 0L) {
            throw new GatewayException("Could not create Application. Name '" + name + "' is already in use.");
        }
    }

    private void assertValidModel(AppModel appModel) {
        //is this needed? The object already have the same constraints.
        if (appModel == null) throw new GatewayException("Could not create Application. Invalid ApplicationModel.");
        if (appModel.getApplicationGroupId() == null)
            throw new GatewayException("Could not create Application. Invalid ApplicationGroupID: " + appModel.getApplicationGroupId());
        if (applicationGroupRepository.findOne(appModel.getApplicationGroupId()) == null)
            throw new GatewayException("Could not create Application. ApplicationGroupID did not match the ID of any known application group.");
        if (appModel.getName() == null || !Pattern.matches("^\\S+$", appModel.getName()))
            throw new GatewayException("Could not create Application. Name must match pattern '^\\S+$'.");

        /*if (appModel.getPublicUrl() == null || !Pattern.matches("^/[a-zA-Z]\\S*$", appModel.getPublicUrl()))
            throw new GatewayException("Could not create Application. PublicUrl must match pattern '^/[a-zA-Z]\\S*$'.");
        if (appModel.getCheckPath() == null || !Pattern.matches("^/[a-zA-Z]\\S*$", appModel.getCheckPath()))
            throw new GatewayException("Could not create Application. CheckPath must match pattern '^/[a-zA-Z]\\S*$'.");*/
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/applications", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel create(@RequestBody AppModel appModel) {
        log.info("ApplicationController.create");

        assertValidModel(appModel);
        assertNameUnique(appModel.name);

        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(appModel.getApplicationGroupId());
        Application application = new Application(appModel.getName(), appModel.getPublicUrl(), applicationGroup, appModel.getEmails(), appModel.getCheckPath(), applicationGroup.applicationCount());
        applicationGroup.addApplication(application);

        application = applicationRepository.save(application);
        applicationGroupRepository.save(applicationGroup);

        return new AppModel(application);
    }




    @RequestMapping(method = RequestMethod.DELETE, value = "/data/applications/{id}")
    @ResponseBody //has to be here
    public void remove(@PathVariable Long id) {
        log.info("ApplicationController.remove, id={}", id);

        Application application = findEntityById(id);

        for (LoadBalancer loadBalancer : application.getLoadBalancers()) {
            loadBalancer.removeApplication(application);
        }

        ApplicationGroup applicationGroup = application.getApplicationGroup();
        applicationGroup.removeApplication(application);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/applications/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel update(@PathVariable Long id, @RequestBody AppModel appModel) {
        log.info("ApplicationController.update, id={}", id);

        assertValidModel(appModel);
        if (id == null) throw new GatewayException("Could not create Application. Invalid ID: " + id);
        if (!id.equals(appModel.getId()))
            throw new GatewayException("Could not create Application. IDs did not match: " + id + " - " + appModel.getId());

        Application application = findEntityById(id);
        if (!application.getName().equals(appModel.name)) {
            assertNameUnique(appModel.name);
        }

        application.setName(appModel.getName());
        application.setPublicUrl(appModel.getPublicUrl());
        application.setEmails(appModel.getEmails());
        application.setCheckPath(appModel.getCheckPath());
        application.setStickySession(appModel.stickySession);
        application.setFailoverLoadBalancerSetup(appModel.failoverLoadBalancerSetup);

        return new AppModel(application);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/application-group", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppGroupModel getApplicationGroup(@PathVariable Long id) {
        log.info("ApplicationController.getApplicationGroup, id={}", id);

        Application application = findEntityById(id);
        return new AppGroupModel(application.getApplicationGroup());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/load-balancers", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LoadBalancerModel> getLoadBalancers(@PathVariable Long id) {
        log.info("ApplicationController.getLoadBalancers, id={}", id);

        Application application = findEntityById(id);
        return application.getLoadBalancers().stream().map(LoadBalancerModel::new).collect(toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/applications/{id}/configureHaproxySetupAndStartLoadbalancer/{setup}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel configureHaproxySetupAndStartLoadbalancer(@PathVariable Long id, @PathVariable String setup) {

        AppModel appModel = configureHaproxySetup(id, setup);

        Application application = findEntityById(id);

        for (LoadBalancer loadBalancer : application.getLoadBalancers()) {
            haProxyService.pushConfigFile(loadBalancer);
            haProxyService.start(loadBalancer);
        }

        return appModel;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data/applications/{id}/setSticky/{sticky}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppModel setStickyAndStartLoadBalancer(@PathVariable Long id, @PathVariable String sticky) {

        AppModel appModel = setSticky(id, sticky);

        Application application = findEntityById(id);

        for (LoadBalancer loadBalancer : application.getLoadBalancers()) {
            haProxyService.pushConfigFile(loadBalancer);
            haProxyService.start(loadBalancer);
        }

        return appModel;
    }

    protected AppModel setSticky(Long id, String sticky) {

        if (sticky == null) throw new GatewayException("Sticky cannot be null: " + sticky);

        boolean found = false;
        for (int i = 0; i < StickySession.values().length; i++) {
            if (sticky.equals(StickySession.values()[i].name())) {
                found = true;
            }
        }
        if (!found) {
            throw new GatewayException("Detected non-valid enum-value for StickySession: " + sticky);
        }
        AppModel appModel = findById(id);
        appModel.setStickySession(sticky);
        return update(id, appModel);
    }

    protected AppModel configureHaproxySetup(Long id, String setup) {
        if (setup == null) throw new GatewayException("Setup cannot be null: " + setup);

        boolean found = false;
        for (int i = 0; i < FailoverLoadBalancerSetup.values().length; i++) {
            if (setup.equals(FailoverLoadBalancerSetup.values()[i].name())) {
                found = true;
            }
        }
        if (!found) {
            throw new GatewayException("Detected non-valid enum-value for FailoverLoadBalancerSetup: " + setup);
        }

        AppModel appModel = findById(id);
        appModel.setFailoverLoadBalancerSetup(setup);
        return update(id, appModel);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/data/application/{applicationId}/newRule", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public HeaderRuleModel addHeaderRule(@PathVariable Long applicationId, @RequestBody HeaderRuleModel headerRuleModel) {
        log.info("ApplicationController.addHeaderRule");
        if(applicationRepository.findOne(applicationId) == null) throw new GatewayException("Could not create ApplicationInstance. ApplicationID did not match the ID of any known application.");

        Application application = applicationRepository.findOne(applicationId);
        HeaderRule headerRule = new HeaderRule(headerRuleModel.getName(),headerRuleModel.getPrefixMatch(),application);
        headerRule = headerRuleRepository.save(headerRule);
        application.addHeaderRule(headerRule);
        return new HeaderRuleModel(headerRule);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/data/application/{applicationId}/removeRule/{headerId}",produces = APPLICATION_JSON_VALUE)
    @ResponseBody //has to be here
    public AppModel removeHeaderRule(@PathVariable Long applicationId, @PathVariable Long headerId) {
        log.info("ApplicationController.removeHeaderRule");
        if(applicationRepository.findOne(applicationId) == null) throw new GatewayException("ApplicationID did not match the ID of any known application.");
        if(headerRuleRepository.findOne(headerId) == null) throw new GatewayException("HeaderRuleID did not match the ID of any known headerRule.");
        Application application = applicationRepository.findOne(applicationId);

        HeaderRule headerRule = headerRuleRepository.findOne(headerId);
        application.removeHeaderRule(headerRule);

        return new AppModel(application);
    }



}