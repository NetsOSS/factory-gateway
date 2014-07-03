package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.ApplicationInstance;

import java.util.HashMap;
import java.util.Map;

public class ApplicationInstanceStatusModel  {
    public final String name;

    public Map<Long, HashMap<String, String>> statuses = new HashMap<>();

    public ApplicationInstanceStatusModel(ApplicationInstance ai) {
        this.name = ai.getName();
    }
}
