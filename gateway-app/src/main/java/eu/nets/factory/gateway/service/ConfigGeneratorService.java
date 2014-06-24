package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigGeneratorService {
    public String generateConfig() {
        return null;
    }

    public String generateConfig(LoadBalancer loadBalancer) {

        StringBuilder config = new StringBuilder();

        //Fetch from database
        List<Application> applications = loadBalancer.getApplications();


        List<String> rules = new ArrayList<>();
        List<String> backends = new ArrayList<>();

        //Make config
        for (Application application : applications) {
            rules.add("acl " + application.getName() + "rule path -m beg /" + application.getPublicUrl() + "\n" +
                        "use_backend " + application.getName() + " if " + application.getName() + "rule\n");

            StringBuilder b = new StringBuilder().append("\nbackend " + application.getName() + "\n");
            b.append("reqrep ^([^\\ :]*)\\ /ofbj/(.*)     \\1\\ /\\2\n");

            List<ApplicationInstance> applicationInstances = application.getApplicationInstances();
            for (ApplicationInstance applicationInstance : applicationInstances) {
                b.append("server " + applicationInstance.getName() + " " + applicationInstance.getHost() + ":" + applicationInstance.getPort() + " maxconn 32\n");
            }
            backends.add(b.toString());
        }


        for (String rule : rules) {
            config.append(rule);
        }

        for(String backend : backends) {
            config.append(backend);
        }


//        config.append("   # Simple configuration for an HTTP proxy listening on port 80 on all\n" +
//                "    # interfaces and forwarding requests to a single backend \"servers\" with a\n" +
//                "    # single server \"server1\" listening on 127.0.0.1:8000\n" +
//                "    global\n" +
//                "        daemon\n" +
//                "        maxconn 256\n" +
//                "\n" +
//                "    defaults\n" +
//                "        mode http\n" +
//                "        timeout connect 5000ms\n" +
//                "        timeout client 50000ms\n" +
//                "        timeout server 50000ms\n" +
//                "\n" +
//                "    frontend http-in3\n" +
//                "        bind *:30111\n" +
//                "#       acl rule1 path -m beg /data\n" +
//                "        acl rule2 path -m beg /ofbj\n" +
//                "        acl rule3 path -m beg /ogamm\n" +
//                "#       use_backend servers2 if rule1\n" +
//                "        use_backend servers3 if rule2\n" +
//                "        use_backend servers4 if rule3\n" +
//                "        default_backend servers\n" +
//                "\n" +
//                "    backend servers\n" +
//                "#        server server1 127.0.0.1:9002 maxconn 32\n" +
//                "        server server2 svn.bbsas.no:80 maxconn 32\n" +
//                "\n" +
//                "    backend servers2\n" +
//                "        server server3 172.21.3.13:9002 maxconn 32\n" +
//                "\n" +
//                "    backend servers3\n" +
//                "        reqrep ^([^\\ :]*)\\ /ofbj/(.*)     \\1\\ /\\2\n" +
//                "        server server3 172.21.3.13:9002 maxconn 32\n" +
//                "\n" +
//                "    backend servers4\n" +
//                "        reqrep ^([^\\ :]*)\\ /ogamm(.*)     \\1\\ /\\2\n" +
//                "        server server3 172.21.3.45:9002 maxconn 32\n" +
//                "        server server4 172.21.3.45:9002 maxconn 32\n" +
//                "\n" +
//                "    listen stats *:33334\n" +
//                "        mode    http\n" +
//                "        stats enable\n" +
//                "        stats uri       /proxy-stats\n" +
//                "        stats admin if TRUE\n");


        return config.toString();
    }
}
