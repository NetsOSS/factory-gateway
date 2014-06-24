package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroupRepository;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import eu.nets.factory.gateway.web.ApplicationController;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
@ActiveProfiles("unitTest")
public class ApplicationControllerTest extends TestCase {

    @Autowired
    ApplicationController applicationController;

    @Autowired
    private ApplicationGroupRepository applicationInstanceRepository;

    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;

    @Autowired
    private ApplicationGroupRepository LoadBalancerRepository;

    String classId = "ApplicationControllerTest ";


    public void testListAllApps() throws Exception {
        String methodId = classId + "- testListAllApps ";
        String partId;
        String groupName;
        AppModel appModel;
        AppInstModel appInstModel;
        LoadBalancerModel loadBalancerModel;
        List<AppModel> appModels = applicationController.listAllApps();

        assertNotNull(methodId + ": received null-pointer: 'appModels'", appModels);
        assertEquals(methodId + ": expected list 'applications' to be of size 3, got " + appModels.size() + "", 3, appModels.size());

        appModel = appModels.get(0);
        partId = methodId + "- 1: ";

        assertEquals(partId + "expected 'Kamino', got '" + appModel.name + "'", "Kamino", appModel.name);
        assertEquals(partId + "expected 'www.kamino.no', got '" + appModel.publicURL + "'", "www.kamino.no", appModel.publicURL);

        assertNotNull(partId + "applicationGroupID " + appModel.applicationGroupId + " did not match the ID of any ApplicationGroup", applicationGroupRepository.findOne(appModel.applicationGroupId));
        groupName = applicationGroupRepository.findOne(appModel.applicationGroupId).getName();
        assertEquals(partId + "expected 'GroupOne', got '" + groupName + "'", "GroupOne", groupName);

        assertNotNull(partId + "received null-pointer: 'applicationInstances'", appModel.applicationInstances);
        assertEquals(partId + "expected list 'applicationInstances' to be of size 1, found size " + appModel.applicationInstances.size() + "", 1, appModel.applicationInstances.size());
        appInstModel = appModel.applicationInstances.get(0);
        assertEquals(partId + "expected 'Kamino 1.0', got '" + appInstModel.name + "'", "Kamino 1.0", appInstModel.name);

        assertNotNull(partId + "received null-pointer: 'loadBalancers'", appModel.loadBalancers);
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + appModel.loadBalancers.size() + "", 1, appModel.loadBalancers.size());
        loadBalancerModel = appModel.loadBalancers.get(0);
        assertEquals(partId + "expected 'Per', got '" + loadBalancerModel.name + "'", "Per", loadBalancerModel.name);

        appModel = appModels.get(1);
        partId = methodId + "- 2: ";

        assertEquals(partId + "expected 'Grandiosa', got '" + appModel.name + "'", "Grandiosa", appModel.name);
        assertEquals(partId + "expected 'www.grandiosa.no', got '" + appModel.publicURL + "'", "www.grandiosa.no", appModel.publicURL);

        assertNotNull(partId + "applicationGroupID " + appModel.applicationGroupId + " did not match the ID of any ApplicationGroup", applicationGroupRepository.findOne(appModel.applicationGroupId));
        groupName = applicationGroupRepository.findOne(appModel.applicationGroupId).getName();
        assertEquals(partId + "expected 'GroupTwo', got '" + groupName + "'", "GroupTwo", groupName);

        assertNotNull(partId + "received null-pointer: 'applicationInstances'", appModel.applicationInstances);
        assertEquals(partId + "expected list 'applicationInstances' to be of size 2, found size " + appModel.applicationInstances.size() + "", 2, appModel.applicationInstances.size());
        appInstModel = appModel.applicationInstances.get(0);
        assertEquals(partId + "expected 'Grandiosa 1.0', got '" + appInstModel.name + "'", "Grandiosa 1.0", appInstModel.name);
        appInstModel = appModel.applicationInstances.get(1);
        assertEquals(partId + "expected 'Alpha 1.0', got '" + appInstModel.name + "'", "Alpha 1.0", appInstModel.name);

        assertNotNull(partId + "received null-pointer: 'loadBalancers'", appModel.loadBalancers);
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + appModel.loadBalancers.size() + "", 1, appModel.loadBalancers.size());
        loadBalancerModel = appModel.loadBalancers.get(0);
        assertEquals(partId + "expected 'Knut', got '" + loadBalancerModel.name + "'", "Knut", loadBalancerModel.name);

        appModel = appModels.get(2);
        partId = methodId + "- 3: ";

        assertEquals(partId + "expected 'Alpha', got '" + appModel.name + "'", "Alpha", appModel.name);
        assertEquals(partId + "expected 'www.alpha.no', got '" + appModel.publicURL + "'", "www.alpha.no", appModel.publicURL);

        assertNotNull(partId + "applicationGroupID " + appModel.applicationGroupId + " did not match the ID of any ApplicationGroup", applicationGroupRepository.findOne(appModel.applicationGroupId));
        groupName = applicationGroupRepository.findOne(appModel.applicationGroupId).getName();
        assertEquals(partId + "expected 'GroupTwo', got '" + groupName + "'", "GroupTwo", groupName);

        assertNotNull(partId + "received null-pointer: 'applicationInstances'", appModel.applicationInstances);
        assertEquals(partId + "expected list 'applicationInstances' to be of size 0, found size " + appModel.applicationInstances.size() + "", 0, appModel.applicationInstances.size());

        assertNotNull(partId + "received null-pointer: 'loadBalancers'", appModel.loadBalancers);
        assertEquals(partId + "expected list 'loadBalancers' to be of size 1, found size " + appModel.loadBalancers.size() + "", 1, appModel.loadBalancers.size());
        loadBalancerModel = appModel.loadBalancers.get(0);
        assertEquals(partId + "expected 'Knut', got '" + loadBalancerModel.name + "'", "Knut", loadBalancerModel.name);
    }

    public void testSearch() throws Exception {
        String methodId = classId + "- testSearch ";
        String partId;
        AppModel appModel;
        List<AppModel> appModels;

        partId = methodId + "- 1: ";
        appModels = applicationController.search("Alpha");
        assertNotNull(partId + "received null-pointer: 'appModels'", appModels);
        assertEquals(partId + "expected list 'appModels' to be of size 1, found size " + appModels.size() + "", 1, appModels.size());
        appModel = appModels.get(0);
        assertEquals(partId + "expected 'Alpha', got '" + appModel.name + "'", "Alpha", appModel.name);

        partId = methodId + "- 2: ";
        appModels = applicationController.search("Grandiosa");
        assertNotNull(partId + "received null-pointer: 'appModels'", appModels);
        assertEquals(partId + "expected list 'appModels' to be of size 1, found size " + appModels.size() + "", 1, appModels.size());
        appModel = appModels.get(0);
        assertEquals(partId + "expected 'Grandiosa', got '" + appModel.name + "'", "Grandiosa", appModel.name);

        partId = methodId + "- 3: ";
        appModels = applicationController.search("Kamino");
        assertNotNull(partId + "received null-pointer: 'appModels'", appModels);
        assertEquals(partId + "expected list 'appModels' to be of size 1, found size " + appModels.size() + "", 1, appModels.size());
        appModel = appModels.get(0);
        assertEquals(partId + "expected 'Kamino', got '" + appModel.name + "'", "Kamino", appModel.name);

        partId = methodId + "- 4: ";
        appModels = applicationController.search("Batman");
        assertNotNull(partId + "received null-pointer: 'appModels'", appModels);
        assertEquals(partId + "expected list 'appModels' to be of size 0, found size " + appModels.size() + "", 0, appModels.size());
    }

    public void testFindById() throws Exception {
        String methodId = classId + "- testFindById ";
        String partId;
        AppModel appModel;
        List<AppModel> appModels = applicationController.listAllApps();


        partId = methodId + "- 1: ";
        appModel = appModels.get(2);
        assertEquals(partId + "expected '" + appModel.name + "', got '" + applicationController.findById(appModel.getId()).name + "'", appModel.name, applicationController.findById(appModel.getId()).name);

        partId = methodId + "- 2: ";
        appModel = appModels.get(0);
        assertEquals(partId + "expected '" + appModel.name + "', got '" + applicationController.findById(appModel.getId()).name + "'", appModel.name, applicationController.findById(appModel.getId()).name);

        partId = methodId + "- 3: ";
        appModel = appModels.get(1);
        assertEquals(partId + "expected '" + appModel.name + "', got '" + applicationController.findById(appModel.getId()).name + "'", appModel.name, applicationController.findById(appModel.getId()).name);

        /*
        partId = methodId + "- 4: ";
        assertNull(partId + "expected null-pointer on applicationID 10'", applicationController.findById(10L));
        */
    }

    public void testCreate() throws Exception {

    }

    public void testRemove() throws Exception {

    }

    public void testUpdate() throws Exception {

    }

    public void testGetApplicationGroup() throws Exception {

    }

    public void testGetLoadBalancers() throws Exception {

    }
}