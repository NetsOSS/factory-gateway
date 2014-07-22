package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.ApplicationRepository;
import eu.nets.factory.gateway.model.HaProxyState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
@ActiveProfiles("unitTest")
@Transactional
public class ApplicationInstanceControllerTest {

    @Autowired
    ApplicationInstanceController applicationInstanceController;

    @Autowired
    ApplicationController applicationController;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InitTestClass initTestClass;

    @Before
    public void Before() {
        initTestClass.init();
    }


    @Test
    public void testListAllAppInsts() throws Exception {
        assertThat(applicationInstanceController.listAllAppInsts()).isNotNull().hasSize(3).onProperty("name").contains("Kamino1.0", "Grandiosa1.0", "Alpha1.0").excludes("Beta1.0");
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(applicationInstanceController.search("Kamino1.0")).isNotNull().hasSize(1).onProperty("name").contains("Kamino1.0");
        assertThat(applicationInstanceController.search("")).isNotNull().hasSize(3);
        assertThat(applicationInstanceController.search(null)).isNotNull().hasSize(3);
    }

    @Test
    public void testFindEntityById() throws Exception {
        assertThat(applicationInstanceController.findEntityById(applicationInstanceController.search("Grandiosa1.0").get(0).getId())).isNotNull();
        assertThat(applicationInstanceController.findEntityById(applicationInstanceController.search("Grandiosa1.0").get(0).getId()).getName()).isNotNull().isEqualTo("Grandiosa1.0");

        try {
            applicationInstanceController.findEntityById(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            applicationInstanceController.findEntityById(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testFindById() throws Exception {
        assertThat(applicationInstanceController.findById(applicationInstanceController.search("Alpha1.0").get(0).id)).isNotNull();
        assertThat(applicationInstanceController.findById(applicationInstanceController.search("Alpha1.0").get(0).id).name).isNotNull().isEqualTo("Alpha1.0");

        try {
            applicationInstanceController.findById(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            applicationInstanceController.findById(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testAssertNameUnique() throws Exception {
        //assertThat(true).isEqualTo(false); // this method is private
    }

    @Test
    public void testCreate() throws Exception {
        Application application = applicationRepository.findByNameLike("Kamino").get(0);
        ApplicationInstance applicationInstance = new ApplicationInstance("Beta1.0", "host", 8080, "/beta/1.0", application);

        applicationInstanceController.create(application.getId(), new AppInstModel(applicationInstance));
        assertThat(applicationInstanceController.listAllAppInsts()).isNotNull().hasSize(4).onProperty("name").contains("Beta1.0");

        AppInstModel appInstModel = applicationInstanceController.search("Beta1.0").get(0);
        assertThat(appInstModel).isNotNull();
        assertThat(appInstModel.name).isNotNull().isEqualTo("Beta1.0");
        assertThat(appInstModel.server).isNotNull().isEqualTo("host:8080/beta/1.0");
        //assertThat(appInstModel.host).isNotNull().isEqualTo("host");
       // assertThat(appInstModel.path).isNotNull().isEqualTo("/beta/1.0");
        //assertThat(appInstModel.port).isNotNull().isEqualTo(8080);
        assertThat(appInstModel.applicationId).isNotNull().isEqualTo(application.getId());
        assertThat(appInstModel.haProxyState).isNotNull().isEqualTo(HaProxyState.READY);
        assertThat(appInstModel.getHaProxyState()).isNotNull().isEqualTo("READY");

        try { //model is null
            applicationInstanceController.create(applicationInstance.getApplication().getId(), null);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testCreateValidName() throws Exception {
        Application application = applicationRepository.findByNameLike("Kamino").get(0);
        ApplicationInstance applicationInstance = new ApplicationInstance("Beta1.0", "host", 8080, "/beta/1.0", application);
        AppInstModel appInstModel = new AppInstModel(applicationInstance);

        try { //name already exists - not unique
            appInstModel.name = "Alpha1.0";
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //name is null
            appInstModel.name = null;
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name is blank
            appInstModel.name = "";
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name contains a whitespace
            appInstModel.name = "as d";
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    /*
    @Test()
    public void testCreateValidHost() throws Exception {
        Application application =  applicationRepository.findByNameLike("Kamino").get(0);
        ApplicationInstance applicationInstance = new ApplicationInstance("Beta1.0", "host", 8080, "/beta/1.0", application);
        AppInstModel appInstModel = new AppInstModel(applicationInstance);

        try { //host is null
            appInstModel.host = null;
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //host is blank
            appInstModel.host = "";
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test()
    public void testCreateValidPort() throws Exception {
        Application application =  applicationRepository.findByNameLike("Kamino").get(0);
        ApplicationInstance applicationInstance = new ApplicationInstance("Beta1.0", "host", 8080, "/beta/1.0", application);
        AppInstModel appInstModel = new AppInstModel(applicationInstance);

        try { //port is null
            appInstModel.port = null;
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //port is less than 1
            appInstModel.port = 0;
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //port is greater than 65535
            appInstModel.port = 65536;
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test()
    public void testCreateValidPath() throws Exception {
        Application application =  applicationRepository.findByNameLike("Kamino").get(0);
        ApplicationInstance applicationInstance = new ApplicationInstance("Beta1.0", "host", 8080, "/beta/1.0", application);
        AppInstModel appInstModel = new AppInstModel(applicationInstance);

        try { //path is null
            appInstModel.path = null;
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
        
        //try { //path is blank
        //    appInstModel.path = "";
        //    applicationInstanceController.create(appInstModel.applicationId, appInstModel);
        //    fail("Expected exception");
        //} catch(GatewayException ignore) { }
        
        try { //path does not start with '/'
            appInstModel.path = "asd";
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //path does not start with '/[a-zA-Z]'
            appInstModel.path = "/3";
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //path contains whitespace
            appInstModel.path = "/as d";
            applicationInstanceController.create(appInstModel.applicationId, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }
    */

    @Test()
    public void testCreateValidApplication() throws Exception {
        Application application = applicationRepository.findByNameLike("Kamino").get(0);
        ApplicationInstance applicationInstance = new ApplicationInstance("Beta1.0", "host", 8080, "/beta/1.0", application);
        AppInstModel appInstModel = new AppInstModel(applicationInstance);

        try { //application id mismatch
            applicationInstanceController.create(-1L, appInstModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //application has invalid id
            appInstModel.applicationId = -1L;
            applicationInstanceController.create(appInstModel.getApplicationId(), appInstModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

         try { //application id is null
            appInstModel.applicationId = null;
            applicationInstanceController.create(appInstModel.getApplicationId(), appInstModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test
    public void testRemove() throws Exception {
        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(3);
        assertThat(applicationController.search("Grandiosa").get(0).applicationInstances.size()).isNotNull().isEqualTo(2);

        applicationInstanceController.remove(applicationInstanceController.search("Alpha1.0").get(0).id);
        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(2);

        assertThat(applicationController.search("Grandiosa").get(0).applicationInstances).isNotNull().hasSize(1).onProperty("name").contains("Grandiosa1.0").excludes("Alpha1.0");

        try {
            applicationInstanceController.remove(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            applicationInstanceController.remove(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testUpdate() throws Exception {
        AppInstModel appInstModel = applicationInstanceController.search("Kamino1.0").get(0);

        appInstModel.name = "Kamino1.1";
        //appInstModel.server = "newhost/kamino/1.1";
        //appInstModel.host = "new host";
        //appInstModel.port = 8090;
        appInstModel.setHaProxyState("MAINT");
        appInstModel = applicationInstanceController.update(appInstModel.id, appInstModel);

        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(3);
        assertThat(applicationInstanceController.search("Kamino1.1").get(0).name).isNotNull().isEqualTo("Kamino1.1");
        assertThat(appInstModel.name).isNotNull().isEqualTo("Kamino1.1");
//        assertThat(appInstModel.host).isNotNull().isEqualTo("new host");
//        assertThat(appInstModel.path).isNotNull().isEqualTo("/kamino/1.1");
//        assertThat(appInstModel.port).isNotNull().isEqualTo(8090);
        assertThat(appInstModel.getHaProxyState()).isNotNull().isEqualTo("MAINT");

        try { //model is null
            applicationInstanceController.update(appInstModel.getId(), null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //id is null
            appInstModel.id = null;
            applicationInstanceController.update(appInstModel.getId(), appInstModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //invalid id
            appInstModel.id = -1L;
            applicationInstanceController.update(appInstModel.getId(), appInstModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //id mismatch
            applicationInstanceController.update(applicationInstanceController.search("Alpha1.0").get(0).getId(), appInstModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }


    }

    @Test()
    public void testUpdateValidName() throws Exception {
        AppInstModel appInstModel = applicationInstanceController.search("Kamino1.0").get(0);

        //name remains the same
        assertThat(applicationInstanceController.update(appInstModel.getId(), appInstModel)).isInstanceOf(AppInstModel.class);

        try {//name already exists - not unique
            appInstModel.name = "Alpha1.0";
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name is blank
            appInstModel.name = "";
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name is null
            appInstModel.name = null;
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name contains a whitespace
            appInstModel.name = "as d";
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    /*
    @Test()
    public void testUpdateValidHost() throws Exception {
        AppInstModel appInstModel = applicationInstanceController.search("Kamino1.0").get(0);

        try { //host is null
            appInstModel.host = null;
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //host is blank
            appInstModel.host = "";
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test()
    public void testUpdateValidPort() throws Exception {
        AppInstModel appInstModel = applicationInstanceController.search("Kamino1.0").get(0);

        try { //port is null
            appInstModel.port = null;
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //port is less than 1
            appInstModel.port = 0;
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //port is greater than 65535
            appInstModel.port = 65536;
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test()
    public void testUpdateValidPath() throws Exception {
        AppInstModel appInstModel = applicationInstanceController.search("Kamino1.0").get(0);

        try { //path is null
            appInstModel.path = null;
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
        
        //try { //path is blank
        //    appInstModel.path = "";
        //    applicationInstanceController.update(appInstModel.id, appInstModel);
        //    fail("Expected exception");
        //} catch(GatewayException ignore) { }
        
        try { //path does not start with '/'
            appInstModel.path = "asd";
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //path does not start with '/[a-zA-Z]'
            appInstModel.path = "/3";
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //path contains whitespace
            appInstModel.path = "/as d";
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }
    */

    @Test()
    public void testURLTransform() throws Exception {
        AppInstModel appInstModel = applicationInstanceController.search("Kamino1.0").get(0);

        assertThat(appInstModel.getServer()).isNotNull().isEqualTo("hostOne:8080/kamino/1.0");
    }

    @Test()
    public void testSetProxyStateByInstanceName() throws Exception {

        AppInstModel appInstModel = applicationInstanceController.search("Kamino1.0").get(0);

        AppInstModel testModel = applicationInstanceController.setProxyStateForInstance(appInstModel.getName(), "MAINT");

        assertThat(testModel.applicationId).isNotNull().isEqualTo(appInstModel.applicationId);
        assertThat(testModel.id).isNotNull().isEqualTo(appInstModel.id);
        assertThat(testModel.name).isNotNull().isEqualTo("Kamino1.0");
//        assertThat(testModel.host).isNotNull().isEqualTo("hostOne");
//        assertThat(testModel.path).isNotNull().isEqualTo("/kamino/1.0");
        assertThat(testModel.getHaProxyState()).isNotNull().isEqualTo("MAINT");

        try {
            applicationInstanceController.setProxyStateForInstance(appInstModel.getName(), null);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try {
            applicationInstanceController.setProxyStateForInstance(null, "MAINT");
            fail("Expected exception");
        } catch (GatewayException ignore) { }
        try {
            applicationInstanceController.setProxyStateForInstance(appInstModel.getName(), "NOTVALIDSTATE");
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try {
            applicationInstanceController.setProxyStateForInstance("Namethatdoesnotexist", "MAINT");
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test
    public void testSetWeight() {
        AppInstModel appInstModel = applicationInstanceController.search("Alpha1.0").get(0);

        assertThat(appInstModel.getWeight()).isEqualTo(100);

        applicationInstanceController.setWeight(appInstModel.getId(), 0);
        assertThat(applicationInstanceController.search("Alpha1.0").get(0).getWeight()).isEqualTo(0);

        applicationInstanceController.setWeight(appInstModel.getId(), 256);
        assertThat(applicationInstanceController.search("Alpha1.0").get(0).getWeight()).isEqualTo(256);

        try { //weight is less than 0
            applicationInstanceController.setWeight(appInstModel.getId(), -1);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //weight is greater than 256
            applicationInstanceController.setWeight(appInstModel.getId(), 257);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }
}