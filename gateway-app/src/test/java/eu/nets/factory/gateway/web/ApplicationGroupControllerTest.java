package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationGroupRepository;
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
@ContextConfiguration(classes={WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
@ActiveProfiles("unitTest")
@Transactional
public class ApplicationGroupControllerTest {

    @Autowired
    ApplicationGroupController applicationGroupController;

    @Autowired
    ApplicationController applicationController;

    @Autowired
    ApplicationInstanceController applicationInstanceController;

    @Autowired
    ApplicationGroupRepository applicationGroupRepository;

    @Autowired
    private InitTestClass initTestClass;
    @Before
    public void Before() {
        initTestClass.init();
    }

    @Test
    public void testListAllAppGroups() throws Exception {
        assertThat(applicationGroupController.listAllAppGroups()).isNotNull().hasSize(3).onProperty("name").contains("GroupOne", "GroupTwo", "GroupThree").excludes("GroupX");
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(applicationGroupController.search("GroupTwo")).isNotNull().hasSize(1).onProperty("name").contains("GroupTwo");
        assertThat(applicationGroupController.search("")).isNotNull().hasSize(3);
        assertThat(applicationGroupController.search(null)).isNotNull().hasSize(3);
    }

    @Test
    public void testFindEntityById() throws Exception {
        assertThat(applicationGroupController.findEntityById(applicationGroupController.search("GroupTwo").get(0).getId())).isNotNull();
        assertThat(applicationGroupController.findEntityById(applicationGroupController.search("GroupTwo").get(0).getId()).getName()).isNotNull().isEqualTo("GroupTwo");

        try {
            applicationGroupController.findEntityById(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            applicationGroupController.findEntityById(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testFindById() throws Exception {
        assertThat(applicationGroupController.findById(applicationGroupController.listAllAppGroups().get(2).id)).isNotNull();
        assertThat(applicationGroupController.findById(applicationGroupController.listAllAppGroups().get(2).id).name).isNotNull().isEqualTo("GroupThree");

        try {
            applicationGroupController.findById(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testAssertNameUnique() throws Exception {
        //assertThat(true).isEqualTo(false); // this method is private
    }

    @Test
    public void testCreate() throws Exception {
        ApplicationGroup applicationGroup = new ApplicationGroup("GroupX");
        AppGroupModel appGroupModel = applicationGroupController.create(new AppGroupModel(applicationGroup));
        assertThat(applicationGroupController.listAllAppGroups().size()).isNotNull().isEqualTo(4);
        assertThat(applicationGroupController.search("GroupX")).isNotNull().hasSize(1);

        assertThat(appGroupModel).isNotNull();
        assertThat(appGroupModel.name).isNotNull().isEqualTo("GroupX");

        try {
            applicationGroupController.create(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test()
    public void testCreateValidName() throws Exception {
        ApplicationGroup applicationGroup =  new ApplicationGroup("GroupX");
        AppGroupModel appGroupModel = new AppGroupModel(applicationGroup);

        try { //name already exists - not unique
            appGroupModel.name = "GroupTwo";
            applicationGroupController.create(appGroupModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //name is null
            appGroupModel.name = null;
            applicationGroupController.create(appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name is blank
            appGroupModel.name = "";
            applicationGroupController.create(appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name contains a whitespace
            appGroupModel.name = "as d";
            applicationGroupController.create(appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testRemove() throws Exception {
        assertThat(applicationGroupController.listAllAppGroups()).isNotNull().hasSize(3);
        assertThat(applicationController.listAllApps()).isNotNull().hasSize(3);
        assertThat(applicationInstanceController.listAllAppInsts()).isNotNull().hasSize(3);

        applicationGroupController.remove(applicationGroupController.search("GroupOne").get(0).getId());
        assertThat(applicationGroupController.listAllAppGroups()).isNotNull().hasSize(2).onProperty("name").excludes("GroupOne");

        assertThat(applicationController.listAllApps()).isNotNull().hasSize(2).onProperty("name").excludes("Kamino");

        assertThat(applicationInstanceController.listAllAppInsts()).isNotNull().hasSize(2).onProperty("name").excludes("Kamino1.0");

        try {
            applicationGroupController.remove(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            applicationGroupController.remove(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testUpdate() throws Exception {
        AppGroupModel appGroupModel = applicationGroupController.search("GroupTwo").get(0);

        appGroupModel.name = "GroupX";

        appGroupModel = applicationGroupController.update(appGroupModel.id, appGroupModel);

        assertThat(applicationGroupController.listAllAppGroups()).isNotNull().hasSize(3).onProperty("name").contains("GroupX").excludes("GroupTwo");

        try {
            applicationGroupController.update(-1L, appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateValidName() throws Exception {
        AppGroupModel appGroupModel = applicationGroupController.search("GroupOne").get(0);


        try {
            appGroupModel.name = "GroupThree";
            applicationGroupController.update(appGroupModel.getId(), appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name is blank
            appGroupModel.name = "";
            applicationGroupController.update(appGroupModel.getId(), appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name is null
            appGroupModel.name = null;
            applicationGroupController.update(appGroupModel.getId(), appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //name contains a whitespace
            appGroupModel.name = "as d";
            applicationGroupController.update(appGroupModel.getId(), appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }


    @Test
    public void testGetApplications() throws Exception {
        assertThat(applicationGroupController.getApplications(applicationGroupController.search("GroupOne").get(0).getId())).isNotNull().hasSize(1).onProperty("name").contains("Kamino");
        assertThat(applicationGroupController.getApplications(applicationGroupController.search("GroupTwo").get(0).getId())).isNotNull().hasSize(2).onProperty("name").contains("Grandiosa", "Alpha");
        assertThat(applicationGroupController.getApplications(applicationGroupController.search("GroupThree").get(0).getId())).isNotNull().hasSize(0);

        try {
            applicationGroupController.getApplications(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            applicationGroupController.getApplications(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testRemoveApplication() throws Exception {
        // tis function does not do anything...
    }
}