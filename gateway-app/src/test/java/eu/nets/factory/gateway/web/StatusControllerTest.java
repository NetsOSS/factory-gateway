package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.LoadBalancer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
@ActiveProfiles("unitTest")
@Transactional
public class StatusControllerTest {

    @Autowired
    StatusController statusController;

    private final Logger log = getLogger(getClass());

    @Autowired
    LoadBalancerController loadBalancerController;

    @Test
    public void testParseCSV() throws Exception {

       LoadBalancer loadBalancer = new LoadBalancer("Grandiosa", "127.0.0.1", "/instPath", "sshKey", 10003);
       LoadBalancerModel loadBalancerModel = loadBalancerController.create(new LoadBalancerModel(loadBalancer));
       assertTrue(statusController.readCSV(loadBalancerModel).length() > 0);

    }
}