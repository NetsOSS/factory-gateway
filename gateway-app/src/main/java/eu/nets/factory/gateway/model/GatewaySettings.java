package eu.nets.factory.gateway.model;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GatewaySettings {

    @Autowired
    private Environment environment;

    private String userName;

    private boolean local;

    private boolean windows;

    private boolean unitTest;

    @PostConstruct
    public void postConstruct() {
        userName = System.getProperty("user.name");
        local = environment.acceptsProfiles("local");
        unitTest = environment.acceptsProfiles("unitTest");

        windows = System.getProperty("os.name").toLowerCase().contains("win");
    }

    public boolean loadResourcesFromDisk() {
        return environment.acceptsProfiles("local");
    }

    public boolean migrateDatabase() {
        return !getDatabaseUrl().startsWith("jdbc:h2:mem");
    }

    public String getDatabaseUrl() {
        if (local) {

            switch (userName) {
                case "tlaug":
                case "sleru":
                case "kwlar":
                case "ofbje":
                    return environment.getRequiredProperty("database.url");
                case "ogamm":
                    return "jdbc:oracle:thin:@vm-udb-7:1521:u7utv";
                case "mbyhr":
                    return environment.getRequiredProperty("database.url");
            }

            throw new IllegalStateException("Could not find database URL for user " + userName);
        } else if (unitTest) {
            return "jdbc:h2:mem:.";
        } else {
            return environment.getRequiredProperty("database.url");
        }
    }

    public String getDatabaseUsername() {
        if ((local || unitTest) && !getDatabaseUrl().startsWith("jdbc:h2:file")) {
            return userName;
        }
        return environment.getRequiredProperty("database.username");
    }

    public String getDatabasePassword() {
        if ((local || unitTest) && !getDatabaseUrl().startsWith("jdbc:h2:file")) {
            return userName;
        }
        return environment.getRequiredProperty("database.password");
    }

    public boolean isLocal() {
        return local;
    }

    public boolean isWindows() {
        return windows;
    }

    public String getHaproxyBin() {
        if (unitTest) {
            throw new RuntimeException("Not supported in unit tests.");
        }

        return windows ? environment.getRequiredProperty("haproxy.bin.windows") : environment.getRequiredProperty("haproxy.bin.linux");
    }

    public int getTimeoutInSeconds() {
        return environment.getRequiredProperty("haproxy.timeout", Integer.class);
    }
}
