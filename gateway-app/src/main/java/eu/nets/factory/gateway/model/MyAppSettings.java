package eu.nets.factory.gateway.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MyAppSettings {

    @Autowired
    private Environment environment;

    private String userName;

    private boolean local;

    @PostConstruct
    public void postConstruct() {
        userName = System.getProperty("user.name");
        local = environment.acceptsProfiles("local");
    }

    public boolean loadResourcesFromDisk() {
        return environment.acceptsProfiles("local");
    }

    public boolean migrateDatabase() {
        return environment.acceptsProfiles("local") && !getDatabaseUrl().contains("h2");
    }

    public String getDatabaseUrl() {
        if (!local) {
            return environment.getRequiredProperty("database.url");
        }

        switch (userName) {
            case "tlaug":
            case "sleru":
            case "kwlar":
            case "ofbje":
            case "ogamm":
            case "mbyhr":
                return "jdbc:oracle:thin:@vm-udb-7:1521:u7utv";
        }

        throw new IllegalStateException("Could not find database URL for user " + userName);
    }

    public String getDatabaseUsername() {
        if (local) {
            return userName;
        }
        return environment.getRequiredProperty("database.username");
    }

    public String getDatabasePassword() {
        if (local) {
            return userName;
        }
        return environment.getRequiredProperty("database.password");
    }

    public boolean isLocal() {
        return local;
    }
}
