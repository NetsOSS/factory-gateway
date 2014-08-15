package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import net.schmizz.sshj.Config;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.xfer.InMemorySourceFile;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class SshConnection implements Closeable {


    private final Logger log = getLogger(getClass());

    private SSHClient sshClient;

    public SshConnection(String host, String username, String privateKey) {
        Config sshConfig = new DefaultConfig();
        SSHClient client = new SSHClient(sshConfig);
        client.addHostKeyVerifier((s, i, publicKey) -> true);
        try {

            client.connect(host);
            client.authPublickey(username, client.loadKeys(privateKey, null, null));
            sshClient = client;

        } catch (UnknownHostException e) {
            String errorMessage = "Unknown host: " + e.getLocalizedMessage();
            log.warn(errorMessage);
            throw new GatewayException(errorMessage);
        } catch (UserAuthException e) {
            String errorMessage = "Could not authenticate as '" + username + "' on " + host + " using the \"publickey\" authentication method - " + e.getLocalizedMessage();
            log.warn(errorMessage);
            throw new GatewayException(errorMessage);
        } catch (IOException e) {
            String errorMessage = "Problem connecting with SSH: " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
    }

    public void execute(String command, int timeoutSeconds) throws IOException {

        Session session = sshClient.startSession();
        Session.Command cmd = session.exec(command);

        new StreamConsumer("stdout(" + command + ")", cmd.getInputStream()).start();
        new StreamConsumer("stderr(" + command + ")", cmd.getErrorStream()).start();

        cmd.join(timeoutSeconds, TimeUnit.SECONDS);

        log.info("Command exited with: {}", cmd.getExitStatus());
        if (cmd.getExitStatus() != 0) {
            throw new GatewayException("The command '" + command + "' failed with exit code " + cmd.getExitStatus());
        }
    }

    /**
     * Writes the given fileContents to the given remotePath
     *
     * @param fileContents - The String to write
     * @param remotePath   - Destination path to write to
     */
    public void writeRemoteFile(final String fileContents, final String remotePath) {
        try (SFTPClient sftpClient = sshClient.newSFTPClient()) {
            sftpClient.put(new InMemoryFile(fileContents), remotePath);
        } catch (IOException e) {
            String errorMessage = "Could not upload file: " + sshClient.getRemoteHostname() + ":" + remotePath + ": " + e.getLocalizedMessage();
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
    }

    @Override
    public void close() {
        try {
            sshClient.close();
        } catch (IOException e) {
            log.warn("Could nor close ssh client", e);
        }
    }

    /**
     * Inner class to represent a local in-memory file to write to remote
     */
    private class InMemoryFile extends InMemorySourceFile {

        private final String fileContents;

        public InMemoryFile(String fileContents) {
            this.fileContents = fileContents;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public long getLength() {
            return 0;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(fileContents.getBytes());
        }
    }

    private class StreamConsumer extends Thread {
        private final String name;
        private final InputStream stream;

        private StreamConsumer(String name, InputStream stream) {
            this.name = name;
            this.stream = stream;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "ISO8859-1"));

                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(name + ": " + line);
                }
            } catch (IOException e) {
                log.error("oops", e);
            }
        }
    }
}
