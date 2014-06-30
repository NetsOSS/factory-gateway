package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
@Transactional
public class StatusController {

    @Autowired
    LoadBalancerController loadBalancerController;

    @Autowired
    LoadBalancerRepository loadBalancerRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/getCSV", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String readCSV(LoadBalancerModel loadBalancerModel) {

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(loadBalancerModel.id);
        if(loadBalancer == null) {
            return null;
        }
        loadBalancerModel = new LoadBalancerModel(loadBalancer);
        String csvString = "";
        int port = loadBalancer.getPublicPort()+1;
        String csvFile = "http://vm-stapp-145:" + port + "/proxy-stats;csv";

        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(csvFile);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<StatusModel> parseCSV(String csvString) {
        return null;
    }

    public static class StatusModel {

        public String application;
        public String applicationInstance;
        public int currentQueue;
        public int maxQueue;
        //more to come
    }
}
