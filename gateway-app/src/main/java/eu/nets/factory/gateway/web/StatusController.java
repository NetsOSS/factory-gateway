package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationRepository;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.model.LoadBalancerRepository;
import eu.nets.factory.gateway.service.EmailService;
import eu.nets.factory.gateway.service.StatusService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
@Transactional
public class StatusController {
    private final Logger log = getLogger(getClass());

    @Autowired
    LoadBalancerController loadBalancerController;

    @Autowired
    LoadBalancerRepository loadBalancerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    StatusService statusService;

    @Autowired
    EmailService emailService;

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/server-status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApplicationStatusModel getServerStatusForApplication(@PathVariable Long id) {

        Application application = applicationRepository.findOne(id);
        if(application == null) {
            return null;
        }

        ApplicationStatusModel applicationStatusModel = new ApplicationStatusModel();
        application.getApplicationInstances().
                forEach(ai -> applicationStatusModel.applicationInstances.put(ai.getId(), new ApplicationInstanceStatusModel(ai)));

        List<LoadBalancer> loadBalancers = application.getLoadBalancers();
        for(LoadBalancer loadBalancer: loadBalancers) {
            List<StatusModel> statusModelsFromCSV = parseCSV(readCSV(loadBalancer));
            for(StatusModel statusModel: statusModelsFromCSV) {
                if (!statusModel.data.get("pxname").equals(application.getName())) {
                    continue;
                }

                String svname = statusModel.data.get("svname");

                if (!svname.equals("BACKEND")) {
                    applicationStatusModel.getByName(svname).ifPresent(m -> m.statuses.put(loadBalancer.getId(), statusModel.data));
                }
                else {

                    applicationStatusModel.data.putAll(statusModel.data);
                }
            }
        }

        return applicationStatusModel;
    }

    public List<StatusModel> getServerStatus(List<StatusModel> statusModelsFromCSV, Application application ) {

        List<StatusModel> models = new ArrayList<StatusModel>();
        for(StatusModel statusModel: statusModelsFromCSV) {
            if(statusModel.data.get("pxname").equals(application.getName()) && !statusModel.data.get("svname").equals("BACKEND")) {
                models.add(statusModel);
            }
        }
        return models;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/backend-status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<Long, StatusModel> getBackendStatusForApplication(@PathVariable Long id) {
        log.info("StatusController.getBackendStatusForApplication, id={}", id);

        HashMap<Long, StatusModel> hashMap = new HashMap<>();
        Application application = applicationRepository.findOne(id);
        if(application == null) { throw new EntityNotFoundException("Application", id); }

        if(application == null) {
            return null;
        }

        List<LoadBalancer> loadBalancers = application.getLoadBalancers();
        for(LoadBalancer loadBalancer: loadBalancers) {
            List<StatusModel> models = parseCSV(readCSV(loadBalancer));
            hashMap.put(loadBalancer.getId(), getBackendServer(models, application));
        }

        return hashMap;
    }

    public StatusModel getBackendServer(List<StatusModel> models, Application application) {

        for(StatusModel model: models) {
            if(model.data.get("pxname").equals(application.getName()) && model.data.get("svname").equals("BACKEND")) {
                return model;
            }
        }
        return null;
    }

    public List<String> readCSV(LoadBalancer loadBalancer) {
        log.info("StatusController.getStatus");
        return statusService.readCSV(loadBalancer);
    }

    public List<StatusModel> parseCSV(List<String> csvString) {
        log.info("StatusController.parseCSV");
        return statusService.parseCSV(csvString);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatusModel> getStatusForLoadbalancer(@PathVariable Long id) {
        log.info("StatusController.getStatusForLoadBalancer, id={}", id);

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        if(loadBalancer == null) { throw new EntityNotFoundException("LoadBalancer", id); }

        List<String> csvString = readCSV(loadBalancer);

        return parseCSV(csvString);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/sendEmail/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String sendEmail(@PathVariable Long id) {
        log.info("StatusController.sendEmail, id={}", id);

        Application application = applicationRepository.findOne(id);
        if(application == null) { throw new EntityNotFoundException("Application", id); }

        emailService.sendEmail();
        return "Sent email status of " + application.getName() + " to " + application.getEmails();
    }
}
