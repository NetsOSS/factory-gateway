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
        BufferedReader rd;
        String line;
        List<String> result = new ArrayList<>();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] sArr = stringWriter.toString().split("\n");
        for(String s : sArr)
            result.add(s);

        return result;
    }

    public List<StatusModel> parseCSV(List<String> csvString) {
        log.info("StatusService.parseCSV");

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

}
