package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationRepository;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import junit.framework.TestCase;
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
public class LoadBalancerControllerTest {

    @Autowired
    LoadBalancerController loadBalancerController;

    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

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
    public void testListAllLoadBalancers() throws Exception {
        List<LoadBalancerModel> loadBalancerModels = loadBalancerController.listAllLoadBalancers();
        assertThat(loadBalancerModels).isNotNull().hasSize(3);
        Collections.sort(loadBalancerModels, (o1, o2) -> { return o1.id.compareTo(o2.id); });
        assertThat(loadBalancerModels.get(1)).isNotNull();
        assertThat(loadBalancerModels.get(1).name).isNotNull().isEqualTo("Knut");
    }

    @Test
    public void testSearch() throws Exception {
        assertThat(loadBalancerController.search("Knut")).isNotNull().hasSize(1);
        assertThat(loadBalancerController.search(null)).isNotNull().hasSize(3);
    }

    @Test
    public void testFindById() throws Exception {
        assertThat(loadBalancerController.findById(loadBalancerController.listAllLoadBalancers().get(2).id)).isNotNull();
        assertThat(loadBalancerController.findById(loadBalancerController.listAllLoadBalancers().get(2).id).name).isNotNull().isEqualTo("Hans");
    }

    @Test
    public void testFindBySshKey() throws Exception {
        assertThat(loadBalancerController.findBySshKey("sshTwo")).isNotNull();
        assertThat(loadBalancerController.findBySshKey("sshTwo").name).isNotNull().isEqualTo("Knut");
    }

    @Test
    public void testCreate() throws Exception {
        LoadBalancer loadBalancer = new LoadBalancer("Batman", "hostX", "instPathX", "sshX", 456);
        LoadBalancerModel loadBalancerModel = loadBalancerController.create(new LoadBalancerModel(loadBalancer));
        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(4);
        assertThat(loadBalancerController.search("Batman")).isNotNull().hasSize(1);

        assertThat(loadBalancerModel).isNotNull();
        assertThat(loadBalancerModel.name).isNotNull().isEqualTo("Batman");
    }

    @Test
    public void testRemove() throws Exception {
        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(3);
        assertThat(applicationController.listAllApps().size()).isNotNull().isEqualTo(3);
        assertThat(applicationController.search("Grandiosa").get(0).loadBalancers).isNotNull().hasSize(1);
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(1);

        loadBalancerController.remove(loadBalancerController.search("Knut").get(0).id);
        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(2);
        assertThat(applicationController.listAllApps().size()).isNotNull().isEqualTo(3);

        assertThat(applicationController.search("Grandiosa").get(0).loadBalancers).isNotNull().hasSize(0);
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(0);
    }

    @Test
    public void testUpdate() throws Exception {
        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(3);

        LoadBalancerModel loadBalancerModel = loadBalancerController.search("Knut").get(0);
        loadBalancerModel.name = "Batman";
        loadBalancerModel = loadBalancerController.update(loadBalancerModel.id, loadBalancerModel);

        assertThat(loadBalancerController.listAllLoadBalancers().size()).isNotNull().isEqualTo(3);
        assertThat(loadBalancerController.search("Batman").get(0).name).isNotNull().isEqualTo("Batman");
        assertThat(loadBalancerModel.name).isNotNull().isEqualTo("Batman");
    }

    @Test
    public void testAddApplication() throws Exception {
        loadBalancerController.addApplication(loadBalancerController.search("Hans").get(0).id, applicationController.search("Kamino").get(0).id);
        assertThat(loadBalancerController.search("Hans").get(0).applications).isNotNull().hasSize(1);
        assertThat(loadBalancerController.search("Hans").get(0).applications.get(0).name).isNotNull().isEqualTo("Kamino");
        assertThat(applicationController.search("Kamino").get(0).loadBalancers).isNotNull().hasSize(2);
    }

    @Test
    public void testGetApplications() throws Exception {
        assertThat(loadBalancerController.getApplications(loadBalancerController.search("Knut").get(0).id)).isNotNull().hasSize(2);
        assertThat(loadBalancerController.getApplications(loadBalancerController.search("Per").get(0).id).get(0).name).isNotNull().isEqualTo("Kamino");
    }

    @Test
    public void testRemoveApplicationFromLoadbalancer() throws Exception {
        loadBalancerController.removeApplicationFromLoadbalancer(loadBalancerController.search("Knut").get(0).id, applicationController.search("Alpha").get(0).id);
        assertThat(loadBalancerController.search("Knut").get(0).applications).isNotNull().hasSize(1);
        assertThat(loadBalancerController.search("Knut").get(0).applications.get(0).name).isNotNull().isEqualTo("Grandiosa");
        assertThat(applicationController.search("Alpha").get(0).loadBalancers).isNotNull().hasSize(0);
    }

    @Test
    public void testPushConfiguration() throws Exception {
        /* TODO */
    }
}