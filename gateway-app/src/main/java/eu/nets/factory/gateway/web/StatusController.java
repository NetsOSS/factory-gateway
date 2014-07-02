package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationRepository;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import eu.nets.factory.gateway.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
@Transactional
public class StatusController {

    @Autowired
    LoadBalancerController loadBalancerController;

    @Autowired
    LoadBalancerRepository loadBalancerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    EmailService emailService;

    public String readCSV(LoadBalancer loadBalancer) {
        String csvString = "";
        int port = loadBalancer.getPublicPort()+1;
        String csvFile = "http://vm-stapp-145:" + port + "/proxy-stats;csv";

        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        try {
            url = new URL(csvFile);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                pw.println(line);
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringWriter.toString();
    }

    public List<StatusModel> parseCSV(String csvString) {
        List<StatusModel> list = new ArrayList<StatusModel>();

        String [] models = csvString.split("\n");
        int startValue = 0;
        for(int i = 0; i < models.length; i++) {
            if(models[i].startsWith("http-in")) {
                startValue = i;
                break;
            }
        }

        for(int i = startValue; i < models.length; i++) {

            StatusModel statMod = new StatusModel();
            String [] col = models[i].split(",");
            statMod.pxname = col[0];
            statMod.svname = col[1];
            statMod.qcur = col[2];
            statMod.qmax = col[3];
            statMod.scur = col[4];
            statMod.smax = col[5];
            statMod.slim = col[6];
            statMod.stot = col[7];
            statMod.bin = col[8];
            statMod.bout = col[9];
            statMod.dreq = col[10];
            statMod.dresp = col[11];
            statMod.ereq = col[12];
            statMod.econ = col[13];
            statMod.eresp = col[14];
            statMod.wretr = col[15];
            statMod.wredis = col[16];
            statMod.status = col[17];
            statMod.weight = col[18];
            statMod.act = col[19];
            statMod.bck = col[20];
            statMod.chkfail = col[21];
            statMod.chkdown = col[22];
            statMod.lastchg = col[23];
            statMod.downtime = col[24];
            statMod.qlimit = col[25];
            statMod.pid = col[26];
            statMod.iid = col[27];
            statMod.sid = col[28];
            statMod.throttle = col[29];
            statMod.lbtot = col[30];
            statMod.tracked = col[31];
            statMod.type = col[32];
            statMod.rate = col[33];
            statMod.rate_lim = col[34];
            statMod.rate_max = col[35];
            statMod.check_status = col[36];
            statMod.check_code = col[37];
            statMod.check_duration = col[38];
            statMod.hrsp_1xx = col[39];
            statMod.hrsp_2xx = col[40];
            statMod.hrsp_3xx = col[41];
            statMod.hrsp_4xx = col[42];
            statMod.hrsp_5xx = col[43];
            statMod.hrsp_other = col[44];
            statMod.hanafail = col[45];
            statMod.req_rate = col[46];
            statMod.req_rate_max = col[47];
            statMod.req_tot = col[48];
            statMod.cli_abrt = col[49];
            statMod.srv_abrt = col[50];
            statMod.comp_in = col[51];
            statMod.comp_out = col[52];
            statMod.comp_byp = col[53];
            statMod.comp_rsp = col[54];
            statMod.lastsess = col[55];
            list.add(statMod);

        }
        return list;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatusModel> getStatusForLoadbalancer(@PathVariable Long id) {
        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        if(loadBalancer == null) {
            return null;
        }
        String csvString = readCSV(loadBalancer);
        return parseCSV(csvString);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/sendEmail/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String sendEmail(@PathVariable Long id) {
        Application app = applicationRepository.findOne(id);

        emailService.sendEmail();
        return "Sending email status of "+app.getName()+" to "+app.getEmails();
    }


}
