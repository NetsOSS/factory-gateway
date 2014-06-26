package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.*;
import junit.framework.TestCase;
import org.fest.assertions.Assert;
import org.fest.assertions.Assertions;
import org.fest.assertions.CollectionAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import eu.nets.factory.gateway.web.ApplicationController;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
@Transactional
@ActiveProfiles("unitTest")
public class ApplicationControllerTest {

    @Autowired
    ApplicationController applicationController;

    @Autowired
    ApplicationInstanceController applicationInstanceController;

    @Autowired
    LoadBalancerController loadBalancerController;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationInstanceRepository applicationInstanceRepository;

    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;


    @Autowired
    private InitTestClass initTestClass;
    @Before
    public void Before() {
        initTestClass.init();
    }

    String classId = "ApplicationControllerTest";

    @Test
    public void testListAllApps() throws Exception {
        String methodId = classId + " - testListAllApps";
        String partId;
        String groupName;
        AppModel appModel;
        AppInstModel appInstModel;
        LoadBalancerModel loadBalancerModel;
        List<AppModel> appModels = applicationController.listAllApps();
        Collections.sort(appModels, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });

        assertNotNull(methodId + ": received null-pointer: 'appModels'", appModels);
        assertEquals(methodId + ": expected list 'applications' to be of size 3, got " + appModels.size(), 3, appModels.size());

        partId = methodId + " - 1: "; //check 'completeness' of first object
        appModel = appModels.get(0);
        assertEquals(partId + "expected 'Kamino', got '" + appModel.name + "'", "Kamino", appModel.name);
        assertEquals(partId + "expected 'www.kamino.no', got '" + appModel.publicURL + "'", "www.kamino.no", appModel.publicURL);

        assertNotNull(partId + "applicationGroupID " + appModel.applicationGroupId + " did not match the ID of any ApplicationGroup", applicationGroupRepository.findOne(appModel.applicationGroupId));
        groupName = applicationGroupRepository.findOne(appModel.applicationGroupId).getName();
        assertEquals(partId + "expected 'GroupOne', got '" + groupName + "'", "GroupOne", groupName);

        assertNotNull(partId + "received null-pointer: 'applicationInstances'", appModel.applicationInstances);
        assertEquals(partId + "expected list 'applicationInstances' to be of size 1, found size " + appModel.applicationInstances.size(), 1, appModel.applicationInstances.size());
        appInstModel = appModel.applicationInstances.get(0);
        assertEquals(partId + "expected 'Kamino 1.0', got '" + appInstModel.name + "'", "Kamino 1.0", appInstModel.name);

        assertNotNull(partId + "received null-pointer: 'loadBalancers'", appModel.loadBalancers);
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + appModel.loadBalancers.size(), 1, appModel.loadBalancers.size());
        loadBalancerModel = appModel.loadBalancers.get(0);
        assertEquals(partId + "expected 'Per', got '" + loadBalancerModel.name + "'", "Per", loadBalancerModel.name);

        partId = methodId + " - 2: "; // check 'completeness' of middle object
        appModel = appModels.get(1);
        assertEquals(partId + "expected 'Grandiosa', got '" + appModel.name + "'", "Grandiosa", appModel.name);
        assertEquals(partId + "expected 'www.grandiosa.no', got '" + appModel.publicURL + "'", "www.grandiosa.no", appModel.publicURL);

        assertNotNull(partId + "applicationGroupID " + appModel.applicationGroupId + " did not match the ID of any ApplicationGroup", applicationGroupRepository.findOne(appModel.applicationGroupId));
        groupName = applicationGroupRepository.findOne(appModel.applicationGroupId).getName();
        assertEquals(partId + "expected 'GroupTwo', got '" + groupName + "'", "GroupTwo", groupName);

        assertNotNull(partId + "received null-pointer: 'applicationInstances'", appModel.applicationInstances);
        assertEquals(partId + "expected list 'applicationInstances' to be of size 2, found size " + appModel.applicationInstances.size(), 2, appModel.applicationInstances.size());
        List<AppInstModel> appInstModels = appModel.applicationInstances;
        Collections.sort(appInstModels, (o1, o2) -> { return o1.id.compareTo(o2.id); });
        appInstModel = appInstModels.get(0);
        assertEquals(partId + "expected 'Grandiosa 1.0', got '" + appInstModel.name + "'", "Grandiosa 1.0", appInstModel.name);
        appInstModel = appInstModels.get(1);
        assertEquals(partId + "expected 'Alpha 1.0', got '" + appInstModel.name + "'", "Alpha 1.0", appInstModel.name);

        assertNotNull(partId + "received null-pointer: 'loadBalancers'", appModel.loadBalancers);
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + appModel.loadBalancers.size() + "", 1, appModel.loadBalancers.size());
        loadBalancerModel = appModel.loadBalancers.get(0);
        assertEquals(partId + "expected 'Knut', got '" + loadBalancerModel.name + "'", "Knut", loadBalancerModel.name);

        partId = methodId + " - 3: "; // check 'completeness' of last object
        appModel = appModels.get(2);
        assertEquals(partId + "expected 'Alpha', got '" + appModel.name + "'", "Alpha", appModel.name);
        assertEquals(partId + "expected 'www.alpha.no', got '" + appModel.publicURL + "'", "www.alpha.no", appModel.publicURL);

        assertNotNull(partId + "applicationGroupID " + appModel.applicationGroupId + " did not match the ID of any ApplicationGroup", applicationGroupRepository.findOne(appModel.applicationGroupId));
        groupName = applicationGroupRepository.findOne(appModel.applicationGroupId).getName();
        assertEquals(partId + "expected 'GroupTwo', got '" + groupName + "'", "GroupTwo", groupName);

        assertNotNull(partId + "received null-pointer: 'applicationInstances'", appModel.applicationInstances);
        assertEquals(partId + "expected list 'applicationInstances' to be of size 0, found size " + appModel.applicationInstances.size(), 0, appModel.applicationInstances.size());

        assertNotNull(partId + "received null-pointer: 'loadBalancers'", appModel.loadBalancers);
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + appModel.loadBalancers.size(), 1, appModel.loadBalancers.size());
        loadBalancerModel = appModel.loadBalancers.get(0);
        assertEquals(partId + "expected 'Knut', got '" + loadBalancerModel.name + "'", "Knut", loadBalancerModel.name);
    }

    @Test
    public void testSearch() throws Exception {
        String methodId = classId + " - testSearch";
        String partId;
        AppModel appModel;
        List<AppModel> appModels;

        partId = methodId + " - 1: "; // search for last object
        appModels = applicationController.search("Alpha");
        //Collections.sort(appModels, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        assertNotNull(partId + "received null-pointer: 'appModels'", appModels);
        assertEquals(partId + "expected list 'appModels' to be of size 1, found size " + appModels.size(), 1, appModels.size());
        appModel = appModels.get(0);
        assertEquals(partId + "expected 'Alpha', got '" + appModel.name + "'", "Alpha", appModel.name);

        partId = methodId + " - 2: "; // search for middle object
        appModels = applicationController.search("Grandiosa");
        //Collections.sort(appModels, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        assertNotNull(partId + "received null-pointer: 'appModels'", appModels);
        assertEquals(partId + "expected list 'appModels' to be of size 1, found size " + appModels.size(), 1, appModels.size());
        appModel = appModels.get(0);
        assertEquals(partId + "expected 'Grandiosa', got '" + appModel.name + "'", "Grandiosa", appModel.name);

        partId = methodId + " - 3: "; // search for first object
        appModels = applicationController.search("Kamino");
        //Collections.sort(appModels, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        assertNotNull(partId + "received null-pointer: 'appModels'", appModels);
        assertEquals(partId + "expected list 'appModels' to be of size 1, found size " + appModels.size(), 1, appModels.size());
        appModel = appModels.get(0);
        assertEquals(partId + "expected 'Kamino', got '" + appModel.name + "'", "Kamino", appModel.name);

        partId = methodId + " - 4: "; // search for nonexistent object
        appModels = applicationController.search("Batman");
        //Collections.sort(appModels, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        assertNotNull(partId + "received null-pointer: 'appModels'", appModels);
        assertEquals(partId + "expected list 'appModels' to be of size 0, found size " + appModels.size(), 0, appModels.size());
    }

    @Test
    public void testFindById() throws Exception {
        String methodId = classId + " - testFindById";
        String partId;
        AppModel appModel;

        List<AppModel> appModels = applicationController.listAllApps();
        Collections.sort(appModels, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });

        partId = methodId + " - 1: "; // find last object
        appModel = appModels.get(2);
        assertEquals(partId + "expected '" + appModel.name + "', got '" + applicationController.findById(appModel.getId()).name + "'", appModel.name, applicationController.findById(appModel.getId()).name);

        partId = methodId + " - 2: ";
        appModel = appModels.get(0); // find first object
        assertEquals(partId + "expected '" + appModel.name + "', got '" + applicationController.findById(appModel.getId()).name + "'", appModel.name, applicationController.findById(appModel.getId()).name);

        partId = methodId + " - 3: ";
        appModel = appModels.get(1); // find middle object
        assertEquals(partId + "expected '" + appModel.name + "', got '" + applicationController.findById(appModel.getId()).name + "'", appModel.name, applicationController.findById(appModel.getId()).name);

        /*
        partId = methodId + " - 4: "; // attempt to find nonexistent object
        assertNull(partId + "expected null-pointer on applicationID 10'", applicationController.findById(10L));
        */
    }

    @Test
    public void testCreate() throws Exception {
        String methodId = classId + " - testCreate";
        String partId;

        partId = methodId + " - 1: "; // create application

        List<ApplicationGroup> applicationGroups = applicationGroupRepository.findAll();
        assertNotNull(partId + "received null-pointer: 'applicationGroups'", applicationGroups);
        Collections.sort(applicationGroups, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        AppGroupModel appGroupModel = new AppGroupModel(applicationGroups.get(0));
        assertNotNull(partId + "received null-pointer: 'appGroupModel'", appGroupModel);
        Application application = new Application("Beta", "www.beta.no", applicationGroupRepository.findOne(appGroupModel.getId()));
        assertNotNull(partId + "received null-pointer: 'application'", application);
        AppModel appModel = new AppModel(application);
        assertNotNull(partId + "received null-pointer: 'appModel'", application);
        appModel = applicationController.create(appModel);
        assertNotNull(partId + "received null-pointer from create", appModel);
        assertEquals(partId + "expected 'Beta', got '" + appModel.name + "'", "Beta", appModel.name);
        applicationGroups = applicationGroupRepository.findAll();
        Collections.sort(applicationGroups, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        appGroupModel = new AppGroupModel(applicationGroups.get(0));
        assertNotNull(partId + "received null-pointer: 'applications'", appGroupModel.applications);
        assertEquals(partId + "expected list 'appModels' to be of size 2, found size " + appGroupModel.applications.size() + "", 2, appGroupModel.applications.size());
        List<AppModel> appModels =  appGroupModel.applications;
        Collections.sort(appModels, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        assertEquals(partId + "expected 'Beta', got '" + appModels.get(1).getName() + "'", "Beta", appModels.get(1).getName());

        partId = methodId + " - 2: "; // add ApplicationInstance - testRemove
        application = applicationRepository.findOne(appModel.getId());
        ApplicationInstance applicationInstance = new ApplicationInstance("Beta 1.0", "hostX", 8080, "www.beta.no/1.0", application);
        AppInstModel appInstModel = new AppInstModel(applicationInstance);
        assertNotNull(partId + "received null-pointer: 'appInstModel'", appInstModel);
        applicationInstanceController.create(application.getId(), appInstModel);
        ApplicationInstance applicationInstance2 = new ApplicationInstance("Beta 1.1", "hostX", 8080, "www.beta.no/1.1", application);
        AppInstModel appInstModel2 = new AppInstModel(applicationInstance2);
        applicationInstanceController.create(application.getId(), appInstModel2);
        application = applicationRepository.findOne(appModel.getId());
        List<ApplicationInstance> applicationInstances = application.getApplicationInstances();
        assertNotNull(partId + "received null-pointer: 'applicationInstances'", applicationInstances);
        Collections.sort(applicationInstances, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        assertEquals(partId + "expected list 'applicationInstances' to be of size 2, found size " + applicationInstances.size(), 2, applicationInstances.size());
        assertEquals(partId + "expected 'Beta 1.0', got '" + applicationInstances.get(0).getName() + "'", "Beta 1.0", applicationInstances.get(0).getName());
        assertNotNull(partId + "received null-pointer: 'application'", applicationInstance.getApplication());
        assertEquals(partId + "expected 'Beta', got '" + applicationInstance.getApplication().getName() + "'", "Beta", applicationInstance.getApplication().getName());

        partId = methodId + " - 3: "; // add LoadBalancer - testRemove
        List<LoadBalancer> loadBalancers = loadBalancerRepository.findAll();
        Collections.sort(loadBalancers, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancers.get(0));
        assertNotNull(partId + "received null-pointer: 'loadBalancerModel'", loadBalancerModel);
        loadBalancerController.addApplication(loadBalancerModel.id, application.getId());
        LoadBalancerModel loadBalancerModel2 = new LoadBalancerModel(loadBalancers.get(1));
        assertNotNull(partId + "received null-pointer: 'loadBalancerModel'", loadBalancerModel);
        loadBalancerController.addApplication(loadBalancerModel2.id, application.getId());
        application = applicationRepository.findOne(appModel.getId());
        List<LoadBalancer> appLoadBalancers = application.getLoadBalancers();
        assertNotNull(partId + "received null-pointer: 'loadBalancers'", appLoadBalancers);
        Collections.sort(appLoadBalancers, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        //assertEquals(partId + "expected list 'loadBalancers' to be of size 2, found size " + appLoadBalancers.size(), 2, appLoadBalancers.size());
        //assertEquals(partId + "expected 'Per', got '" + appLoadBalancers.get(0).getName() + "'", "Per", appLoadBalancers.get(0).getName());
        loadBalancers = loadBalancerRepository.findAll();
        Collections.sort(loadBalancers, (o1, o2) -> { return o1.getId().compareTo(o2.getId()); });
        loadBalancerModel = new LoadBalancerModel(loadBalancers.get(0));
        appModels = loadBalancerModel.applications;
        assertNotNull(partId + "received null-pointer: 'applications'", appModels);
        assertEquals(partId + "expected list 'applications' to be of size 2, found size " + appModels.size(), 2, appModels.size());
        assertEquals(partId + "expected 'Beta', got '" + appModels.get(1).getName() + "'", "Beta", appModels.get(1).getName());
    }

    @Test
    public void testRemove() throws Exception {
        String methodId = classId + " - testRemove";
        String partId;

        testCreate();

        partId = methodId + " - 1: "; // remove application from repository
        assertEquals(partId + "expected list 'applications' to be of size 4, found size " + applicationController.listAllApps().size(), 4, applicationController.listAllApps().size());
        AppModel appModel = applicationController.search("Beta").get(0);
        applicationController.remove(appModel.getId());
        //assertNotNull(partId + "received null-pointer: 'applicationRepository'", applicationRepository);
        //assertNotNull(partId + "received null-pointer: 'applicationsRepository.findAll'", applicationRepository.findAll());
        assertEquals(partId + "expected list 'applications' to be of size 3, found size " + applicationController.listAllApps().size(), 3, applicationController.listAllApps().size());

        partId = methodId + " - 2: "; // linked ApplicationInstances were also removed from repository
        List<AppInstModel> appInstModels = appModel.applicationInstances;
        Collections.sort(appInstModels, (o1, o2) -> { return o1.id.compareTo(o2.id); });
        assertFalse(partId + "expected null-pointer on ApplicationInstanceID " + appInstModels.get(0).id, applicationInstanceRepository.exists(appInstModels.get(0).id));
        assertFalse(partId + "expected null-pointer on ApplicationInstanceID " + appInstModels.get(1).id, applicationInstanceRepository.exists(appInstModels.get(1).id));

        partId = methodId + " - 3: "; // removed from list in ApplicationGroup
        ApplicationGroup applicationGroup = applicationGroupRepository.findOne(appModel.applicationGroupId);
        assertEquals(partId + "expected list 'applications' to be of size 1, found size " + applicationGroup.getApplications().size(), 1, applicationGroup.getApplications().size());

        partId = methodId + " - 4: "; // removed from list in LoadBalancers
        List<LoadBalancerModel> loadBalancerModels = appModel.loadBalancers;
        Collections.sort(loadBalancerModels, (o1, o2) -> { return o1.id.compareTo(o2.id); });
        LoadBalancer loadBalancer = loadBalancerRepository.findOne(loadBalancerModels.get(0).id);
        LoadBalancer loadBalancer2 = loadBalancerRepository.findOne(loadBalancerModels.get(1).id);
        assertEquals(partId + "expected list 'applications' to be of size 1, found size " + loadBalancer.getApplications().size(), 1, loadBalancer.getApplications().size());
        assertEquals(partId + "expected list 'applications' to be of size 2, found size " + loadBalancer2.getApplications().size(), 2, loadBalancer2.getApplications().size());
    }

    @Test
    public void testUpdate() throws Exception {
        String methodId = classId + " - testUpdate";
        String partId;

        partId = methodId + " - 1: ";
        List<AppModel> appModels = applicationController.listAllApps();
        Collections.sort(appModels, (o1, o2) -> { return o1.id.compareTo(o2.id); });
        AppModel appModel = appModels.get(0);
        String oldName = appModel.name;
        String oldUrl = appModel.publicURL;
        appModel.name = "Delta";
        appModel.publicURL = "www.delta.no";
        applicationController.update(appModel.getId(), appModel);
        appModels = applicationController.listAllApps();
        Collections.sort(appModels, (o1, o2) -> { return o1.id.compareTo(o2.id); });
        appModel = appModels.get(0);
        assertEquals(partId + "expected 'Delta', got '" + appModel.name + "'", "Delta", appModel.name);
        assertEquals(partId + "expected 'www.delta.no', got '" + appModel.publicURL + "'", "www.delta.no", appModel.publicURL);

        // sett back to old values
        appModel.name = oldName;
        appModel.publicURL = oldUrl;
        applicationController.update(appModel.getId(), appModel);
    }

    @Test
    public void testGetApplicationGroup() throws Exception {
        String methodId = classId + " - getApplicationGroup";
        String partId;

        List<AppModel> appModels = applicationController.listAllApps();
        Collections.sort(appModels, (o1, o2) -> { return o1.id.compareTo(o2.id); });
        assertEquals(classId + "expected list 'appModels' to be of size 3, found size " + appModels.size(), 3, appModels.size());

        partId = methodId + " - 1: ";
        assertNotNull(partId + "received null-pointer: 'applicationGroup'", applicationController.getApplicationGroup(appModels.get(0).getId()));
        assertEquals(partId + "expected applicationGroupID " + appModels.get(0).applicationGroupId + ", got '" + applicationController.getApplicationGroup(appModels.get(0).getId()).getId() + "'", appModels.get(0).applicationGroupId, applicationController.getApplicationGroup(appModels.get(0).getId()).getId());
        assertNotNull(partId + "received null-pointer: 'applicationGroup'", applicationController.getApplicationGroup(appModels.get(1).getId()));
        assertEquals(partId + "expected applicationGroupID " + appModels.get(1).applicationGroupId + ", got '" + applicationController.getApplicationGroup(appModels.get(1).getId()).getId() + "'", appModels.get(1).applicationGroupId, applicationController.getApplicationGroup(appModels.get(1).getId()).getId());
        assertNotNull(partId + "received null-pointer: 'applicationGroup'", applicationController.getApplicationGroup(appModels.get(2).getId()));
        assertEquals(partId + "expected applicationGroupID " + appModels.get(2).applicationGroupId + ", got '" + applicationController.getApplicationGroup(appModels.get(2).getId()).getId() + "'", appModels.get(2).applicationGroupId, applicationController.getApplicationGroup(appModels.get(2).getId()).getId());
    }

    @Test
    public void testGetLoadBalancers() throws Exception {
        String methodId = classId + " - testGetLoadBalancers";
        String partId;

        List<AppModel> appModels = applicationController.listAllApps();
        Collections.sort(appModels, (o1, o2) -> { return o1.id.compareTo(o2.id); });

        partId = methodId + " - 1: ";
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + applicationController.getLoadBalancers(appModels.get(0).getId()).size(), 1, applicationController.getLoadBalancers(appModels.get(0).getId()).size());
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + applicationController.getLoadBalancers(appModels.get(1).getId()).size(), 1, applicationController.getLoadBalancers(appModels.get(1).getId()).size());
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + applicationController.getLoadBalancers(appModels.get(2).getId()).size(), 1, applicationController.getLoadBalancers(appModels.get(2).getId()).size());
    }
}