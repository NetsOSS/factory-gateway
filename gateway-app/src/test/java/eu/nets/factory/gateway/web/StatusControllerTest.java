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
    public void testParseCSV() throws Exception{

        String csvString = "# pxname,svname,qcur,qmax,scur,smax,slim,stot,bin,bout,dreq,dresp,ereq,econ,eresp,wretr,wredis,status,weight,act,bck,chkfail,chkdown,lastchg,downtime,qlimit,pid,iid,sid,throttle,lbtot,tracked,type,rate,rate_lim,rate_max,check_status,check_code,check_duration,hrsp_1xx,hrsp_2xx,hrsp_3xx,hrsp_4xx,hrsp_5xx,hrsp_other,hanafail,req_rate,req_rate_max,req_tot,cli_abrt,srv_abrt,comp_in,comp_out,comp_byp,comp_rsp,lastsess,\n" +
                "http-in,FRONTEND,,,4,6,2000,633,435989,10271083,0,0,0,,,,,OPEN,,,,,,,,,1,1,0,,,,0,0,0,49,,,,0,580,127,18,143,0,,0,70,868,,,0,0,0,0,,\n" +
                "Kamino,Kamino1,0,0,0,0,32,0,0,0,,0,,0,0,0,0,UP,1,1,0,0,0,7076,0,,1,2,1,,0,,2,0,,0,L4OK,,0,0,0,0,0,0,0,0,,,,0,0,,,,,-1,\n" +
                "Kamino,Kamino2,0,0,0,0,32,0,0,0,,0,,0,0,0,0,MAINT,1,1,0,0,1,40,40,,1,2,2,,0,,2,0,,0,L4OK,,0,0,0,0,0,0,0,0,,,,0,0,,,,,-1,\n" +
                "Kamino,BACKEND,0,0,0,0,200,0,0,0,0,0,,0,0,0,0,UP,1,1,0,,0,7076,0,,1,2,0,,0,,1,0,,0,,,,0,0,0,0,0,0,,,,,0,0,0,0,0,0,-1,\n" +
                "Finch,Finch2,0,0,0,1,32,52,27089,557647,,0,,0,0,0,0,MAINT,1,1,0,0,1,44,44,,1,3,1,,51,,2,0,,8,L4OK,,0,0,40,10,2,0,0,0,,,,11,0,,,,,55,\n" +
                "Finch,Finch6,0,0,0,2,32,171,88947,2278932,,0,,0,0,0,0,UP,1,1,0,0,0,7076,0,,1,3,2,,170,,2,0,,19,L4OK,,0,0,145,24,2,0,0,0,,,,29,0,,,,,27,\n" +
                "Finch,Finch3,0,0,0,2,32,172,89414,2291404,,0,,0,0,0,0,UP,1,1,0,0,0,7076,0,,1,3,3,,170,,2,0,,19,L4OK,,0,0,139,29,4,0,0,0,,,,40,0,,,,,27,\n" +
                "Finch,Finch5,0,0,0,1,32,53,27580,761580,,0,,0,0,0,0,MAINT,1,1,0,0,1,44,44,,1,3,4,,51,,2,0,,8,L4OK,,0,0,42,8,3,0,0,0,,,,15,0,,,,,54,\n" +
                "Finch,Finch1,0,0,0,2,32,174,90856,2359426,,0,,0,0,0,0,UP,1,1,0,0,0,7076,0,,1,3,5,,169,,2,0,,19,L4OK,,0,0,134,35,5,0,0,0,,,,38,0,,,,,27,\n" +
                "Finch,Finch4,0,0,0,1,32,51,26563,780304,,0,,0,0,0,0,MAINT,1,1,0,0,1,44,44,,1,3,6,,51,,2,0,,8,L4OK,,0,0,40,11,0,0,0,0,,,,13,0,,,,,54,\n" +
                "Finch,Finch7,0,0,0,1,32,52,27053,1211474,,0,,0,0,0,0,MAINT,1,1,0,0,1,44,44,,1,3,7,,51,,2,0,,8,L4OK,,0,0,40,10,2,0,0,0,,,,12,0,,,,,54,\n" +
                "Finch,BACKEND,0,0,0,5,200,725,377502,10240767,0,0,,0,0,0,0,UP,3,3,0,,0,7076,0,,1,3,0,,713,,1,0,,57,,,,0,580,127,18,0,0,,,,,158,0,0,0,0,0,27,\n" +
                "Sekot,Sekot2,0,0,0,0,32,0,0,0,,0,,0,0,0,0,UP,1,1,0,0,0,7076,0,,1,4,1,,0,,2,0,,0,L4OK,,0,0,0,0,0,0,0,0,,,,0,0,,,,,-1,\n" +
                "Sekot,Sekot1,0,0,0,0,32,0,0,0,,0,,0,0,0,0,UP,1,1,0,0,0,7076,0,,1,4,2,,0,,2,0,,0,L4OK,,0,0,0,0,0,0,0,0,,,,0,0,,,,,-1,\n" +
                "Sekot,BACKEND,0,0,0,0,200,0,0,0,0,0,,0,0,0,0,UP,2,2,0,,0,7076,0,,1,4,0,,0,,1,0,,0,,,,0,0,0,0,0,0,,,,,0,0,0,0,0,0,-1,\n" +
                "stats,FRONTEND,,,2,3,2000,39,15177,349183,0,0,5,,,,,OPEN,,,,,,,,,1,5,0,,,,0,1,0,3,,,,0,30,2,5,11,0,,1,4,49,,,0,0,0,0,,\n" +
                "stats,BACKEND,0,0,0,1,200,11,15177,349183,0,0,,11,0,0,0,UP,0,0,0,,0,7076,0,,1,5,0,,0,,1,0,,2,,,,0,0,0,0,11,0,,,,,0,0,0,0,0,0,0,";

        String[] models = csvString.split("\n");
        for(int i = 0; i < models.length; i++) {
            System.out.println(models[i]);
        }
        List<StatusModel> list = statusController.parseCSV(csvString);



        int startValue = 0;
        for(int i = 0; i < models.length; i++) {
            if(models[i].startsWith("http-in")) {
                startValue = i;
                break;
            }
        }

        for(int i = startValue; i < models.length; i++) {
            String [] fields = models[i].split(",");

            assertEquals(fields[0], list.get(i).pxname);
            assertEquals(fields[1], list.get(i).svname);
            assertEquals(fields[2], list.get(i).qcur);
            assertEquals(fields[3], list.get(i).qmax);
            assertEquals(fields[4], list.get(i).scur);
            assertEquals(fields[5], list.get(i).smax);
            assertEquals(fields[6], list.get(i).slim);
            assertEquals(fields[7], list.get(i).stot);
            assertEquals(fields[8], list.get(i).bin);
            assertEquals(fields[9], list.get(i).bout);
            assertEquals(fields[10], list.get(i).dreq);
            assertEquals(fields[11], list.get(i).dresp);
            assertEquals(fields[12], list.get(i).ereq);
            assertEquals(fields[13], list.get(i).econ);
            assertEquals(fields[14], list.get(i).eresp);
            assertEquals(fields[15], list.get(i).wretr);
            assertEquals(fields[16], list.get(i).wredis);
            assertEquals(fields[17], list.get(i).status);
            assertEquals(fields[18], list.get(i).weight);
            assertEquals(fields[19], list.get(i).act);
            assertEquals(fields[20], list.get(i).bck);
            assertEquals(fields[21], list.get(i).chkfail);
            assertEquals(fields[22], list.get(i).chkdown);
            assertEquals(fields[23], list.get(i).lastchg);
            assertEquals(fields[24], list.get(i).downtime);
            assertEquals(fields[25], list.get(i).qlimit);
            assertEquals(fields[26], list.get(i).pid);
            assertEquals(fields[27], list.get(i).iid);
            assertEquals(fields[28], list.get(i).sid);
            assertEquals(fields[29], list.get(i).throttle);
            assertEquals(fields[30], list.get(i).lbtot);
            assertEquals(fields[31], list.get(i).tracked);
            assertEquals(fields[32], list.get(i).type);
            assertEquals(fields[33], list.get(i).rate);
            assertEquals(fields[34], list.get(i).rate_lim);
            assertEquals(fields[35], list.get(i).rate_max);
            assertEquals(fields[36], list.get(i).check_status);
            assertEquals(fields[37], list.get(i).check_code);
            assertEquals(fields[38], list.get(i).check_duration);
            assertEquals(fields[39], list.get(i).hrsp_1xx);
            assertEquals(fields[40], list.get(i).hrsp_2xx);
            assertEquals(fields[41], list.get(i).hrsp_3xx);
            assertEquals(fields[42], list.get(i).hrsp_4xx);
            assertEquals(fields[43], list.get(i).hrsp_5xx);
            assertEquals(fields[44], list.get(i).hrsp_other);
            assertEquals(fields[45], list.get(i).hanafail);
            assertEquals(fields[46], list.get(i).req_rate);
            assertEquals(fields[47], list.get(i).req_rate_max);
            assertEquals(fields[48], list.get(i).req_tot);
            assertEquals(fields[49], list.get(i).cli_abrt);
            assertEquals(fields[50], list.get(i).srv_abrt);
            assertEquals(fields[51], list.get(i).comp_in);
            assertEquals(fields[52], list.get(i).comp_out);
            assertEquals(fields[53], list.get(i).comp_byp);
            assertEquals(fields[54], list.get(i).comp_rsp);
            assertEquals(fields[55], list.get(i).lastsess);
        }
    }


    @Test
    public void testReadAndParseCSV() throws Exception {

       LoadBalancer loadBalancer = new LoadBalancer("Grandiosa", "127.0.0.1", "/instPath", "sshKey", 10003);
       LoadBalancerModel loadBalancerModel = loadBalancerController.create(new LoadBalancerModel(loadBalancer));
       String csvString = statusController.readCSV(loadBalancer);

        List<StatusModel> list = statusController.parseCSV(csvString);
        String[] models = csvString.split("\n");
        int startValue = 0;
        for(int i = 0; i < models.length; i++) {
            if(models[i].startsWith("http-in")) {
                startValue = i;
                break;
            }
        }

        for(int i = startValue; i < models.length; i++) {
            String [] fields = models[i].split(",");
            assertEquals(fields[0], list.get(i).pxname);
            assertEquals(fields[1], list.get(i).svname);
            assertEquals(fields[2], list.get(i).qcur);
            assertEquals(fields[3], list.get(i).qmax);
            assertEquals(fields[4], list.get(i).scur);
            assertEquals(fields[5], list.get(i).smax);
            assertEquals(fields[6], list.get(i).slim);
            assertEquals(fields[7], list.get(i).stot);
            assertEquals(fields[8], list.get(i).bin);
            assertEquals(fields[9], list.get(i).bout);
            assertEquals(fields[10], list.get(i).dreq);
            assertEquals(fields[11], list.get(i).dresp);
            assertEquals(fields[12], list.get(i).ereq);
            assertEquals(fields[13], list.get(i).econ);
            assertEquals(fields[14], list.get(i).eresp);
            assertEquals(fields[15], list.get(i).wretr);
            assertEquals(fields[16], list.get(i).wredis);
            assertEquals(fields[17], list.get(i).status);
            assertEquals(fields[18], list.get(i).weight);
            assertEquals(fields[19], list.get(i).act);
            assertEquals(fields[20], list.get(i).bck);
            assertEquals(fields[21], list.get(i).chkfail);
            assertEquals(fields[22], list.get(i).chkdown);
            assertEquals(fields[23], list.get(i).lastchg);
            assertEquals(fields[24], list.get(i).downtime);
            assertEquals(fields[25], list.get(i).qlimit);
            assertEquals(fields[26], list.get(i).pid);
            assertEquals(fields[27], list.get(i).iid);
            assertEquals(fields[28], list.get(i).sid);
            assertEquals(fields[29], list.get(i).throttle);
            assertEquals(fields[30], list.get(i).lbtot);
            assertEquals(fields[31], list.get(i).tracked);
            assertEquals(fields[32], list.get(i).type);
            assertEquals(fields[33], list.get(i).rate);
            assertEquals(fields[34], list.get(i).rate_lim);
            assertEquals(fields[35], list.get(i).rate_max);
            assertEquals(fields[36], list.get(i).check_status);
            assertEquals(fields[37], list.get(i).check_code);
            assertEquals(fields[38], list.get(i).check_duration);
            assertEquals(fields[39], list.get(i).hrsp_1xx);
            assertEquals(fields[40], list.get(i).hrsp_2xx);
            assertEquals(fields[41], list.get(i).hrsp_3xx);
            assertEquals(fields[42], list.get(i).hrsp_4xx);
            assertEquals(fields[43], list.get(i).hrsp_5xx);
            assertEquals(fields[44], list.get(i).hrsp_other);
            assertEquals(fields[45], list.get(i).hanafail);
            assertEquals(fields[46], list.get(i).req_rate);
            assertEquals(fields[47], list.get(i).req_rate_max);
            assertEquals(fields[48], list.get(i).req_tot);
            assertEquals(fields[49], list.get(i).cli_abrt);
            assertEquals(fields[50], list.get(i).srv_abrt);
            assertEquals(fields[51], list.get(i).comp_in);
            assertEquals(fields[52], list.get(i).comp_out);
            assertEquals(fields[53], list.get(i).comp_byp);
            assertEquals(fields[54], list.get(i).comp_rsp);
            assertEquals(fields[55], list.get(i).lastsess);

        }
    }
}