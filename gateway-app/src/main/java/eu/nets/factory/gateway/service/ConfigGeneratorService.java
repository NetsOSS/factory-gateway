package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.StickySession;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        String haproxyInstallationPath = loadBalancer.getInstallationPath();

        List<String> rules = new ArrayList<>();
        List<String> backends = new ArrayList<>();
        List<String> use_backends = new ArrayList<>();
        int loadBalancerPort = loadBalancer.getPublicPort();

        // Populate variables
        for (Application application : loadBalancer.getApplications()) {

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            // acl + use_backend
            rules.add("acl " + application.getName() + "rule path -m beg " + application.getPublicUrl());
            use_backends.add("use_backend " + application.getName() + " if " + application.getName() + "rule");

            // backend
            printWriter.println();
            printWriter.println(TAB + "backend " + application.getName());
            printWriter.println(TAB2 + "option httpchk GET " + application.getCheckPath());

            // Check if app wants sticky cookies . cookie SERVERID insert indirect nocache
            if(application.getStickySession().name().equals("STICKY"))
                printWriter.println(TAB2 + "cookie JSESSIONID prefix");
//            printWriter.println(TAB2 + "reqrep ^([^\\ :]*)\\ " + application.getPublicUrl() + "/(.*)     \\1\\ /\\2");

            // server

            for(int i = 0; i < application.getApplicationInstances().size(); i++) {
                ApplicationInstance applicationInstance = application.getApplicationInstances().get(i);
                String state = "";
                String setup = "";
                if (applicationInstance.getHaProxyState().name().equals("MAINT"))
                    state = " disabled";
                if(application.getFailoverLoadBalancerSetup().name().equals("HOT_STANDBY"))
                    if(i > 0) {
                        setup = " backup";
                    }
                String s = TAB2 + "server " + applicationInstance.getName() + " " + applicationInstance.getHost() + ":" + applicationInstance.getPort() + applicationInstance.getPath() + " " + state + setup +" check";
                if(application.getStickySession().name().equals("STICKY"))
                 s+=" cookie " + applicationInstance.getName();
                printWriter.println(s);

               // printWriter.println(TAB2 + "server " + applicationInstance.getName() + " " + applicationInstance.getHost() + ":" + applicationInstance.getPort() + applicationInstance.getPath() + " check cookie " + applicationInstance.getName() + state + setup);

            }
            backends.add(stringWriter.toString());
            //stringWriter.flush();
            //printWriter.flush();
        }

        return buildString(rules, backends, use_backends, loadBalancerPort);
    }

    private String buildString(List<String> rules, List<String> backends, List<String> use_backends, int loadBalancerPort) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        writeDefaultsStart(printWriter);

        printWriter.println(TAB2 + "bind *:" + loadBalancerPort);

        // Write content
        for (String rule : rules) {
            printWriter.println(TAB2 + rule);
        }
        for (String use_backend : use_backends) {
            printWriter.println(TAB2 + use_backend);
        }
        for(String backend : backends) {
            printWriter.println(TAB2 + backend);
        }

        printWriter.println();
        printWriter.println(TAB + "listen stats *:" + ++loadBalancerPort);

        writeDefaultsEnd(printWriter);

        String strConfig = stringWriter.toString();
        //stringWriter.flush();
        //printWriter.flush();
        return strConfig;
    }

    private void writeDefaultsStart(PrintWriter pw) {
        pw.println(TAB + "global");
        pw.println(TAB2 + "daemon");
        pw.println(TAB2 + "maxconn 256");
//        pw.println(TAB2 + "pidfile haproxy.pid");
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

    private void writeFile(String fileName, String fileContents) throws IOException {
        Files.write(Paths.get(fileName), fileContents.getBytes());
    }
}
