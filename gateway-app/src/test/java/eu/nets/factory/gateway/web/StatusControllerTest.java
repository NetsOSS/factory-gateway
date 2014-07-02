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

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
    public void testReadAndParseCSV() throws Exception {

        //new LoadBalancer
        LoadBalancer loadBalancer = new LoadBalancer("Grandiosa", "127.0.0.1", "/instPath", "sshKey", 10003);

        //get csvString (List) from statusController
        List<String> csvString = statusController.readCSV(loadBalancer);

        //First line in CSV is the column-names
        String[] names = csvString.get(0).split(",");
        //names[0].replaceAll("# ", "");

        //Get the list of statusmodel from controller
        List<StatusModel> list = statusController.parseCSV(csvString);

        //Create new statusmodels to test with
        List<StatusModel> testModels = new ArrayList<StatusModel>();
        boolean start = false;

        for(int i = 0; i < csvString.size(); i++) {
            if(csvString.get(i).startsWith("http-in")) {
                start = true;
            }
            if(start) {
                StatusModel model = new StatusModel();
                for (int j = 0; j < names.length; j++) {
                    model.data.put(names[j], csvString.get(i).split(",")[j]);
                }
                testModels.add(model);
            }
        }

        assertEquals(testModels.size(), list.size());

        for(int i = 0; i < list.size(); i++) {
            for (int j = 0; j < names.length; j++) {
                assertEquals(list.get(i).data.get(names[j]), testModels.get(i).data.get(names[j]));
                System.out.println("List: " + names[j] + ", " + list.get(i).data.get(names[j]) + " Test: " + names[j] + ", " + testModels.get(i).data.get(names[j]));
            }

        }
    }
}