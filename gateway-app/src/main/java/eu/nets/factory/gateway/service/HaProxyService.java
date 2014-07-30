package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.GatewaySettings;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.IOUtils;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class HaProxyService {

    private static final String CFG_FILE = "haproxy.cfg";
    private static final String HASTART_SH = "hastart.sh";
    private static final String HASTOP_SH = "hastop.sh";
    private static final String PID_FILE = "haproxy.pid";
    private static final String LOG_FILE = "haproxy.log";

    private final Logger log = getLogger(getClass());

    @Autowired
    private GatewaySettings settings;

    @Autowired
    ConfigGeneratorService configGeneratorService;

    public void start(LoadBalancer loadBalancer) {
        try {
            // Get script file
            byte[] content = IOUtils.readFully(this.getClass().getResource("/script/harestart.sh").openStream(), Integer.MAX_VALUE, true);

            // Push script file
            writeFile(loadBalancer, HASTART_SH, new String(content));

        } catch (IOException e) {
            String errorMessage = "Could not find script file: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }

        // Call startup script
        try (SshConnection c = new SshConnection(loadBalancer.getHost(), loadBalancer.getUserName(), loadBalancer.getSshKey())) {

            c.execute("cd " + loadBalancer.getInstallationPath() + " && bash " + HASTART_SH + " " + settings.getHaproxyBin(), settings.getTimeoutInSeconds());

        } catch (IOException e) {
            String errorMessage = "Could not execute command: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
    }

    public void stop(LoadBalancer loadBalancer) {
        try {
            // Get script file
            byte[] content = IOUtils.readFully(this.getClass().getResource("/script/hastop.sh").openStream(), Integer.MAX_VALUE, true);

            // Push script file
            writeFile(loadBalancer, HASTOP_SH, new String(content));

        } catch (IOException e) {
            String errorMessage = "Could not find script file: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }

        // Call startup script
        try (SshConnection c = new SshConnection(loadBalancer.getHost(), loadBalancer.getUserName(), loadBalancer.getSshKey())) {

            c.execute("cd " + loadBalancer.getInstallationPath() + " && bash " + HASTOP_SH + " " + settings.getHaproxyBin(), settings.getTimeoutInSeconds());

        } catch (IOException e) {
            String errorMessage = "Could not execute command: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
    }

    public void pushConfigFile(LoadBalancer loadBalancer) {
        String strConfig = configGeneratorService.generateConfig(loadBalancer);
        writeFile(loadBalancer, CFG_FILE, strConfig);
    }

    private void writeFile(LoadBalancer loadBalancer, String fileName, String fileContents) throws GatewayException {

        String installationPath = loadBalancer.getInstallationPath();
        String host = loadBalancer.getHost();
        String username = loadBalancer.getUserName();
        String sshKey = loadBalancer.getSshKey();

        try (SshConnection c = new SshConnection(host, username, sshKey)) {
            c.writeRemoteFile(fileContents, installationPath + "/" + fileName);
        }
    }
}
