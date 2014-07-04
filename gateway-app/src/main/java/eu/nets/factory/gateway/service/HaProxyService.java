package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.GatewaySettings;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class HaProxyService {

    private static final String CFG_FILE = "haproxy.cfg";
    private static final String PID_FILE = "haproxy.pid";
    private static final String LOG_FILE = "haproxy.log";

    private final Logger log = getLogger(getClass());

    @Autowired
    private GatewaySettings settings;

    public void start(LoadBalancer loadBalancer) {

        String installationPath = new File(loadBalancer.getInstallationPath()).getAbsolutePath();

        // Create list with command and arguments
        List<String> commands = new ArrayList<>();
        commands.add(settings.getHaproxyBin());
        commands.add("-f");
        commands.add(CFG_FILE);
        commands.add("-p");
        commands.add(PID_FILE);

        // Read pid-file if exists, and add pid as argument to do a "graceful" restart
        if (Files.exists(Paths.get(installationPath + "/" + PID_FILE))) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(Paths.get(installationPath + "/" + PID_FILE));
                String pid = reader.readLine();
                commands.add("-sf");
                commands.add(pid);
            } catch (IOException e) {
                //TODO: Should operation continue without the -sf option if the pid-file exists, but an IOException occurs?
                String errorMessage = "Problem reading from pidfile " + installationPath + "/" + PID_FILE + "" + e.getLocalizedMessage();
                log.warn(errorMessage, e);
                throw new GatewayException(errorMessage);
            }
        }

        ProcessBuilder pb = new ProcessBuilder(commands);

        // Sett current dir til installation path
        pb.directory(new File(installationPath));

        // Redirect output to a log file - (Works on the Linux server. Use console output for local Windows (see below))
        File logFile = new File(installationPath + "/" + LOG_FILE);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
        pb.redirectError(ProcessBuilder.Redirect.appendTo(logFile));

//        Log to console
//        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        // Start HAProxy
        try {
            pb.start();
        } catch (IOException e) {
            String errorMessage = "Loadbalancer could not be started: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
        log.info("started LoadBalancer");
    }
}
