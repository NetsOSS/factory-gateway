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
    public void testFindBySshKey() throws Exception {
        assertThat(loadBalancerController.findBySshKey("sshTwo")).isNotNull();
        assertThat(loadBalancerController.findBySshKey("sshTwo").name).isNotNull().isEqualTo("Knut");

        try {
            loadBalancerController.findBySshKey("derp");
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testAssertNameUnique() throws Exception {
        //assertThat(true).isEqualTo(false); // this method is private
    }

    @Test
    public void testAssertHostInstallationPathUnique() throws Exception {
        //assertThat(true).isEqualTo(false); // this method is private
    }

    @Test
    public void testAssertHostPublicPortUnique() throws Exception {
        //assertThat(true).isEqualTo(false); // this method is private
    }

    @Test
    public void testCreate() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "hostX", "instPathX", "sshX", 456);

        LoadBalancerModel loadBalancerModel = loadBalancerController.create(new LoadBalancerModel(loadBalancer));
        assertThat(loadBalancerController.listAllLoadBalancers()).isNotNull().hasSize(4).onProperty("name").contains("Batman");

        assertThat(loadBalancerModel).isNotNull();
        assertThat(loadBalancerModel.name).isNotNull().isEqualTo("Batman");

        try { //model is null
            loadBalancerController.create(null);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test()
    public void testCreateUniqueName() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Knut", "X", "X", "X", 567);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);
        try {
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test()
    public void testCreateUniqueHostInstallationPath() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Bob", "hostTwo", "instPathTwo", "X", 567);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);
        try {
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test()
    public void testCreateUniqueHostPublicPort() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Bob", "hostTwo", "X", "X", 234);
        LoadBalancerModel loadBalancerModel = new LoadBalancerModel(loadBalancer);
        try {
            loadBalancerController.create(loadBalancerModel);
            fail("Expected exception");
        } catch (GatewayException ignore) {
        }
    }

    @Test
    public void testRemove() throws Exception {
        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(3);
        assertThat(applicationController.listAllApps().size()).isNotNull().isEqualTo(3);
        assertThat(applicationController.search("Grandiosa").get(0).loadBalancers).isNotNull().hasSize(2);
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(1);

        loadBalancerController.remove(loadBalancerController.search("Knut").get(0).id);
        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(2);
        assertThat(applicationController.listAllApps().size()).isNotNull().isEqualTo(3);

        assertThat(applicationController.search("Grandiosa").get(0).loadBalancers).isNotNull().hasSize(1);
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(0);

        try {
            loadBalancerController.remove(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testUpdate() throws Exception {
        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(3);

        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Knut").get(0);
        loadBalancerModel.name = "Batman";
        loadBalancerModel.host = "Alfred";
        loadBalancerModel = loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);

        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(3);
        assertThat(loadBalancerController.search("Batman").get(0).name).isNotNull().isEqualTo("Batman");
        assertThat(loadBalancerModel.name).isNotNull().isEqualTo("Batman");

        try {
            loadBalancerController.update(-1L, loadBalancerModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateUniqueName() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        loadBalancerModel.host = "X";
        loadBalancerModel.installationPath = "X";
        loadBalancerModel.sshKey = "X";
        loadBalancerModel.publicPort = 987;

        assertThat(loadBalancerController.update(loadBalancerModel.id, loadBalancerModel)).isNotNull();

        loadBalancerModel.name = "Hans";
        try {
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateUniqueHostInstallationPath() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        loadBalancerModel.name = "X";
        loadBalancerModel.sshKey = "X";
        loadBalancerModel.publicPort = 987;

        assertThat(loadBalancerController.update(loadBalancerModel.id, loadBalancerModel)).isNotNull();

        loadBalancerModel.host = "hostTwo";
        loadBalancerModel.installationPath = "instPathTwo";
        try {
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testUpdateUniqueHostPublicPort() throws Exception {
        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Per").get(0);

        loadBalancerModel.name = "X";
        loadBalancerModel.installationPath = "X";
        loadBalancerModel.sshKey = "X";

        assertThat(loadBalancerController.update(loadBalancerModel.id, loadBalancerModel)).isNotNull();

        loadBalancerModel.host = "hostTwo";
        loadBalancerModel.publicPort = 234;
        try {
            loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testAddApplication() throws Exception {
        loadBalancerController.addApplication(loadBalancerController.search("Hans").get(0).id, applicationController.search("Kamino").get(0).id);
        assertThat(loadBalancerController.search("Hans").get(0).applications).isNotNull().hasSize(2);
        assertThat(loadBalancerController.search("Hans").get(0).applications).isNotNull().onProperty("name").contains("Grandiosa", "Kamino");
        assertThat(applicationController.search("Kamino").get(0).loadBalancers).isNotNull().hasSize(2);

        try {
            loadBalancerController.addApplication(-1L, applicationController.search("Kamino").get(0).id);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }

        try {
            loadBalancerController.addApplication(loadBalancerController.search("Hans").get(0).id, -1L);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testGetApplications() throws Exception {
        assertThat(loadBalancerController.getApplications(loadBalancerController.search("Knut").get(0).id)).isNotNull().hasSize(2);
        assertThat(loadBalancerController.getApplications(loadBalancerController.search("Per").get(0).id).get(0).name).isNotNull().isEqualTo("Kamino");

        try {
            loadBalancerController.getApplications(-1L);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    @Test
    public void testRemoveApplicationFromLoadbalancer() throws Exception {
        loadBalancerController.removeApplicationFromLoadbalancer(loadBalancerController.search("Knut").get(0).id, applicationController.search("Alpha").get(0).id);
        assertThat(loadBalancerController.search("Knut").get(0).applications).isNotNull().hasSize(1);
        assertThat(loadBalancerController.search("Knut").get(0).applications.get(0).name).isNotNull().isEqualTo("Grandiosa");
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(0);

        try {
            loadBalancerController.addApplication(-1L, applicationController.search("Alpha").get(0).id);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }

        try {
            loadBalancerController.addApplication(loadBalancerController.search("Knut").get(0).id, -1L);
            fail("Expected exception");
        } catch(GatewayException ignore) {
        }
    }

    /*
    @Test
    public void testPushConfiguration() throws Exception {
        assertThat(true).isEqualTo(false);
    }

    @Test
    public void testStartLoadBalancer() throws Exception {
        assertThat(true).isEqualTo(false);
    }*/
}