package eu.nets.factory.gateway.service;

import eu.nets.factory.gateway.GatewayException;
import net.schmizz.sshj.Config;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.xfer.InMemorySourceFile;
import org.slf4j.Logger;

import java.io.*;

import static org.slf4j.LoggerFactory.getLogger;

public class SshConnection implements Closeable {

    private final Logger log = getLogger(getClass());

    private SSHClient sshClient;

    public SshConnection(String host, String username, String privateKey) throws IOException {
        Config sshConfig = new DefaultConfig();
        SSHClient client = new SSHClient(sshConfig);
        client.addHostKeyVerifier((s, i, publicKey) -> true);
        client.loadKnownHosts(new File("known_hosts"));
        client.connect(host);
        try {
            client.authPublickey(username, client.loadKeys(privateKey, null, null));
        } catch (UserAuthException e) {
            String errorMessage = "Could not authenticate " + username + "-user using the \"publickey\" authentication method - " + e.getLocalizedMessage();
            log.warn(errorMessage, e.getLocalizedMessage());
            throw new GatewayException(errorMessage);
        }
        sshClient = client;
    }


    public void writeRemoteFile(final String fileContents, final String remotePath) throws IOException {
        SFTPClient sftpClient = sshClient.newSFTPClient();
        try {
            sftpClient.put(new InMemoryFile(fileContents), remotePath);
        } catch (SFTPException e) {
            String errorMessage = "Could not upload file to " + remotePath + ": " + e.getLocalizedMessage() + "/path";
            log.warn(errorMessage, e);
            throw new GatewayException(errorMessage);
        }
    }

    public void execute(String ... commands) throws IOException {
        for (String command : commands) {
            execute(command);
        }
    }

    public void execute(String commandString) throws IOException {
        Command command = null;
        Session session = sshClient.startSession();
        command = session.exec("" + commandString);
    }


    @Override
    public void close() throws IOException {
        sshClient.close();
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
}
