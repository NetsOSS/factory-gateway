package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.LoadBalancer;
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
public class LoadBalancerControllerTest {

    @Autowired
    LoadBalancerController loadBalancerController;

    @Autowired
    ApplicationController applicationController;

    @Autowired
    private InitTestClass initTestClass;
    @Before
    public void Before() {
        initTestClass.init();
    }

    @Test
    public void testListAllLoadBalancers() throws Exception {
        assertThat(loadBalancerController.listAllLoadBalancers()).isNotNull().hasSize(3).onProperty("name").contains("Per", "Knut", "Hans");
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(loadBalancerController.search("Knut")).isNotNull().hasSize(1).onProperty("name").contains("Knut");
        assertThat(loadBalancerController.search("")).isNotNull().hasSize(3).onProperty("name").contains("Per", "Knut", "Hans");
        assertThat(loadBalancerController.search(null)).isNotNull().hasSize(3).onProperty("name").contains("Per", "Knut", "Hans");
    }

    @Test
    public void testFindEntityById() throws Exception {
        assertThat(loadBalancerController.findEntityById(loadBalancerController.search("Knut").get(0).getId())).isNotNull();
        assertThat(loadBalancerController.findEntityById(loadBalancerController.search("Knut").get(0).getId()).getName()).isNotNull().isEqualTo("Knut");

        try {
            loadBalancerController.findEntityById(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            loadBalancerController.findEntityById(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testFindById() throws Exception {
        assertThat(loadBalancerController.findById(loadBalancerController.search("Hans").get(0).getId())).isNotNull();
        assertThat(loadBalancerController.findById(loadBalancerController.search("Hans").get(0).getId()).getName()).isNotNull().isEqualTo("Hans");

        try {
            loadBalancerController.findById(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            loadBalancerController.findById(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }


    @Test
    public void testCreate() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "hostX", "/instPathX", "sshX", 456, "factory", 2000, 1000, 60000, 60000, 3);

        LoadBalancerModel loadBalancerModel = loadBalancerController.create(new LoadBalancerModel(loadBalancer));
        assertThat(loadBalancerController.listAllLoadBalancers()).isNotNull().hasSize(4).onProperty("name").contains("Batman");

        assertThat(loadBalancerModel).isNotNull();
        assertThat(loadBalancerModel.name).isNotNull().isEqualTo("Batman");
        assertThat(loadBalancerModel.checkTimeout).isNotNull().isEqualTo(2000);
        assertThat(loadBalancerModel.connectTimeout).isNotNull().isEqualTo(1000);
        assertThat(loadBalancerModel.clientTimeout).isNotNull().isEqualTo(60000);
        assertThat(loadBalancerModel.serverTimeout).isNotNull().isEqualTo(60000);
        assertThat(loadBalancerModel.retries).isNotNull().isEqualTo(3);

        try { //model is null
            loadBalancerController.create(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test()
    public void testCreateValidName() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "X", "/X", "X", 567, "factory", 2000, 1000, 60000, 60000, 3);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);

        try { //name already exists - not unique
            loadBalancerModel.name = "Knut";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //name is null
            loadBalancerModel.name = null;
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //name is blank
            loadBalancerModel.name = "";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //name contains a whitespace
            loadBalancerModel.name = "as d";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testCreateValidHost() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "X", "/X", "X", 567, "factory", 2000, 1000, 60000, 60000, 3);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);

        try { //host is null
            loadBalancerModel.host = null;
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //host is blank
            loadBalancerModel.host = "";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testCreateValidInstallationPath() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "X", "/X", "X", 567, "factory", 2000, 1000, 60000, 60000, 3);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);

        try { //installationPath is null
            loadBalancerModel.installationPath = null;
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //installationPath is blank
            loadBalancerModel.installationPath = "";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //installationPath does not start with '/'
            loadBalancerModel.installationPath = "asd";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //installationPath does not start with '/[a-zA-Z]'
            loadBalancerModel.installationPath = "/3";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //installationPath contains whitespace
            loadBalancerModel.installationPath = "/as d";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testCreateValidSshKey() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "X", "/X", "X", 567, "factory", 2000, 1000, 60000, 60000, 3);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);

        try { //sshKey is null
            loadBalancerModel.sshKey = null;
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //sshKey is blank
            loadBalancerModel.sshKey = "";
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testCreateUniqueHostInstallationPath() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Bob", "hostTwo", "instPathTwo", "X", 567, "factory", 2000, 1000, 60000, 60000, 3);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);
        try {
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testCreateValidClientTimeoutAndServerTimeout() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "X", "/X", "X", 567, "factory", 2000, 1000, 60000, 60000, 3);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);
        try { //name contains a whitespace
            loadBalancerModel.clientTimeout = 1;
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test
    public void testRemove() throws Exception {
        assertThat(applicationController.search("Grandiosa").get(0).loadBalancers).isNotNull().hasSize(2);
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(1);

        loadBalancerController.remove(loadBalancerController.search("Knut").get(0).id);
        assertThat(loadBalancerController.listAllLoadBalancers()).isNotNull().hasSize(2).onProperty("name").excludes("Knut");
        assertThat(applicationController.listAllApps()).isNotNull().hasSize(3);

        assertThat(applicationController.search("Grandiosa").get(0).loadBalancers).isNotNull().hasSize(1).onProperty("name").excludes("Knut");
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(0);

        try {
            loadBalancerController.remove(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try {
            loadBalancerController.remove(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testUpdate() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Knut").get(0);

        loadBalancerModel.name = "Batman";
        loadBalancerModel.host = "Alfred";
        loadBalancerModel.installationPath = "/batcave";
        loadBalancerModel.sshKey = "nananananananana";
        loadBalancerModel.userName = "factory";
        loadBalancerModel.checkTimeout = 2000;
        loadBalancerModel.connectTimeout = 1000;
        loadBalancerModel.clientTimeout = 60000;
        loadBalancerModel.serverTimeout = 60000;
        loadBalancerModel.retries = 3;
        loadBalancerModel = loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);

        assertThat(loadBalancerController.listAllLoadBalancers()).isNotNull().hasSize(3).onProperty("name").contains("Batman").excludes("Knut");

        try {
            loadBalancerController.update(loadBalancerModel.getId(), null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //id is null
            loadBalancerModel.id = null;
            loadBalancerController.update(loadBalancerModel.getId(), loadBalancerModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //invalid id
            loadBalancerModel.id = -1L;
            loadBalancerController.update(loadBalancerModel.getId(), loadBalancerModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //id mismatch
            loadBalancerController.update(loadBalancerController.search("Per").get(0).getId(), loadBalancerModel);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test()
    public void testUpdateValidName() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        //name remains the same
        assertThat(loadBalancerController.update(loadBalancerModel.getId(), loadBalancerModel)).isInstanceOf(LoadBalancerModel.class);

        try { //name already exists - not unique
            loadBalancerModel.name = "Knut";
            loadBalancerController.update(loadBalancerModel.getId(), loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //name is null
            loadBalancerModel.name = null;
            loadBalancerController.update(loadBalancerModel.getId(), loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //name is blank
            loadBalancerModel.name = "";
            loadBalancerController.update(loadBalancerModel.getId(), loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //name contains a whitespace
            loadBalancerModel.name = "as d";
            loadBalancerController.update(loadBalancerModel.getId(), loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testUpdateValidHost() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        try { //host is null
            loadBalancerModel.host = null;
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //host is blank
            loadBalancerModel.host = "";
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testUpdateValidInstallationPath() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        try { //installationPath is null
            loadBalancerModel.installationPath = null;
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //installationPath is blank
            loadBalancerModel.installationPath = "";
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //installationPath does not start with '/'
            loadBalancerModel.installationPath = "asd";
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //installationPath does not start with '/[a-zA-Z]'
            loadBalancerModel.installationPath = "/3";
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //installationPath contains whitespace
            loadBalancerModel.installationPath = "/as d";
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test()
    public void testUpdateValidSshKey() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        try { //sshKey is null
            loadBalancerModel.sshKey = null;
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }

        try { //sshKey is blank
            loadBalancerModel.sshKey = "";
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test
    public void testUpdateUniqueHostInstallationPath() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        //host & installationPath remains the same
        loadBalancerModel.name = "X";
        assertThat(loadBalancerController.update(loadBalancerModel.id, loadBalancerModel)).isInstanceOf(LoadBalancerModel.class);

        try { //host & installationPath already exists - not unique
            loadBalancerModel.host = "hostTwo";
            loadBalancerModel.installationPath = "instPathTwo";
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateUniqueHostStatsPort() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        //host & statsPort remains the same
        loadBalancerModel.name = "X";
        loadBalancerModel.installationPath = "/X";
        assertThat(loadBalancerController.update(loadBalancerModel.id, loadBalancerModel)).isInstanceOf(LoadBalancerModel.class);

        loadBalancerModel.host = "hostTwo";
        int statsPort = loadBalancerController.update(loadBalancerModel.id, loadBalancerModel).getStatsPort();
        assertTrue(statsPort <= LoadBalancer.STATS_PORT_MAX);
        assertTrue(statsPort >= LoadBalancer.STATS_PORT_MIN);
    }

    @Test()
    public void testUpdateClientTimeoutEqualsServerTimeout() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "X", "/X", "X", 567, "factory", 2000, 1000, 60000, 60000, 3);
        LoadBalancerModel loadBalancerModel = loadBalancerController.create(new LoadBalancerModel(loadBalancer));
        try { //name contains a whitespace
            loadBalancerModel.clientTimeout = 1;
            loadBalancerController.update(loadBalancer.getId(), loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) { }
    }

    @Test
    public void testAddApplication() throws Exception {
        loadBalancerController.addApplication(loadBalancerController.search("Hans").get(0).id, applicationController.search("Kamino").get(0).id);

        assertThat(loadBalancerController.search("Hans").get(0).applications).isNotNull().hasSize(2).onProperty("name").contains("Kamino");
        assertThat(applicationController.search("Kamino").get(0).loadBalancers).isNotNull().hasSize(2).onProperty("name").contains("Hans");

        try { //application is already linked to loadBalancer
            loadBalancerController.addApplication(loadBalancerController.search("Hans").get(0).id, applicationController.search("Kamino").get(0).id);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //loadBalancer id is null
            loadBalancerController.addApplication(null, applicationController.search("Kamino").get(0).id);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //loadBalancer id is invalid
            loadBalancerController.addApplication(-1L, applicationController.search("Kamino").get(0).id);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //application id is null
            loadBalancerController.addApplication(loadBalancerController.search("Hans").get(0).id, null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //application id is invalid
            loadBalancerController.addApplication(loadBalancerController.search("Hans").get(0).id, -1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testGetApplications() throws Exception {
        assertThat(loadBalancerController.getApplications(loadBalancerController.search("Knut").get(0).id)).isNotNull().hasSize(2);
        assertThat(loadBalancerController.getApplications(loadBalancerController.search("Per").get(0).id).get(0).name).isNotNull().isEqualTo("Kamino");

        try { //id is null
            loadBalancerController.getApplications(null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //invalid id
            loadBalancerController.getApplications(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }

    @Test
    public void testRemoveApplicationFromLoadBalancer() throws Exception {
        loadBalancerController.removeApplicationFromLoadbalancer(loadBalancerController.search("Knut").get(0).id, applicationController.search("Alpha").get(0).id);
        assertThat(loadBalancerController.search("Knut").get(0).applications).isNotNull().hasSize(1).onProperty("name").contains("Grandiosa");
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(0);

        //attempt to remove an application from a loadBalancer to which it is not connected - this does not cast an exception
        assertThat(loadBalancerController.removeApplicationFromLoadbalancer(loadBalancerController.search("Knut").get(0).id, applicationController.search("Alpha").get(0).id)).isInstanceOf(LoadBalancerModel.class);

        try { //loadBalancer id is null
            loadBalancerController.removeApplicationFromLoadbalancer(null, applicationController.search("Grandiosa").get(0).id);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //invalid loadBalancer id
            loadBalancerController.removeApplicationFromLoadbalancer(-1L, applicationController.search("Grandiosa").get(0).id);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //application id is null
            loadBalancerController.removeApplicationFromLoadbalancer(loadBalancerController.search("Knut").get(0).id, null);
            fail("Expected exception");
        } catch(GatewayException ignore) { }

        try { //invalid application id
            loadBalancerController.removeApplicationFromLoadbalancer(loadBalancerController.search("Knut").get(0).id, -1L);
            fail("Expected exception");
        } catch(GatewayException ignore) { }
    }
}