package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigGeneratorService {

//    private StringWriter stringWriter = new StringWriter();
//    private PrintWriter pw = new PrintWriter(stringWriter);

    private final static String TAB = "    ";
    private final static String TAB2 = TAB + TAB;

    public String generateConfig() {
        return null;
    }

    public String generateConfig(LoadBalancer loadBalancer) {

        List<String> rules = new ArrayList<>();
        List<String> backends = new ArrayList<>();

        // Populate variables
        for (Application application : loadBalancer.getApplications()) {

            // acl + use_backend
            rules.add(  "acl " + application.getName() + "rule path -m beg /" + application.getPublicUrl() +
                        "\nuse_backend " + application.getName() + " if " + application.getName() + "rule\n");

            // backend
            StringBuilder b = new StringBuilder().append("\nbackend " + application.getName() + "\n");
            b.append("reqrep ^([^\\ :]*)\\ /ofbj/(.*)     \\1\\ /\\2\n");

            // server
            for (ApplicationInstance applicationInstance : application.getApplicationInstances()) {
                b.append("server " + applicationInstance.getName() + " " + applicationInstance.getHost() + ":" + applicationInstance.getPort() + " maxconn 32\n");
            }
            backends.add(b.toString());
        }

        String strConfig = buildString(rules, backends);
        return strConfig;
    }

    private String buildString(List<String> rules, List<String> backends) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);


        writeDefaultsStart(pw);

        // Write content
        for (String rule : rules) {
            pw.println(TAB2 + rule);
        }
        for(String backend : backends) {
            pw.println(TAB2 + backend);
        }

        writeDefaultsEnd(pw);


        String strConfig = stringWriter.toString();
        stringWriter.flush();
        pw.flush();
        return strConfig;
    }

    private void writeDefaultsStart(PrintWriter pw) {
        pw.println(TAB + "global");
        pw.println(TAB2 + "daemon");
        pw.println(TAB2 + "maxconn 256");
        pw.println();
        pw.println(TAB + "defaults");
        pw.println(TAB2 + "mode http");
        pw.println(TAB2 + "timeout connect 5000ms");
        pw.println(TAB2 + "timeout client 50000ms");
        pw.println(TAB2 + "timeout server 50000ms");
        pw.println();
        pw.println(TAB + "frontend http-in3");
        pw.println(TAB2 + "bind *:30111");
    }

    private void writeDefaultsEnd(PrintWriter pw) {
        pw.println();
        pw.println(TAB + "listen stats *:33334");
        pw.println(TAB2 + "mode http");
        pw.println(TAB2 + "stats enable");
        pw.println(TAB2 + "stats uri /proxy-stats");
        pw.println(TAB2 + "stats admin if TRUE");
    }
}
