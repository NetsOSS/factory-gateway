package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class FileWriterService {

    private final Logger log = getLogger(getClass());

    public void writeConfigFile(LoadBalancer loadBalancer, String fileName, String fileContents) throws GatewayException {

        String installationPath = loadBalancer.getInstallationPath();
        String host = loadBalancer.getHost();
        String username = loadBalancer.getUserName();   // TODO: Test with username (which username should we use? factory?)
        String sshKey = loadBalancer.getSshKey();       // TODO: Put valid private key in DB from GUI, and add public key to authorized_keys on server to make this work - TEST

        if (!Files.exists(Paths.get(installationPath)) || !Files.isDirectory(Paths.get(installationPath))) {
            throw new GatewayException("The path " + installationPath + " doesn't exist. No config-file will be generated.");
        }
        try {

            // Write local file - TODO: Could eventually be removed when SFTP is tested to work
            Files.write(Paths.get(installationPath + "/" + fileName), fileContents.getBytes());

            // Write remote file
            try (SshConnection c = new SshConnection(host, username, sshKey)) {
                c.writeRemoteFile(fileContents, installationPath + "/" + fileName);
            }


        } catch (IOException e) {
            String errorMessage = "Could not write file: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
    }
}
