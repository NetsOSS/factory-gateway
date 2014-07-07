package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import eu.nets.factory.gateway.web.LoadBalancerController;
import eu.nets.factory.gateway.web.LoadBalancerModel;
import eu.nets.factory.gateway.web.StatusModel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@EnableScheduling
public class StatusService {
    private final Logger log = getLogger(getClass());


    @Autowired
    LoadBalancerRepository loadBalancerRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private HashMap<Long, List<StatusModel>> loadBalancerStatuses = new HashMap<>();


    @Scheduled(fixedRate = 5000)
    public void autoGather() {
        log.info("StatusService.autoGather {}, lbSize: {}", dateFormat.format(new Date()), loadBalancerStatuses.size());
        List<LoadBalancer> lbList = loadBalancerRepository.findAll();

        for (LoadBalancer lb : lbList) {
            List<StatusModel> listStatus = parseCSV(readCSV(lb));

            List<StatusModel> oldlistStatusList = loadBalancerStatuses.get(lb.getId());
            if (oldlistStatusList != null)
                checkForChangesInStatus(oldlistStatusList, listStatus);
            loadBalancerStatuses.put(lb.getId(), listStatus);
            //log.info("StatusService.autoGather demo String {}" , listStatus.toString());

        }

    }

    //Checks if the state has changes for a status model. Not a fast implementation O(n^2). Could be better.
    private void checkForChangesInStatus(List<StatusModel> oldlistStatus, List<StatusModel> newlistStatus) {
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
                log.info("StatusService.checkForChangesInStatus() : {} went from status {} -> {}", oldStatusModel.data.get("svname"), oldStatus, newStatus);
            }


        }

    }

    public List<StatusModel> getStatusForLoadBalancer(Long loadBalancerId) {
        return loadBalancerStatuses.get(loadBalancerId);
    }

    //Should be private. but used in test. fix later
    public List<String> readCSV(LoadBalancer loadBalancer) {
        //log.info("StatusService.readCSV");
        int port = loadBalancer.getPublicPort() + 1;
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
            throw new GatewayException("Cannot connect to HAproxy.");

        }

        String[] sArr = stringWriter.toString().split("\n");
        for (String s : sArr)
            result.add(s);

        return result;
    }

    //Should be private. but used in test. fix later
    public List<StatusModel> parseCSV(List<String> csvStrings) {
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

            list.add(statusModel);
        }

        return list;
    }
}
