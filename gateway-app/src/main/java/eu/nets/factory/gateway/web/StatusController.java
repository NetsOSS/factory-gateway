package eu.nets.factory.gateway.web;

import edu.umd.cs.findbugs.ba.bcp.Load;
import eu.nets.factory.gateway.GatewayException;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/server-status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<Long, List<StatusModel>> getServerStatusForApplication(@PathVariable Long id) {

        HashMap<Long, List<StatusModel>> hashMap = new HashMap<Long, List<StatusModel>>();
        Application application = applicationRepository.findOne(id);
        if(application == null) {
            return null;
        }

        List<LoadBalancer> loadBalancers = application.getLoadBalancers();
        for(LoadBalancer loadBalancer: loadBalancers) {
            List<StatusModel> statusModelsFromCSV = parseCSV(readCSV(loadBalancer));
            List<StatusModel> models = new ArrayList<StatusModel>();
            for(StatusModel statusModel: statusModelsFromCSV) {
                if(statusModel.data.get("pxname").equals(application.getName()) && !statusModel.data.get("svname").equals("BACKEND")) {
                    models.add(statusModel);
                }
            }
            hashMap.put(loadBalancer.getId(), models);
        }

        return hashMap;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/backend-status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<Long, StatusModel> getBackendStatusForApplication(@PathVariable Long id) {

        HashMap<Long, StatusModel> hashMap = new HashMap<Long, StatusModel>();
        Application application = applicationRepository.findOne(id);

        if(application == null) {
            return null;
        }

        List<LoadBalancer> loadBalancers = application.getLoadBalancers();
        for(LoadBalancer loadBalancer: loadBalancers) {
            List<StatusModel> models = parseCSV(readCSV(loadBalancer));
            for(StatusModel model: models) {
                if(model.data.get("pxname").equals(application.getName()) && model.data.get("svname").equals("BACKEND")) {
                    hashMap.put(loadBalancer.getId(), model);
                }
            }
        }

        return hashMap;
    }

    public List<String> readCSV(LoadBalancer loadBalancer) {

        int port = loadBalancer.getPublicPort()+1;
        String csvFile = "http://vm-stapp-145:" + port + "/proxy-stats;csv";

        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        List<String> result = new ArrayList<>();
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        int count = 0;

        try {
            url = new URL(csvFile);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = rd.readLine()) != null) {
                pw.println(line);
                count++;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] sArr = stringWriter.toString().split("\n");
        for(String s : sArr)
            result.add(s);

        return result;
    }

    public List<StatusModel> parseCSV(List<String> csvString) {
        if(!(csvString.get(0).startsWith("# ")) && (csvString.get(1).startsWith("http-in"))) {
            throw new GatewayException("Unrecognized format in CSV file. Expected first line to start with '# ', and second line to start with 'http-in'");
        }

        List<StatusModel> list = new ArrayList<>();

        String[] names = csvString.get(0).split(",");
        names[0] = names[0].replaceAll("# ", "");

        for(int i = 1; i < csvString.size(); i++) {
            StatusModel statusModel = new StatusModel();
            String csvLine = csvString.get(i).substring(0, csvString.get(i).length()-1);
            String[] splitCsvString = csvLine.split(",", -1);

            for (int j = 0; j < splitCsvString.length; j++) {
                if((j < names.length) && (j < splitCsvString.length))
                 statusModel.data.put(names[j], splitCsvString[j]);
            }
            list.add(statusModel);
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
        List<String> csvString = readCSV(loadBalancer);

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
