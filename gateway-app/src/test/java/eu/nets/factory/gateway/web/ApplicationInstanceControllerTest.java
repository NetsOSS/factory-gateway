package eu.nets.factory.gateway.web;

import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
@ActiveProfiles("unitTest")
public class ApplicationInstanceControllerTest extends TestCase {

    public void testListAllAppInsts() throws Exception {

    }

    public void testSearch() throws Exception {

    }

    public void testFindById() throws Exception {

    }

    public void testCreate() throws Exception {

    }

    public void testRemove() throws Exception {

    }

    public void testUpdate() throws Exception {

    }
}