package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.HeaderRule;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
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


    /**
     * Sorts the applications. First on name.
     * If 2 applications has the same name, sort on the number of header rules.
     * Such that the app with more headers, will be used before the others which accept all headers.
     *
     * Another way of sorting this, would be to search for all with the same acl path, and negate all other rules.
     * Then the sorting would not be necessary
     */
    class NameAndRuleComparator implements Comparator<Application>{
        @Override
        public int compare(Application app1, Application app2) {
            int aclUrl = app1.getPublicUrl().compareTo(app2.getPublicUrl());
            if(aclUrl==0)
                return app2.getHeaderRules().size() - app1.getHeaderRules().size();
            return aclUrl;
        }
    }

    public String generateConfig(LoadBalancer loadBalancer) {

        List<String> rules = new ArrayList<>();
        List<String> backends = new ArrayList<>();
        List<String> use_backends = new ArrayList<>();
        int loadBalancerPort = loadBalancer.getStatsPort();

        List<Application> lbApplications = loadBalancer.getApplications();
        lbApplications.sort(new NameAndRuleComparator());

        // Populate variables
        for (Application application :lbApplications) {

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            // acl + use_backend
            //String aclRule = "acl p_" + application.getName() + "_rule path -m beg " + application.getPublicUrl()+" ";
            StringBuilder aclRules = new StringBuilder();
            aclRules.append("{ path_beg "+application.getPublicUrl()+" } ");
            for(HeaderRule headerRule : application.getHeaderRules()){

                aclRules.append("{ hdr_reg("+headerRule.getName()+") "+headerRule.getPrefixMatch()+" } ");
            }


            use_backends.add("use_backend " + application.getName() + " if " + aclRules.toString());

            // backend
            printWriter.println();
            printWriter.println(TAB + "backend " + application.getName());
            printWriter.println(TAB2 + "option httpchk GET " + application.getCheckPath());

            //debug, adding headers to see which was chosen.
            /// reqadd X-CustomHeader:\ debugMode
            printWriter.println(TAB2 + "reqadd X-ForwardToApp:\\ " + application.getName());
            //connect timeout
           /* printWriter.println(TAB2 + "timeout connect " + application.getConnectTimeout() + "ms");
            //read timeout
            printWriter.println(TAB2 + "timeout server " + application.getReadTimeout() + "ms");
            //retry timeout
            printWriter.println(TAB2 + "retries " + application.getRetryTimeout());*/


            // Check if app wants sticky cookies.
            if (application.getStickySession().name().equals("STICKY"))
                printWriter.println(TAB2 + "cookie JSESSIONID prefix");
            if (application.getStickySession().name().equals("STICKY_NEW_COOKIE"))
                printWriter.println(TAB2 + "cookie SERVERID_"+application.getName()+" insert indirect nocache");

//            printWriter.println(TAB2 + "reqrep ^([^\\ :]*)\\ " + application.getPublicUrl() + "/(.*)     \\1\\ /\\2");

            // server
            for(int i = 0; i < application.getApplicationInstances().size(); i++) {
                ApplicationInstance applicationInstance = application.getApplicationInstances().get(i);

                String serverConfigLine = TAB2 + "server " + applicationInstance.getName() + " " + applicationInstance.getHost() + ":" + applicationInstance.getPort() + applicationInstance.getPath() + " check";

                if(applicationInstance.getWeight() != 0)
                    serverConfigLine += " weight " + applicationInstance.getWeight();

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

        return buildString(rules, backends, use_backends, loadBalancer);
    }

    private String buildString(List<String> rules, List<String> backends, List<String> use_backends, LoadBalancer loadBalancer) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        int loadBalancerPort = loadBalancer.getStatsPort();

        writeDefaultsStart(printWriter, loadBalancer);

        printWriter.println(TAB2 + "bind *:" + loadBalancer.getStatsPort());

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
        int statsPort = loadBalancer.getStatsPort();
        printWriter.println(TAB + "listen stats *:" + ++statsPort);

        writeDefaultsEnd(printWriter,loadBalancer.getName());

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
        /*
        pw.println(TAB2 + "timeout connect 5000ms");
        pw.println(TAB2 + "timeout client 50000ms");
        pw.println(TAB2 + "timeout server 50000ms");*/
        pw.println();
        pw.println(TAB + "frontend http-in");
    }

    private void writeDefaultsEnd(PrintWriter pw,String nodeName) {
        pw.println(TAB2 + "mode http");
        pw.println(TAB2 + "stats enable");
        pw.println(TAB2 + "stats uri /proxy-stats");
        pw.println(TAB2 + "stats admin if TRUE");
        pw.println(TAB2 + "stats show-node "+nodeName);
    }
}
