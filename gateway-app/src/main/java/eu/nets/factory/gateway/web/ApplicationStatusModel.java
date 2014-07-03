package eu.nets.factory.gateway.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ApplicationStatusModel {
    public Map<Long, ApplicationInstanceStatusModel> applicationInstances = new HashMap<>();
    public HashMap<String, String> data = new HashMap<String, String>();
    public ApplicationStatusModel() {
    }

    public ApplicationStatusModel(Map<Long, ApplicationInstanceStatusModel> applicationInstances) {
        this.applicationInstances = applicationInstances;
    }

    public Optional<ApplicationInstanceStatusModel> getByName(String svname) {
        for (ApplicationInstanceStatusModel m : applicationInstances.values()) {
            if (m.name.equals(svname)) {
                return of(m);
            }
        }

        return empty();
    }
}
