package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.LoadBalancer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


import eu.nets.factory.gateway.web.LoadBalancerController;
import eu.nets.factory.gateway.web.WebConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
@ActiveProfiles("unitTest")
@Transactional
public class LoadBalancerControllerTest {

    @Autowired
    private LoadBalancerController loadBalancerController;

    @Autowired
    private InitTestClass initTestClass;

    @Before
    public void Before() {
        initTestClass.init();
    }

    @Test
    public void testListAllLoadBalancers() throws Exception {
        assertNotNull(loadBalancerController);
    }

    @Test
    public void testSearch() throws Exception {

    }

    @Test
    public void testFindById() throws Exception {

    }

    @Test
    public void testFindBySshKey() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {
       /*LoadBalancer loadBalancer = new LoadBalancer("Snorre", "host", "instpath", "ssH");
       LoadBalancerModel model = new LoadBalancerModel(loadBalancer);
       assertEquals(model.name, loadBalancerController.create(model).name);
       //assertEquals(4, loadBalancerController.listAllLoadBalancers().size());*/
    }

    @Test
    public void testRemove() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testAddApplication() throws Exception {

    }

    @Test
    public void testGetApplications() throws Exception {

    }
}