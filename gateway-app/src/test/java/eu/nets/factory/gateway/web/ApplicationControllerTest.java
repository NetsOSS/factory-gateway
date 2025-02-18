package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.CustomAssertions;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroupRepository;
import eu.nets.factory.gateway.model.StickySession;
import java.util.List;
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
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebConfig.class})
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
    ApplicationGroupController applicationGroupController;

    @Autowired
    LoadBalancerController loadBalancerController;

    @Autowired
    private ApplicationGroupRepository applicationGroupRepository;


    @Autowired
    private InitTestClass initTestClass;

    @Before
    public void Before() {
        initTestClass.init();
    }

    @Test
    public void testListAllApps() throws Exception {
        List<AppModel> appModels = applicationController.listAllApps();
        assertThat(appModels).isNotNull().hasSize(3).onProperty("name").contains("Kamino", "Grandiosa", "Alpha").excludes("Beta");

        CustomAssertions.assertThat(appModels.get(0)).hasName("Kamino").hasPublicUrl("/kamino").hasEmails("mailTwo").hasCheckPath("/kamino/v1/ping");
        CustomAssertions.assertThat(appModels.get(1)).hasName("Grandiosa").hasPublicUrl("/grandiosa").hasEmails("mailTwo").hasCheckPath("/grandiosa/ping");
        CustomAssertions.assertThat(appModels.get(2)).hasName("Alpha").hasPublicUrl("/alpha").hasEmails("mailOne").hasCheckPath("/alpha/ping");
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(applicationController.search("Alpha")).isNotNull().hasSize(1).onProperty("name").containsExactly("Alpha");
        assertThat(applicationController.search("Grandiosa")).isNotNull().hasSize(1).onProperty("name").containsExactly("Grandiosa");
        assertThat(applicationController.search("Kamino")).isNotNull().hasSize(1).onProperty("name").containsExactly("Kamino");

        assertThat(applicationController.search("Beta")).isNotNull().hasSize(0);
        assertThat(applicationController.search("")).isNotNull().hasSize(3).onProperty("name").contains("Alpha", "Grandiosa", "Kamino");
        assertThat(applicationController.search(null)).isNotNull().hasSize(3);
    }

    @Test
    public void testGetApplicationByExactName() throws Exception {
        CustomAssertions.assertThat(applicationController.getApplicationByExactName("Alpha")).isNotNull().hasName("Alpha");

        assertThat(applicationController.getApplicationByExactName("Beta")).isNull();
        assertThat(applicationController.getApplicationByExactName("")).isNull();
        assertThat(applicationController.getApplicationByExactName(null)).isNull();
    }

    @Test
    public void testFindEntityById() throws Exception {
        assertThat(applicationController.findEntityById(applicationController.getApplicationByExactName("Grandiosa").getId())).isNotNull();
        assertThat(applicationController.findEntityById(applicationController.getApplicationByExactName("Grandiosa").getId()).getName()).isNotNull().isEqualTo("Grandiosa");

        try {
            applicationController.findEntityById(-1L);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try {
            applicationController.findEntityById(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testFindById() throws Exception {
        assertThat(applicationController.findById(applicationController.getApplicationByExactName("Grandiosa").getId())).isNotNull();
        assertThat(applicationController.findById(applicationController.getApplicationByExactName("Grandiosa").getId()).getName()).isNotNull().isEqualTo("Grandiosa");

        try {
            applicationController.findById(-1L);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try {
            applicationController.findById(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testCreate() throws Exception {
        Application application = new Application("Beta", "/beta", applicationGroupRepository.findByNameLike("GroupTwo").get(0), "betaMail", "/beta/ping", "/beta", 0);
        AppModel appModel = new AppModel(application);
        appModel = applicationController.create(appModel);

        assertThat(appModel).isNotNull();
        CustomAssertions.assertThat(appModel).hasName("Beta").hasPublicUrl("/beta").hasEmails("betaMail").hasCheckPath("/beta/ping");
        CustomAssertions.assertThat(appModel).hasAppGroup(applicationGroupRepository.findByNameLike("GroupTwo").get(0).getId()).hasExactAppInsts(new AppInstModel[]{}).hasExactLoadBalancers(new LoadBalancerModel[]{});

        assertThat(applicationController.listAllApps()).isNotNull().hasSize(4);
        appModel = applicationController.listAllApps().get(3);
        CustomAssertions.assertThat(appModel).hasName("Beta").hasPublicUrl("/beta").hasEmails("betaMail").hasCheckPath("/beta/ping");
        CustomAssertions.assertThat(appModel).hasAppGroup(applicationGroupRepository.findByNameLike("GroupTwo").get(0).getId()).hasExactAppInsts(new AppInstModel[]{}).hasExactLoadBalancers(new LoadBalancerModel[]{});

        assertThat(applicationController.findEntityById(appModel.getId()).getStickySession()).isNotNull().isEqualTo(StickySession.STICKY);

        try { // appModel == null
            applicationController.create(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test()
    public void testCreateValidName() throws Exception {
        Application application = new Application("Beta", "/beta", applicationGroupRepository.findByNameLike("GroupTwo").get(0), "betaMail", "/beta/ping", "/beta", 0);
        AppModel appModel = new AppModel(application);

        try { //name already exists - not unique
            appModel.name = "Alpha";
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //name is null
            appModel.name = null;
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //name is blank
            appModel.name = "";
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //name contains a whitespace
            appModel.name = "as d";
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test()
    public void testCreateValidPublicUrl() throws Exception {
        Application application = new Application("Beta", "/beta", applicationGroupRepository.findByNameLike("GroupTwo").get(0), "betaMail", "/beta/ping", "/beta", 0);
        AppModel appModel = new AppModel(application);

        try { //publicUrl is null
            appModel.publicUrl = null;
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //publicUrl is blank
            appModel.publicUrl = "";
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //publicUrl does not start with '/'
            appModel.publicUrl = "asd";
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }


    }

    @Test()
    public void testCreateValidCheckPath() throws Exception {
        Application application = new Application("Beta", "/beta", applicationGroupRepository.findByNameLike("GroupTwo").get(0), "betaMail", "/beta/ping", "/beta", 0);
        AppModel appModel = new AppModel(application);

        try { //checkPath is null
            appModel.checkPath = null;
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //checkPath is blank
            appModel.checkPath = "";
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //checkPath does not start with '/'
            appModel.checkPath = "asd";
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }


    }

    @Test()
    public void testCreateValidApplicationGroup() throws Exception {
        Application application = new Application("Alpha", "X", applicationGroupRepository.findByNameLike("GroupTwo").get(0), "", "/alpha/ping", "/alpha", 0);
        AppModel appModel = new AppModel(application);

        try { //applicationGroup has invalid id
            appModel.applicationGroupId = -1L;
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //applicationGroup id is null
            appModel.applicationGroupId = null;
            applicationController.create(appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testRemove() throws Exception {
        applicationController.remove(applicationController.search("Grandiosa").get(0).getId());
        assertThat(applicationController.listAllApps().size()).isNotNull().isEqualTo(2);

        assertThat(applicationInstanceController.listAllAppInsts()).isNotNull().hasSize(1).onProperty("name").excludes("Gransiosa1.0", "Alpha1.0");

        assertThat(applicationGroupController.listAllAppGroups()).isNotNull().hasSize(3);
        assertThat(applicationGroupController.search("GroupTwo").get(0).applications).isNotNull().hasSize(1).onProperty("name").excludes("Grandiosa");

        assertThat(loadBalancerController.listAllLoadBalancers()).isNotNull().hasSize(3);
        assertThat(loadBalancerController.search("Knut").get(0).applications).isNotNull().hasSize(1).onProperty("name").excludes("Grandiosa");

        try {
            applicationController.remove(-1L);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try {
            applicationController.remove(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testUpdate() throws Exception {
        AppModel appModel = applicationController.search("Grandiosa").get(0);

        appModel.name = "Beta";
        appModel.publicUrl = "/beta";
        appModel.emails = "BetaMail";
        appModel.checkPath = "/beta/ping";
        appModel.applicationInstances = null;
        appModel.loadBalancers = null;
        appModel.setStickySession("NOT_STICKY");
        appModel = applicationController.update(appModel.getId(), appModel);

        assertThat(applicationController.listAllApps()).isNotNull().hasSize(3).onProperty("name").excludes("Grandiosa").contains("Beta");
        CustomAssertions.assertThat(applicationController.search("Beta").get(0)).hasName("Beta").hasPublicUrl("/beta").hasEmails("BetaMail").hasCheckPath("/beta/ping");
        CustomAssertions.assertThat(applicationController.search("Beta").get(0)).doesNotHaveAppGroupId(-6L).hasAppInsts(new AppInstModel[]{}).hasLoadBalancers(new LoadBalancerModel[]{});
        assertThat(applicationController.search("Beta").get(0).getStickySession()).isNotNull().isEqualTo("NOT_STICKY");

        try { //model is null
            applicationController.update(appModel.getId(), null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //id is null
            appModel.id = null;
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //invalid id
            appModel.id = -1L;
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //id mismatch
            applicationController.update(applicationController.search("Kamino").get(0).getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateValidName() throws Exception {
        AppModel appModel = applicationController.search("Grandiosa").get(0);

        //name remains the same
        assertThat(applicationController.update(appModel.getId(), appModel)).isInstanceOf(AppModel.class);

        try { //name already exists - not unique
            appModel.name = "Kamino";
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //name is null
            appModel.name = null;
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //name is blank
            appModel.name = "";
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //name contains a whitespace
            appModel.name = "as d";
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateValidPublicUrl() throws Exception {
        AppModel appModel = applicationController.search("Grandiosa").get(0);

        try { //publicUrl is blank
            appModel.publicUrl = "";
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //publicUrl does not start with '/'
            appModel.publicUrl = "asd";
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateValidCheckPath() throws Exception {
        AppModel appModel = applicationController.search("Grandiosa").get(0);

        try { //checkPath is blank
            appModel.checkPath = "a";
            applicationController.update(appModel.getId(), appModel);
            //boolean match = Pattern.matches("^/\\S*$", appModel.getCheckPath());
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //checkPath does not start with '/'
            appModel.checkPath = "asd";
            applicationController.update(appModel.getId(), appModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testGetApplicationGroup() throws Exception {

        assertThat(applicationController.getApplicationGroup(applicationController.search("Grandiosa").get(0).getId()).getId()).isNotNull().isEqualTo(applicationController.search("Grandiosa").get(0).applicationGroupId);

        try {
            applicationController.getApplicationGroup(-1L);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try {
            applicationController.getApplicationGroup(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testGetLoadBalancers() throws Exception {
        assertThat(applicationController.getLoadBalancers(applicationController.search("Grandiosa").get(0).getId())).isNotNull().hasSize(2).onProperty("name").contains("Knut", "Hans");

        try {
            applicationController.getLoadBalancers(-1L);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try {
            applicationController.getLoadBalancers(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testSetSticky() {

        AppModel modelOne = applicationController.findById(applicationController.search("Kamino").get(0).getId());
        AppModel modelTwo = applicationController.findById(applicationController.search("Grandiosa").get(0).getId());

        assertThat(modelOne).isNotNull();
        assertThat(modelTwo).isNotNull();

        assertThat(modelOne.getStickySession()).isNotNull().isEqualTo("STICKY");
        assertThat(modelTwo.getStickySession()).isNotNull().isEqualTo("STICKY");

        modelOne = applicationController.setSticky(modelOne.getId(), "NOT_STICKY");
        modelTwo = applicationController.setSticky(modelTwo.getId(), "NOT_STICKY");

        assertThat(modelOne.getStickySession()).isNotNull().isEqualTo("NOT_STICKY");
        assertThat(modelTwo.getStickySession()).isNotNull().isEqualTo("NOT_STICKY");

        try { //id null
            applicationController.setSticky(null, "STICKY");
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { //setup null
            applicationController.setSticky(modelOne.getId(), null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { // non excisting id
            applicationController.setSticky(-1L, "STICKY");
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }

        try { // non valid setup-value
            applicationController.setSticky(modelOne.getId(), "NON_VALID_SETUP");
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }
}