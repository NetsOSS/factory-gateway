package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ModelConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfig.class})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
public class ApplicationGroupControllerTest {

    @Autowired
    ApplicationGroupController applicationGroupController;

    @Test
    public void testFindById() throws Exception {
        AppGroupModel appGroupModel = applicationGroupController.findById(1L);
        assertNotNull(applicationGroupController);
        assertNotNull(appGroupModel);
        assertEquals("Forventer Knut", appGroupModel.getName(), "Knut");
        assertNotEquals(appGroupModel.getName(), "Yolo");
    }
}