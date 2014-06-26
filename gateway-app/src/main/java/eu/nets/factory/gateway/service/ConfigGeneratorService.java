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
        List<String> use_backends = new ArrayList<>();
        int loadBalancerPort = loadBalancer.getPublicPort();

        // Populate variables
        for (Application application : loadBalancer.getApplications()) {

            StringWriter stringWriter = new StringWriter();
            PrintWriter pw = new PrintWriter(stringWriter);

            // acl + use_backend
            rules.add("acl " + application.getName() + "rule path -m beg " + application.getPublicUrl());
            use_backends.add("use_backend " + application.getName() + " if " + application.getName() + "rule");

            // backend
            pw.println();
            pw.println(TAB + "backend " + application.getName());
//            pw.println(TAB2 + "reqrep ^([^\\ :]*)\\ " + application.getPublicUrl() + "/(.*)     \\1\\ /\\2");

            // server
            for (ApplicationInstance applicationInstance : application.getApplicationInstances()) {
                pw.println(TAB2 + "server " + applicationInstance.getName() + " " + applicationInstance.getHost() + ":" + applicationInstance.getPort() + applicationInstance.getPath() + " maxconn 32");
            }
            backends.add(stringWriter.toString());
            stringWriter.flush();
            pw.flush();
        }

        return buildString(rules, backends, use_backends, loadBalancerPort);
    }

    private String buildString(List<String> rules, List<String> backends, List<String> use_backends, int loadBalancerPort) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);

        writeDefaultsStart(pw);

        pw.println(TAB2 + "bind *:" + loadBalancerPort);

        // Write content
        for (String rule : rules) {
            pw.println(TAB2 + rule);
        }
        for (String use_backend : use_backends) {
            pw.println(TAB2 + use_backend);
        }
        for(String backend : backends) {
            pw.println(TAB2 + backend);
        }

        pw.println();
        pw.println(TAB + "listen stats *:" + ++loadBalancerPort);

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
        pw.println(TAB + "frontend http-in");
    }

    private void writeDefaultsEnd(PrintWriter pw) {
        pw.println(TAB2 + "mode http");
        pw.println(TAB2 + "stats enable");
        pw.println(TAB2 + "stats uri /proxy-stats");
        pw.println(TAB2 + "stats admin if TRUE");
    }
}
