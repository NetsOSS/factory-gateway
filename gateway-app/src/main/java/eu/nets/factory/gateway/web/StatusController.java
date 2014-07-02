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

    public List<String> readCSV(LoadBalancer loadBalancer) {

        int port = loadBalancer.getPublicPort()+1;
        String csvFile = "http://vm-stapp-145:" + port + "/proxy-stats;csv";

        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        List<String> result = new ArrayList<String>();
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        int count = 0;

        try {
            url = new URL(csvFile);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = rd.readLine()) != null) {
                //result.add(line);
                pw.println(line);
                count++;
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < count; i++) {
            String tmp = stringWriter.toString().split("\n")[i];
            //tmp = tmp.replaceAll("\\r\\n|\\r|\\n", "");
            result.add(tmp);

        }
        return result;
    }

    public List<StatusModel> parseCSV(List<String> csvString) {

        List<StatusModel> list = new ArrayList<StatusModel>();

        String[] names = csvString.get(0).split(",");
        names[0] = names[0].replaceAll("# ", "");
        boolean start = false;


        for(int i = 0; i < csvString.size(); i++) {
            if(csvString.get(i).startsWith("http-in")) {
                start = true;
            }
            if(start) {
                StatusModel statusModel = new StatusModel();
                for (int j = 0; j < names.length; j++) {
                    statusModel.data.put(names[j], csvString.get(i).split(",")[j]);

                }
                list.add(statusModel);
            }
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
