package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.junit.Before;
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

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
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
    private LoadBalancerController loadBalancerController;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private ApplicationInstanceController applicationInstanceController;

    @Autowired
    private ApplicationGroupController applicationGroupController;

    @Autowired
    private InitTestClass initTestClass;

    @Test
    public void testGetBackendStatus() {

        LoadBalancer loadBalancerOne = new LoadBalancer("Grandiosa", "127.0.0.1", "/Grandiosa","ssh", 10003);
        LoadBalancer loadBalancerTwo = new LoadBalancer("Grandiosa2", "127.0.0.1", "/Grandiosa2","ssh", 10004);
        LoadBalancerModel loadModelOne = new LoadBalancerModel(loadBalancerOne);
        LoadBalancerModel loadModelTwo = new LoadBalancerModel(loadBalancerTwo);
        loadModelOne = loadBalancerController.create(loadModelOne);
        loadModelTwo = loadBalancerController.create(loadModelTwo);

        ApplicationGroup groupOne = new ApplicationGroup("GroupOne");
        AppGroupModel groupModel = new AppGroupModel(groupOne);
        groupModel = applicationGroupController.create(groupModel);

        Application application = new Application("Finch", "/finch", new ApplicationGroup("Online Banking"), "email");
        AppModel appModel = new AppModel(application);
        appModel.applicationGroupId = groupModel.getId();
        appModel = applicationController.create(appModel);

        ApplicationInstance appInstOne = new ApplicationInstance("Finch1", "127.0.0.1", 7272, "/finch", application);
        ApplicationInstance appInstTwo = new ApplicationInstance("Finch2", "127.0.0.1", 7272, "/finch", application);
        AppInstModel instModelOne = new AppInstModel(appInstOne);
        AppInstModel instModelTwo = new AppInstModel(appInstTwo);

        instModelOne=  applicationInstanceController.create(appModel.getId(), instModelOne);
        instModelTwo = applicationInstanceController.create(appModel.getId(), instModelTwo);

        loadBalancerController.addApplication(loadModelOne.id, appModel.getId());
        loadBalancerController.addApplication(loadModelTwo.id, appModel.getId());


        List<String> csvString = new ArrayList<String>();
        csvString.add("# pxname,svname,qcur,qmax,scur,smax,slim,stot,bin,bout,dreq,dresp,ereq,econ,eresp,wretr,wredis,status,weight,act,bck,chkfail,chkdown,lastchg,downtime,qlimit,pid,iid,sid,throttle,lbtot,tracked,type,rate,rate_lim,rate_max,check_status,check_code,check_duration,hrsp_1xx,hrsp_2xx,hrsp_3xx,hrsp_4xx,hrsp_5xx,hrsp_other,hanafail,req_rate,req_rate_max,req_tot,cli_abrt,srv_abrt,comp_in,comp_out,comp_byp,comp_rsp,lastsess,\n");
        csvString.add("http-i   n,FRONTEND,,,0,6,2000,16,2660,3142,0,0,10,,,,,OPEN,,,,,,,,,1,1,0,,,,0,0,0,6,,,,0,0,0,10,6,0,,0,4,16,,,0,0,0,0,,\n");
        csvString.add("Finch,Finch1,0,0,0,0,32,0,0,0,,0,,0,0,0,0,UP,1,1,0,0,0,11616,0,,1,2,1,,0,,2,0,,0,L4OK,,0,0,0,0,0,0,0,0,,,,0,0,,,,,-1,\n");
        csvString.add("Finch,Finch2,0,0,0,0,32,0,0,0,,0,,0,0,0,0,UP,1,1,0,0,0,11616,0,,1,2,2,,0,,2,0,,0,L4OK,,0,0,0,0,0,0,0,0,,,,0,0,,,,,-1,\n");
        csvString.add("Finch,BACKEND,0,0,0,0,200,0,0,0,0,0,,0,0,0,0,UP,2,2,0,,0,11616,0,,1,2,0,,0,,1,0,,0,,,,0,0,0,0,0,0,,,,,0,0,0,0,0,0,-1,\n");
        csvString.add("stats,FRONTEND,,,1,3,2000,54,13388,130089,0,0,5,,,,,OPEN,,,,,,,,,1,3,0,,,,0,1,0,2,,,,0,48,0,5,7,0,,1,2,61,,,0,0,0,0,,\n");
        csvString.add("stats,BACKEND,0,0,0,1,200,7,13388,130089,0,0,,7,0,0,0,UP,0,0,0,,0,11616,0,,1,3,0,,0,,1,0,,1,,,,0,0,0,0,7,0,,,,,0,0,0,0,0,0,0,\n");

        List<StatusModel> statusModels = statusController.parseCSV(csvString);
        String[] names = "# pxname,svname,qcur,qmax,scur,smax,slim,stot,bin,bout,dreq,dresp,ereq,econ,eresp,wretr,wredis,status,weight,act,bck,chkfail,chkdown,lastchg,downtime,qlimit,pid,iid,sid,throttle,lbtot,tracked,type,rate,rate_lim,rate_max,check_status,check_code,check_duration,hrsp_1xx,hrsp_2xx,hrsp_3xx,hrsp_4xx,hrsp_5xx,hrsp_other,hanafail,req_rate,req_rate_max,req_tot,cli_abrt,srv_abrt,comp_in,comp_out,comp_byp,comp_rsp,lastsess,".split(",");
        String[] testValues = "Finch,BACKEND,0,0,0,0,200,0,0,0,0,0,,0,0,0,0,UP,2,2,0,,0,11616,0,,1,2,0,,0,,1,0,,0,,,,0,0,0,0,0,0,,,,,0,0,0,0,0,0,-1,".split(",");

        List<LoadBalancerModel> loadBalancers = applicationController.getLoadBalancers(appModel.getId());
        for(LoadBalancerModel loadBalancer: loadBalancers) {
            StatusModel backend = statusController.getBackendServer(statusModels, application);
            System.out.println("SNORRE");
            for(int i = 0; i < names.length; i++) {
                //assertThat(backend.data.get(names[i])).isNotNull().isEqualTo(testValues[i]);
               System.out.println("SNORRE " + backend.data.get(names[i]));
            }
        }
    }

    @Test
    public void testParseCSV() throws Exception {

        List<String> csvString = new ArrayList<String>();
        csvString.add("# pxname,svname,qcur,qmax,scur,smax,slim,stot,bin,bout,dreq,dresp,ereq,econ,eresp,wretr,wredis,status,weight,act,bck,chkfail,chkdown,lastchg,downtime,qlimit,pid,iid,sid,throttle,lbtot,tracked,type,rate,rate_lim,rate_max,check_status,check_code,check_duration,hrsp_1xx,hrsp_2xx,hrsp_3xx,hrsp_4xx,hrsp_5xx,hrsp_other,hanafail,req_rate,req_rate_max,req_tot,cli_abrt,srv_abrt,comp_in,comp_out,comp_byp,comp_rsp,lastsess,\n");
        csvString.add("http-i   n,FRONTEND,,,0,6,2000,16,2660,3142,0,0,10,,,,,OPEN,,,,,,,,,1,1,0,,,,0,0,0,6,,,,0,0,0,10,6,0,,0,4,16,,,0,0,0,0,,\n");
        csvString.add("Finch,Finch1,0,0,0,0,32,0,0,0,,0,,0,0,0,0,UP,1,1,0,0,0,11616,0,,1,2,1,,0,,2,0,,0,L4OK,,0,0,0,0,0,0,0,0,,,,0,0,,,,,-1,\n");
        csvString.add("Finch,Finch2,0,0,0,0,32,0,0,0,,0,,0,0,0,0,UP,1,1,0,0,0,11616,0,,1,2,2,,0,,2,0,,0,L4OK,,0,0,0,0,0,0,0,0,,,,0,0,,,,,-1,\n");
        csvString.add("Finch,BACKEND,0,0,0,0,200,0,0,0,0,0,,0,0,0,0,UP,2,2,0,,0,11616,0,,1,2,0,,0,,1,0,,0,,,,0,0,0,0,0,0,,,,,0,0,0,0,0,0,-1,\n");
        csvString.add("stats,FRONTEND,,,1,3,2000,54,13388,130089,0,0,5,,,,,OPEN,,,,,,,,,1,3,0,,,,0,1,0,2,,,,0,48,0,5,7,0,,1,2,61,,,0,0,0,0,,\n");
        csvString.add("stats,BACKEND,0,0,0,1,200,7,13388,130089,0,0,,7,0,0,0,UP,0,0,0,,0,11616,0,,1,3,0,,0,,1,0,,1,,,,0,0,0,0,7,0,,,,,0,0,0,0,0,0,0,\n");

        for(int i = 0; i < csvString.size(); i++) {
            csvString.set(i, csvString.get(i).replaceAll("\n", ""));
        }

        //Get the list of statusModels from controller
        List<StatusModel> parsedCsvString = statusController.parseCSV(csvString);

        //First line in CSV are the column-names
        String[] names = csvString.get(0).split(",");
        names[0] = names[0].replaceAll("# ", "");

        for(int i = 0; i < parsedCsvString.size(); i++) {
            String rebuiltCsvString = "";

            for(int j = 0; j < parsedCsvString.get(i).data.size(); j++) {
                rebuiltCsvString += parsedCsvString.get(i).data.get(names[j]) + ",";
            }

            System.out.println(rebuiltCsvString);
            assertThat(rebuiltCsvString).isNotNull().isEqualTo(csvString.get(i+1));
        }
    }
}