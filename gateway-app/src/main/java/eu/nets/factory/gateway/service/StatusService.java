package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.web.StatusModel;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by kwlar on 03.07.2014.
 */
@Service
public class StatusService {
    private final Logger log = getLogger(getClass());

    public List<String> readCSV(LoadBalancer loadBalancer) {
        log.info("StatusService.readCSV");

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
            e.printStackTrace();
        }

        String[] sArr = stringWriter.toString().split("\n");
        for(String s : sArr)
            result.add(s);

        return result;
    }

    public List<StatusModel> parseCSV(List<String> csvStrings) {
        log.info("StatusService.parseCSV");

        if(csvStrings == null) {
            throw new GatewayException("Received List was null.");
        }
        if(csvStrings.size() < 2) {
            throw new GatewayException("Expected list to be of size 2 or greater.");
        }
        if(!(csvStrings.get(0).startsWith("# ")) && (csvStrings.get(1).startsWith("http-in"))) {
            throw new GatewayException("Unrecognized format in CSV file. Expected first line to start with '# ', and second line to start with 'http-in'");
        }

        List<StatusModel> list = new ArrayList<>();

        String[] names = csvStrings.get(0).split(",");
        names[0] = names[0].replaceAll("# ", "");

        for(int i = 1; i < csvStrings.size(); i++) {
            StatusModel statusModel = new StatusModel();
            String csvLine = csvStrings.get(i).substring(0, csvStrings.get(i).length()-1);
            String[] splitCsvString = csvLine.split(",", -1);

            for (int j = 0; j < splitCsvString.length; j++) {
                if(j < names.length) // should not be necessary
                    statusModel.data.put(names[j], splitCsvString[j]);
            }

            list.add(statusModel);
        }

        return list;
    }
}
