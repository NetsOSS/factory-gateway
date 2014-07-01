package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class FileWriterService {

    private final Logger log = getLogger(getClass());

    public void writeConfigFile(String installationPath, String fileName, String fileContents) throws GatewayException {

        if (!Files.exists(Paths.get(installationPath)) || !Files.isDirectory(Paths.get(installationPath))) {
            throw new GatewayException("The path " + installationPath + " doesn't exist. No config-file will be generated.");
        }
        try {

            Files.write(Paths.get(installationPath + "/" + fileName), fileContents.getBytes());

        } catch (IOException e) {
            String errorMessage = "Could not write file: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
    }
}
