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
            PrintWriter printWriter = new PrintWriter(stringWriter);

            // acl + use_backend
            rules.add("acl " + application.getName() + "rule path -m beg " + application.getPublicUrl());
            use_backends.add("use_backend " + application.getName() + " if " + application.getName() + "rule");

            // backend
            printWriter.println();
            printWriter.println(TAB + "backend " + application.getName());
            printWriter.println(TAB2 + "option httpchk GET " + application.getCheckPath());

            //connect timeout
            printWriter.println(TAB2 + "timeout connect " + application.getConnectTimeout() + "ms");
            //read timeout
            printWriter.println(TAB2 + "timeout check " + application.getReadTimeout() + "ms");
            //retry timeout
            printWriter.println(TAB2 + "retries " + application.getRetryTimeout());

            // Check if app wants sticky cookies.
            if (application.getStickySession().name().equals("STICKY"))
                printWriter.println(TAB2 + "cookie JSESSIONID prefix");
            if (application.getStickySession().name().equals("STICKY_NEW_COOKIE"))
                printWriter.println(TAB2 + "cookie SERVERID_"+application.getName()+" insert indirect nocache");

//            printWriter.println(TAB2 + "reqrep ^([^\\ :]*)\\ " + application.getPublicUrl() + "/(.*)     \\1\\ /\\2");

            // server
            for(int i = 0; i < application.getApplicationInstances().size(); i++) {
                ApplicationInstance applicationInstance = application.getApplicationInstances().get(i);

                String serverConfigLine = TAB2 + "server " + applicationInstance.getName() + " " + applicationInstance.getHost() + ":" + applicationInstance.getPort() + applicationInstance.getPath() + " check weight " + applicationInstance.getWeight();

                if (applicationInstance.getHaProxyState().name().equals("MAINT"))
                    serverConfigLine += " disabled";

                if (application.getFailoverLoadBalancerSetup().name().equals("HOT_STANDBY") && i > 0)
                    serverConfigLine += " backup";

                if (application.getStickySession().name().equals("STICKY") || application.getStickySession().name().equals("STICKY_NEW_COOKIE"))
                    serverConfigLine += " cookie " + applicationInstance.getName();

                printWriter.println(serverConfigLine);
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

        //stringWriter.flush();
        //printWriter.flush();
        return stringWriter.toString();
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
}
