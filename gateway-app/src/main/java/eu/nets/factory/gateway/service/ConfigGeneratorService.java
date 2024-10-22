package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.HeaderRule;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.web.AppGroupModel;
import eu.nets.factory.gateway.web.ApplicationGroupController;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigGeneratorService {

//    private StringWriter stringWriter = new StringWriter();
//    private PrintWriter pw = new PrintWriter(stringWriter);

    private final static String TAB = "    ";
    private final static String TAB2 = TAB + TAB;
    @Autowired
    ApplicationGroupController applicationGroupController;

    public String generateConfig() {
        return null;
    }

    public String generateConfig(LoadBalancer loadBalancer) {

        List<String> backends = new ArrayList<>();
        HashMap<String, List<String>> frontends = new HashMap<>();

        // Populate variables
        for (Application application : loadBalancer.getApplications()) {

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            String frontendKey = application.getApplicationGroup().getName() + "_" + application.getApplicationGroup().getId();
            //frontend
            if (frontends.containsKey(frontendKey)) {
                List<String> values = frontends.remove(frontendKey);
                StringBuilder aclRules = new StringBuilder();
                aclRules.append("{ path_beg " + application.getPublicUrl() + " } ");
                for (HeaderRule headerRule : application.getHeaderRules()) {
                    aclRules.append("{ hdr_reg(" + headerRule.getName() + ") " + headerRule.getPrefixMatch() + " } ");
                }

                values.add("use_backend " + application.getName() + "_" + application.getId() + " if " + aclRules.toString());
                frontends.put(frontendKey, values);

            } else {
                List<String> values = new ArrayList<>();

                StringBuilder aclRules = new StringBuilder();
                aclRules.append("{ path_beg " + application.getPublicUrl() + " } ");
                for (HeaderRule headerRule : application.getHeaderRules()) {

                    aclRules.append("{ hdr_reg(" + headerRule.getName() + ") " + headerRule.getPrefixMatch() + " } ");
                }

                values.add("use_backend " + application.getName() + "_" + application.getId() + " if " + aclRules.toString());
                frontends.put(frontendKey, values);
            }


            // backend
            printWriter.println();
            printWriter.println(TAB + "backend " + application.getName() + "_" + application.getId());
            printWriter.println(TAB2 + "option httpchk GET " + application.getCheckPath());

            //reqrep ^([^\ ]*)\ /lang/blog/(.*) \1\ /blog/lang/\2
            printWriter.println(TAB2 + "reqrep ^([^\\ ]*)\\ " + application.getPublicUrl() + "(.*) \\1\\ " + application.getPrivatePath() + "\\2");

            //debug, adding headers to see which was chosen.
            /// reqadd X-CustomHeader:\ debugMode
            printWriter.println(TAB2 + "reqadd X-ForwardToApp:\\ " + application.getName());

            // Check if app wants sticky cookies.
            if (application.getStickySession().name().equals("STICKY"))
                printWriter.println(TAB2 + "cookie JSESSIONID prefix");
            if (application.getStickySession().name().equals("STICKY_NEW_COOKIE"))
                printWriter.println(TAB2 + "cookie SERVERID_" + application.getName() + " insert indirect nocache");

//            printWriter.println(TAB2 + "reqrep ^([^\\ :]*)\\ " + application.getPublicUrl() + "/(.*)     \\1\\ /\\2");

            // server
            for (int i = 0; i < application.getApplicationInstances().size(); i++) {
                ApplicationInstance applicationInstance = application.getApplicationInstances().get(i);

                String serverConfigLine = TAB2 + "server " + applicationInstance.getName() + "_" + applicationInstance.getId() + " " + applicationInstance.getHost() + ":" + applicationInstance.getPort() + applicationInstance.getPath() + " check";

                if (applicationInstance.getWeight() != 0)
                    serverConfigLine += " weight " + applicationInstance.getWeight();

                if (applicationInstance.getHaProxyState().name().equals("MAINT"))
                    serverConfigLine += " disabled";

                if (applicationInstance.isBackup())
                    serverConfigLine += " backup";

                if (application.getStickySession().name().equals("STICKY") || application.getStickySession().name().equals("STICKY_NEW_COOKIE"))
                    serverConfigLine += " cookie " + applicationInstance.getName() + "_" + applicationInstance.getId();

                printWriter.println(serverConfigLine);
            }

            backends.add(stringWriter.toString());
            //stringWriter.flush();
            //printWriter.flush();
        }

        return buildString(frontends, backends, loadBalancer);
    }

    private String buildString(HashMap<String, List<String>> frontends, List<String> backends, LoadBalancer loadBalancer) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        writeDefaultsStart(printWriter, loadBalancer);

        //write frontend
        for (String key : frontends.keySet()) {
            printWriter.println();

            printWriter.println(TAB + "frontend " + key);
            int port = 0;
            List<AppGroupModel> groups = applicationGroupController.listAllAppGroups();
            for (AppGroupModel a : groups) {
                if (key.equals(a.getName() + "_" + a.getId())) {
                    port = a.getPort();
                    break;
                }
            }
            printWriter.println(TAB2 + "bind *:" + port);

            List<String> values = frontends.get(key);
            for (String line : values) {
                printWriter.println(TAB2 + line);
            }
        }

        // Write content
        for (String backend : backends) {
            printWriter.println(TAB2 + backend);
        }

        printWriter.println();

        printWriter.println(TAB + "listen stats *:" + loadBalancer.getStatsPort());

        writeDefaultsEnd(printWriter, loadBalancer.getName());

        //stringWriter.flush();
        //printWriter.flush();
        return stringWriter.toString();
    }

    private void writeDefaultsStart(PrintWriter pw, LoadBalancer loadBalancer) {
        pw.println(TAB + "global");
        pw.println(TAB2 + "daemon");
        pw.println(TAB2 + "maxconn 256");
//        pw.println(TAB2 + "pidfile haproxy.pid");
        pw.println();
        pw.println(TAB + "defaults");
        pw.println(TAB2 + "mode http");
        pw.println(TAB2 + "timeout check " + loadBalancer.getCheckTimeout() + "ms");
        pw.println(TAB2 + "timeout connect " + loadBalancer.getConnectTimeout() + "ms");
        pw.println(TAB2 + "timeout server " + loadBalancer.getServerTimeout() + "ms");
        pw.println(TAB2 + "timeout client " + loadBalancer.getClientTimeout() + "ms");
        pw.println(TAB2 + "retries " + loadBalancer.getRetries());

    }

    private void writeDefaultsEnd(PrintWriter pw, String nodeName) {
        pw.println(TAB2 + "mode http");
        pw.println(TAB2 + "stats enable");
        pw.println(TAB2 + "stats uri /proxy-stats");
        pw.println(TAB2 + "stats admin if TRUE");
        pw.println(TAB2 + "stats show-node " + nodeName);
    }
}
