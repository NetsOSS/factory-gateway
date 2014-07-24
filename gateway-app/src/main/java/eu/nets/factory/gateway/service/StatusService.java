package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.*;
import eu.nets.factory.gateway.web.ApplicationController;
import eu.nets.factory.gateway.web.LoadBalancerController;
import eu.nets.factory.gateway.web.LoadBalancerModel;
import eu.nets.factory.gateway.web.StatusModel;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@EnableScheduling
@Transactional
public class StatusService {
    private final Logger log = getLogger(getClass());

    @Autowired
    private ApplicationController applicationController;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    LoadBalancerRepository loadBalancerRepository;

    @Autowired
    EmailService emailService;


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private HashMap<Long, List<StatusModel>> loadBalancerStatuses = new HashMap<>();


    @Scheduled(fixedRate = 5000)
    public void autoPoll() {
        //log.info("StatusService.autoPoll {} , #loadBalancers {}", dateFormat.format(new Date()), loadBalancerStatuses.size());
        List<LoadBalancer> lbList = loadBalancerRepository.findAll();

        for (LoadBalancer lb : lbList) {
            List<StatusModel> oldListStatusList = loadBalancerStatuses.get(lb.getId());
            try {
                List<StatusModel> listStatus = parseCSV(readCSV(lb), lb); //throws exception if failed
                if (oldListStatusList != null && !oldListStatusList.isEmpty())
                    checkForChangesInStatus(oldListStatusList, listStatus, lb);

                loadBalancerStatuses.put(lb.getId(), listStatus);

            } catch (GatewayException ge) {
                //happens when a haproxy is offline
                //if (oldListStatusList.isEmpty()) {
                //Was offline last time also. No need to send emails.

                //} else {
                List<StatusModel> offlineList = new ArrayList<>();
                for (Application application : lb.getApplications()) {


                    StatusModel statusModel = new StatusModel();
                    statusModel.data.put("pxname", application.getName());
                    statusModel.data.put("lbname", lb.getName());
                    statusModel.data.put("svname", "BACKEND");
                    statusModel.data.put("status", "offline");
                    offlineList.add(statusModel);
                    for (ApplicationInstance applicationInstance : application.getApplicationInstances()) {
                        statusModel = new StatusModel();
                        statusModel.data.put("pxname", application.getName());
                        statusModel.data.put("lbname", lb.getName());
                        statusModel.data.put("svname", applicationInstance.getName());
                        statusModel.data.put("status", "offline");
                        offlineList.add(statusModel);
                    }

                }
                loadBalancerStatuses.put(lb.getId(), offlineList);
                //log.info("StatusService.autoPoll {} at {}:{} is offline. Exception : '{}'", lb.getName(), lb.getHost(), lb.getPublicPort(), ge.getMessage());

                //  }
            }
        }

    }

    //Checks if the state has changes for a status model. Not a fast implementation O(n^2). Could be better.
    private void checkForChangesInStatus(List<StatusModel> oldlistStatus, List<StatusModel> newlistStatus, LoadBalancer lb) {
        for (StatusModel oldStatusModel : oldlistStatus) {
            int index = newlistStatus.indexOf(oldStatusModel);
            if (index == -1) {
                log.info("A status model (backend) was removed");
                continue;
            }
            StatusModel newStatusModel = newlistStatus.get(index);
            String oldStatus = oldStatusModel.data.get("status");
            String newStatus = newStatusModel.data.get("status");

            if (!oldStatus.equals(newStatus)) {
                log.info("StatusService.checkForChangesInStatus() : {} went from status {} -> {}", oldStatusModel, oldStatus, newStatus);

                String appName = newStatusModel.data.get("pxname");
                Application application = applicationController.getApplicationByExactName(appName);
                if (application == null) {
                    log.info("Error getting the application, Should never happen?");
                    continue;
                }
                log.info("StatusService.checkForChangesInStatus() : Sending email to :  {}", application.getEmails());
                ApplicationInstance instance = null;
                for (ApplicationInstance applicationInstance : application.getApplicationInstances()) {
                    if (newStatusModel.data.get("svname").equals(applicationInstance.getName()))
                        instance = applicationInstance;
                }
                if (instance == null)
                    continue;


                StringBuilder message = new StringBuilder();
                message.append("Application : " + application.getName() + " in group: " + application.getApplicationGroup().getName() + ".\n");
                message.append("Application instance : " + instance.getName() + " " + instance.getHost() + ":" + instance.getPort() + "" + instance.getPath() + ".\n");
                message.append("\t went from status " + oldStatus + " to " + newStatus + ". \n");

                message.append("In loadbalancer " + lb.getName() + "  " + lb.getHost() + ":" + lb.getStatsPort() + ". \n");
                log.info("Email msg: {}", message.toString());
                emailService.sendEmail(application.getEmails(), "HaProxy change in status", message.toString());


            }


        }

    }


    public List<StatusModel> getStatusForLoadBalancer(Long loadBalancerId) {
        return loadBalancerStatuses.get(loadBalancerId);
    }

    //Should be private. but used in test. fix later
    public List<String> readCSV(LoadBalancer loadBalancer) {
        //log.info("StatusService.readCSV");
        int port = loadBalancer.getStatsPort() + 1;
        String csvFile = "http://vm-stapp-145:" + port + "/proxy-stats;csv";

        URL url;
        HttpURLConnection conn;
        BufferedReader bufferedReader;
        String line;
        List<String> result = new ArrayList<>();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        try {
            url = new URL(csvFile);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = bufferedReader.readLine()) != null) {
                printWriter.println(line);
            }
            bufferedReader.close();
        } catch (Exception e) {
            // e.printStackTrace();
            throw new GatewayException("Cannot connect to HAproxy. " + loadBalancer.getName() + " with csv at : " + csvFile);

        }

        String[] sArr = stringWriter.toString().split("\n");
        for (String s : sArr)
            result.add(s);

        return result;
    }

    //Should be private. but used in test. fix later
    public List<StatusModel> parseCSV(List<String> csvStrings) {
        return parseCSV(csvStrings, null);
    }

    //Should be private. but used in test. fix later
    public List<StatusModel> parseCSV(List<String> csvStrings, LoadBalancer lb) {
        //log.info("StatusService.parseCSV");

        if (csvStrings == null) {
            throw new GatewayException("Received List was null.");
        }
        if (csvStrings.size() < 2) {
            throw new GatewayException("Expected list to be of size 2 or greater.");
        }
        if (!(csvStrings.get(0).startsWith("# ")) && (csvStrings.get(1).startsWith("http-in"))) {
            throw new GatewayException("Unrecognized format in CSV file. Expected first line to start with '# ', and second line to start with 'http-in'");
        }

        List<StatusModel> list = new ArrayList<>();

        String[] names = csvStrings.get(0).split(",");
        names[0] = names[0].replaceAll("# ", "");

        for (int i = 1; i < csvStrings.size(); i++) {
            StatusModel statusModel = new StatusModel();
            String csvLine = csvStrings.get(i).substring(0, csvStrings.get(i).length() - 1);
            String[] splitCsvString = csvLine.split(",", -1);

            for (int j = 0; j < splitCsvString.length; j++) {
                if (j < names.length) // should not be necessary
                    statusModel.data.put(names[j], splitCsvString[j]);
            }
            if (lb != null)
                statusModel.data.put("lbname", lb.getName());
            list.add(statusModel);
        }

        return list;
    }
}
