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
    private static final String BASH_FILE = "hastart.sh";
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
            writeFile(loadBalancer, BASH_FILE, new String(content));

        } catch (IOException e) {
            String errorMessage = "Could not find script file: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }

        // Call startup script
        try (SshConnection c = new SshConnection(loadBalancer.getHost(), loadBalancer.getUserName(), loadBalancer.getSshKey())) {

            c.execute("cd " + loadBalancer.getInstallationPath() + " && bash " + BASH_FILE + " " + settings.getHaproxyBin(), settings.getTimeoutInSeconds());

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

//    public void startLocal(LoadBalancer loadBalancer) {
//
//        String installationPath = new File(loadBalancer.getInstallationPath()).getAbsolutePath();
//
//        // Create list with command and arguments
//        List<String> commands = new ArrayList<>();
//        commands.add(settings.getHaproxyBin());
//        commands.add("-f");
//        commands.add(CFG_FILE);
//        commands.add("-p");
//        commands.add(PID_FILE);
//
//        // Read pid-file if exists, and add pid as argument to do a "graceful" restart
//        if (Files.exists(Paths.get(installationPath + "/" + PID_FILE))) {
//            try (BufferedReader reader = Files.newBufferedReader(Paths.get(installationPath + "/" + PID_FILE))) {
//                String pid = reader.readLine();
//                commands.add("-sf");
//                commands.add(pid);
//            } catch (IOException e) {
//                // Should operation continue without the -sf option if the pid-file exists, but an IOException occurs?
//                String errorMessage = "Problem reading from pidfile " + installationPath + "/" + PID_FILE + "" + e.getLocalizedMessage();
//                log.warn(errorMessage, e);
//                throw new GatewayException(errorMessage);
//            }
//        }
//
//        ProcessBuilder pb = new ProcessBuilder(commands);
//
//        // Sett current dir til installation path
//        pb.directory(new File(installationPath));
//
//        // Redirect output to a log file - (Works on the Linux server. Use console output for local Windows (see below))
//        File logFile = new File(installationPath + "/" + LOG_FILE);
//        pb.redirectErrorStream(true);
//        pb.redirectOutput(ProcessBuilder.Redirect.to(logFile));
//        pb.redirectError(ProcessBuilder.Redirect.to(logFile));
//
////        Log to console
////        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
////        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
//
//        // Start HAProxy
//        try {
//            Process process = pb.start();
//
//            // Check exit value, to see if HAProxy was successfully restarted
//            process.waitFor();
//            if (process.exitValue() != 0) {
//                String errorMessage = "Loadbalancer could not be started. Check logfile " + installationPath + "/" + LOG_FILE;
//                log.warn(errorMessage);
//                throw new GatewayException(errorMessage);
//            }
//        } catch (IOException | InterruptedException e) {
//            String errorMessage = "Loadbalancer could not be started: " + e.getLocalizedMessage();
//            log.warn(errorMessage, e);
//            throw new GatewayException(errorMessage);
//        }
//        log.info("Started LoadBalancer");
//    }

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
