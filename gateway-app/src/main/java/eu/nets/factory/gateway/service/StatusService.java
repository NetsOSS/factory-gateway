package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.ApplicationRepository;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import eu.nets.factory.gateway.web.ApplicationController;
import eu.nets.factory.gateway.web.ApplicationGroupController;
import eu.nets.factory.gateway.web.StatusModel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    ApplicationGroupController applicationGroupController;

    @Autowired
    LoadBalancerRepository loadBalancerRepository;

    @Autowired
    EmailService emailService;


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private Map<Long, Status> loadBalancerStatuses = Collections.emptyMap();

    @Scheduled(fixedRate = 1000)
    public void autoPoll() {

        Map<Long, Status> loadBalancerStatuses = new HashMap<>();
        for (LoadBalancer lb : loadBalancerRepository.findAll()) {

            Status status = storeAsBetterObject2(lb);

            checkForChangesInStatus2(getStatusForLoadBalancer(lb.getId()), status, lb);
            loadBalancerStatuses.put(lb.getId(), status);
        }
        this.loadBalancerStatuses = loadBalancerStatuses;

    }

    public Status storeAsBetterObject2(LoadBalancer lb) {

//        Set<Long> applicationGroupIds = lb.getApplications().stream().
//                map(a -> a.getApplicationGroup().getId()).
//                collect(Collectors.toSet());
//
//        Map<Long, FrontendStatus> frontends = applicationGroupIds.stream().
//                map(FrontendStatus::new).
//                collect(Collectors.toMap(fs -> fs.groupId, Function.identity()));

        Map<Long, Map<String, String>> statusMap;
        boolean isHaproxyUp = true;
        try {
            List<String> csvStrings = readCSV(lb);
            statusMap = parseCSV2(csvStrings, lb);

        } catch (Exception e) {
            statusMap = Collections.emptyMap();
            isHaproxyUp = false;
        }

        Map<Long, FrontendStatus> frontends = new HashMap<>();
        for (Application application : lb.getApplications()) {

            Long applicationGroupId = application.getApplicationGroup().getId();
            FrontendStatus frontend = frontends.get(applicationGroupId);
            if (frontend == null) {
                Map<String, String> data = statusMap.get(applicationGroupId);
                frontend = new FrontendStatus(data, application.getApplicationGroup());
                frontends.put(applicationGroupId, frontend);
            }

            Map<String, String> statusModel = statusMap.get(application.getId());
            BackendStatus backendStatus = new BackendStatus(statusModel, application, lb.getHost());

            for (ApplicationInstance applicationInstance : application.getApplicationInstances()) {
                Map<String, String> data = statusMap.get(applicationInstance.getId());
                ServerStatus serverStatus = new ServerStatus(data, applicationInstance);
                backendStatus.servers.add(serverStatus);
            }
            frontend.backends.add(backendStatus);


        }
        return new Status(frontends, isHaproxyUp);
    }

    public class Status {
        public Map<Long, FrontendStatus> frontends;
        public final LocalDateTime timeStamp = LocalDateTime.now();
        public final boolean up;

        public Status(Map<Long, FrontendStatus> frontends, boolean up) {
            this.frontends = frontends != null ? frontends : Collections.emptyMap();
            this.up = up;
        }
    }

    public class FrontendStatus {
        public Long groupId;
        public String name;
        public Map<String, String> data;
        //id = data.iid , groupName = pxname
        public List<BackendStatus> backends = new ArrayList<>();

        public FrontendStatus(Map<String, String> data, ApplicationGroup applicationGroup) {
            this.data = data != null ? data : Collections.emptyMap();
            this.name = applicationGroup.getName();
            this.groupId = applicationGroup.getId();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FrontendStatus))
                return false;
            FrontendStatus f = (FrontendStatus) obj;
            return groupId.equals(f.groupId);
        }
    }

    public class BackendStatus {
        public Long appId;
        public String name;
        public String link;
        public Map<String, String> data;
        public List<ServerStatus> servers = new ArrayList<>();

        public BackendStatus(Map<String, String> data, Application application, String host) {
            this.data = data != null ? data : Collections.emptyMap();
            this.appId = application.getId();
            this.name = application.getName();
            link = host + ":" + application.getApplicationGroup().getPort() + "" + application.getPublicUrl();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BackendStatus))
                return false;
            BackendStatus f = (BackendStatus) obj;
            return appId.equals(f.appId);
        }
    }

    public class ServerStatus {
        public Map<String, String> data;
        public Long appInstId;
        public String name;

        public ServerStatus(Map<String, String> data, ApplicationInstance applicationInstance) {
            this.data = data != null ? data : Collections.emptyMap();
            this.name = applicationInstance.getName();
            this.appInstId = applicationInstance.getId();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ServerStatus))
                return false;
            ServerStatus f = (ServerStatus) obj;
            return appInstId.equals(f.appInstId);
        }
    }

    private void checkForChangesInStatus2(Status oldStatus, Status newStatus, LoadBalancer lb) {
        if (oldStatus == null || newStatus == null)
            return;

        for (Long frontendKey : newStatus.frontends.keySet()) {
            FrontendStatus frontendNew = newStatus.frontends.get(frontendKey);
            FrontendStatus frontendOld = oldStatus.frontends.get(frontendKey);
            if (frontendOld == null)
                continue;
            for (BackendStatus backendOld : frontendOld.backends) {
                int index = frontendNew.backends.indexOf(backendOld);
                if (index == -1)
                    continue;
                BackendStatus backendNew = frontendNew.backends.get(index);
                detectChangesInBackend(backendOld, backendNew, lb);

            }
        }

    }

    private void detectChangesInBackend(BackendStatus oldBackendStatus, BackendStatus newBackendStatus, LoadBalancer lb) {
        for (ServerStatus serverOld : oldBackendStatus.servers) {
            int index = newBackendStatus.servers.indexOf(serverOld);
            if (index == -1)
                continue;
            ServerStatus serverNew = newBackendStatus.servers.get(index);
            if (serverNew.data.isEmpty() || serverOld.data.isEmpty())
                continue;

            String oldStatus = serverOld.data.get("status");
            String newStatus = serverNew.data.get("status");
            Application application = applicationRepository.findOne(newBackendStatus.appId);


            if (!oldStatus.equals(newStatus)) {
                log.info("StatusService.checkForChangesInStatus() : {} went from status {} -> {}", serverNew.name, oldStatus, newStatus);

                if (application == null) {
                    log.info("Error getting the application, Should never happen?");
                    continue;
                }
                log.info("StatusService.checkForChangesInStatus() : Sending email to :  {}", application.getEmails());
                ApplicationInstance instance = null;

                for (ApplicationInstance applicationInstance : application.getApplicationInstances()) {
                    if (serverNew.appInstId.equals(applicationInstance.getId()))
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


    public Status getStatusForLoadBalancer(Long loadBalancerId) {
        return loadBalancerStatuses.get(loadBalancerId);
    }

    public Map<Long, BackendStatus> getStatusForApplication(Application application) {
        Map<Long, BackendStatus> map = new HashMap<>();

        List<LoadBalancer> loadBalancers = application.getLoadBalancers();
        for (LoadBalancer lb : loadBalancers) {
            Status status = getStatusForLoadBalancer(lb.getId());
            FrontendStatus frontendStatus = status.frontends.get(application.getApplicationGroup().getId());
            BackendStatus backendStatus = new BackendStatus(null, application, lb.getHost());
            int index = frontendStatus.backends.indexOf(backendStatus);
            if (index == -1) {
                continue;
            }
            map.put(lb.getId(), frontendStatus.backends.get(index));
        }
        return map;
    }

    //Should be private. but used in test. fix later
    public List<String> readCSV(LoadBalancer loadBalancer) {
        String csvFile = "http://" + loadBalancer.getHost() + ":" + loadBalancer.getStatsPort() + "/proxy-stats;csv";

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
        return Arrays.asList(stringWriter.toString().split("\n"));
    }


    public Map<Long, Map<String, String>> parseCSV2(List<String> csvStrings, LoadBalancer lb) {
        if (csvStrings == null) {
            throw new GatewayException("Received List was null.");
        }
        if (csvStrings.size() < 2) {
            throw new GatewayException("Expected list to be of size 2 or greater.");
        }
        if (!(csvStrings.get(0).startsWith("# ")) && (csvStrings.get(1).startsWith("http-in"))) {
            throw new GatewayException("Unrecognized format in CSV file. Expected first line to start with '# ', and second line to start with 'http-in'");
        }

        //List<StatusModel> list = new ArrayList<>();
        Map<Long, Map<String, String>> map = new HashMap<>();

        String[] names = csvStrings.get(0).split(",");
        names[0] = names[0].replaceAll("# ", "");

        for (int i = 1; i < csvStrings.size(); i++) {
            Map<String, String> data = new HashMap<>();
            String csvLine = csvStrings.get(i).substring(0, csvStrings.get(i).length() - 1);
            String[] splitCsvString = csvLine.split(",", -1);

            for (int j = 0; j < splitCsvString.length; j++) {
                if (j < names.length) // should not be necessary
                    data.put(names[j], splitCsvString[j]);
            }
            if (lb != null)
                data.put("lbname", lb.getName());

            /*
            IDs are stored as "NAME_ID"
            pxname = Id of appGroup or id of application.
            svname = FRONTEND, BACKEND, or appInst id
             */

            String svname = data.get("svname");
            String pxname = data.get("pxname");

            if (svname.equals("FRONTEND") || svname.equals("BACKEND")) {
                if (data.get("pxname").equals("stats")) {
                    //Stats are not showed, not sure if we want this in our model.
                } else {
                    try {
                        pxname = pxname.substring(pxname.lastIndexOf("_") + 1);
                        Long id = Long.parseLong(pxname);
                        map.put(id, data);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    svname = svname.substring(svname.lastIndexOf("_") + 1);
                    Long id = Long.parseLong(svname);
                    map.put(id, data);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return map;
    }


    //Should be private. but used in test. fix later
    public List<StatusModel> parseCSV(List<String> csvStrings) {
        return parseCSV(csvStrings, null);
    }

    /*
    Method is not used. Except tests.
     */
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
