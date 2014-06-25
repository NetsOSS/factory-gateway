package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class AppGroupModel {

        public Long id;

        public String name;
        public List<AppModel> applications;

        public AppGroupModel() { }

        public AppGroupModel(ApplicationGroup appGroup) {
            this(appGroup.getId(), appGroup.getName());
            this.applications = appGroup.getApplications().stream().map(AppModel::new).collect(toList());
        }

        public AppGroupModel(Long id, String name) {
            this.id = id;
            this.name = name;
        }


        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }