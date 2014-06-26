package eu.nets.factory.gateway.web;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
@ActiveProfiles("unitTest")
@Transactional
public class ApplicationInstanceControllerTest {

    @Test
    public void testListAllAppInsts() throws Exception {

        // alternative assert method:
        //assertThat(applicationController.getLoadBalancers(appModels.get(0).getId())).hasSize(2);

    }

    @Test
    public void testSearch() throws Exception {

    }

    @Test
    public void testFindById() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {

    }

    @Test
    public void testRemove() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }
}