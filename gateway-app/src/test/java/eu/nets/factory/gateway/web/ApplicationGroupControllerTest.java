package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationGroupRepository;
import eu.nets.factory.gateway.model.ApplicationRepository;
import junit.framework.TestCase;
import org.junit.Assert;
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
public class ApplicationGroupControllerTest {

    @Autowired
    ApplicationGroupController applicationGroupController;

    @Autowired
    ApplicationController applicationController;

    @Autowired
    ApplicationInstanceController applicationInstanceController;

    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InitTestClass initTestClass;
    @Before
    public void Before() {
        initTestClass.init();
    }

    @Test
    public void testListAllAppGroups() throws Exception {
        List<AppGroupModel> appGroupModels = applicationGroupController.listAllAppGroups();
        assertThat(appGroupModels).isNotNull().hasSize(3);
        Collections.sort(appGroupModels, (o1, o2) -> {
            return o1.id.compareTo(o2.id);
        });
        assertThat(appGroupModels.get(1)).isNotNull();
        assertThat(appGroupModels.get(1).name).isNotNull().isEqualTo("GroupTwo");
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(applicationGroupController.search("GroupTwo")).isNotNull().hasSize(1);
        assertThat(applicationGroupController.search(null)).isNotNull().hasSize(3);
    }

    @Test
    public void testFindById() throws Exception {
        assertThat(applicationGroupController.findById(applicationGroupController.listAllAppGroups().get(2).id)).isNotNull();
        assertThat(applicationGroupController.findById(applicationGroupController.listAllAppGroups().get(2).id).name).isNotNull().isEqualTo("GroupThree");
    }

    @Test
    public void testCreate() throws Exception {
        ApplicationGroup applicationGroup = new ApplicationGroup("GroupX");
        AppGroupModel appGroupModel = applicationGroupController.create(new AppGroupModel(applicationGroup));
        assertThat(applicationGroupController.listAllAppGroups().size()).isNotNull().isEqualTo(4);
        assertThat(applicationGroupController.search("GroupX")).isNotNull().hasSize(1);

        assertThat(appGroupModel).isNotNull();
        assertThat(appGroupModel.name).isNotNull().isEqualTo("GroupX");
    }

    @Test()
    public void testCreateUniqueName() throws Exception {
        ApplicationGroup applicationGroup = new ApplicationGroup("GroupTwo");
        AppGroupModel appGroupModel = new AppGroupModel(applicationGroup);
        try {
            applicationGroupController.create(appGroupModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testRemove() throws Exception {
        assertThat(applicationGroupController.listAllAppGroups().size()).isNotNull().isEqualTo(3);
        assertThat(applicationController.listAllApps().size()).isNotNull().isEqualTo(3);
        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(3);

        applicationGroupController.remove(applicationGroupController.search("GroupOne").get(0).getId());
        assertThat(applicationGroupController.listAllAppGroups().size()).isNotNull().isEqualTo(2);
        assertThat(applicationController.listAllApps().size()).isNotNull().isEqualTo(2);
        assertThat(applicationInstanceController.listAllAppInsts().size()).isNotNull().isEqualTo(2);
        assertThat(applicationGroupController.search("GroupOne")).isNotNull().hasSize(0);
        assertThat(applicationController.search("Kamino")).isNotNull().hasSize(0);
        assertThat(applicationInstanceController.search("Kamino 1.0")).isNotNull().hasSize(0);
    }

    @Test
    public void testUpdate() throws Exception {
        assertThat(applicationGroupController.listAllAppGroups().size()).isNotNull().isEqualTo(3);

        AppGroupModel appGroupModel = applicationGroupController.search("GroupTwo").get(0);
        appGroupModel.name = "GroupX";
        appGroupModel = applicationGroupController.update(appGroupModel.id, appGroupModel);

        assertThat(applicationGroupController.listAllAppGroups().size()).isNotNull().isEqualTo(3);
        assertThat(applicationGroupController.search("GroupX").get(0).name).isNotNull().isEqualTo("GroupX");
        assertThat(appGroupModel.name).isNotNull().isEqualTo("GroupX");
    }

    @Test
    public void testUpdateUniqueName() throws Exception {
        AppGroupModel appGroupModel = applicationGroupController.search("GroupOne").get(0);
        appGroupModel.name = "GroupThree";
        try {
            applicationGroupController.update(appGroupModel.getId(), appGroupModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testGetApplications() throws Exception {
        assertThat(applicationGroupController.getApplications(applicationGroupController.search("GroupOne").get(0).getId())).isNotNull().hasSize(1);
        assertThat(applicationGroupController.getApplications(applicationGroupController.search("GroupTwo").get(0).getId())).isNotNull().hasSize(2);
        assertThat(applicationGroupController.getApplications(applicationGroupController.search("GroupThree").get(0).getId())).isNotNull().hasSize(0);

        assertThat(applicationGroupController.getApplications(applicationGroupController.search("GroupOne").get(0).getId()).get(0).name).isNotNull().isEqualTo("Kamino");
    }

    @Test
    public void testRemoveApplication() throws Exception {
        // tid function does not do anything...
    }
}