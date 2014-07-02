package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.ApplicationRepository;
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

import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfig.class})
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
        List<AppInstModel> appInstModels = applicationInstanceController.listAllAppInsts();
        assertThat(appInstModels).isNotNull().hasSize(3);
        Collections.sort(appInstModels, (o1, o2) -> o1.id.compareTo(o2.id));
        assertThat(appInstModels.get(1)).isNotNull();
        assertThat(appInstModels.get(1).name).isNotNull().isEqualTo("Grandiosa 1.0");
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(applicationInstanceController.search("Kamino 1.0")).isNotNull().hasSize(1);
        assertThat(applicationInstanceController.search(null)).isNotNull().hasSize(3);
    }

    @Test
    public void testFindById() throws Exception {
        assertThat(applicationInstanceController.findById(applicationInstanceController.listAllAppInsts().get(2).id)).isNotNull();
        assertThat(applicationInstanceController.findById(applicationInstanceController.listAllAppInsts().get(2).id).name).isNotNull().isEqualTo("Alpha 1.0");
        try {
            applicationController.findById(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testCreate() throws Exception {
        Application application =  applicationRepository.findOne(applicationController.search("Kamino").get(0).getId());
        ApplicationInstance applicationInstance = new ApplicationInstance("Beta 1.0", "host", 8080, "/beta/1.0", application);
        AppInstModel appInstModel = applicationInstanceController.create(application.getId(), new AppInstModel(applicationInstance));
        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(4);
        assertThat(applicationInstanceController.search("Beta 1.0")).isNotNull().hasSize(1);

        assertThat(appInstModel).isNotNull();
        assertThat(appInstModel.name).isNotNull().isEqualTo("Beta 1.0");
        assertThat(appInstModel.host).isNotNull().isEqualTo("host");
        assertThat(appInstModel.path).isNotNull().isEqualTo("/beta/1.0");
        assertThat(appInstModel.port).isNotNull().isEqualTo(8080);
        assertThat(appInstModel.applicationId).isNotNull().isEqualTo(application.getId());
    }

    @Test()
    public void testCreateUniqueName() throws Exception {
        Application application = applicationRepository.findByNameLike("Kamino").get(0);
        ApplicationInstance applicationInstance = new ApplicationInstance("Alpha 1.0", "X", 123, "X", application);
        AppInstModel appInstModel = new AppInstModel(applicationInstance);
        try {
            applicationInstanceController.create(application.getId(), appInstModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testRemove() throws Exception {
        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(3);
        assertThat(applicationController.search("Kamino").get(0).applicationInstances.size()).isNotNull().isEqualTo(1);
        assertThat(applicationController.search("Grandiosa").get(0).applicationInstances.size()).isNotNull().isEqualTo(2);

        applicationInstanceController.remove(applicationInstanceController.search("Alpha 1.0").get(0).id);
        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(2);
        assertThat(applicationController.search("Grandiosa").get(0).applicationInstances.size()).isNotNull().isEqualTo(1);
        assertThat(applicationController.search("Grandiosa").get(0).applicationInstances.get(0).name).isNotNull().isEqualTo("Grandiosa 1.0");

        try {
            applicationInstanceController.remove(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testUpdate() throws Exception {
        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(3);

        AppInstModel appInstModel = applicationInstanceController.search("Kamino 1.0").get(0);
        appInstModel.name = "Kamino 1.1";
        appInstModel.path = "/kamino/1.1";
        appInstModel.host = "new host";
        appInstModel.port = 8090;
        appInstModel = applicationInstanceController.update(appInstModel.id, appInstModel);

        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(3);
        assertThat(applicationInstanceController.search("Kamino 1.1").get(0).name).isNotNull().isEqualTo("Kamino 1.1");
        assertThat(appInstModel.name).isNotNull().isEqualTo("Kamino 1.1");
        assertThat(appInstModel.host).isNotNull().isEqualTo("new host");
        assertThat(appInstModel.path).isNotNull().isEqualTo("/kamino/1.1");
        assertThat(appInstModel.port).isNotNull().isEqualTo(8090);

        try {
            applicationInstanceController.update(-1L, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateUniqueName() throws Exception {
        AppInstModel appInstModel = applicationInstanceController.search("Kamino 1.0").get(0);

        assertThat(applicationInstanceController.update(appInstModel.id, appInstModel)).isNotNull();

        appInstModel.name = "Alpha 1.0";
        try {
            applicationInstanceController.update(appInstModel.id, appInstModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }
}