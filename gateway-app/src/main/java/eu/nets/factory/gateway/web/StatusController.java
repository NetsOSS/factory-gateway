package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.EntityNotFoundException;
import eu.nets.factory.gateway.GatewayException;
import eu.nets.factory.gateway.model.*;
import eu.nets.factory.gateway.service.EmailService;
import eu.nets.factory.gateway.service.StatusService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ApplicationInstanceRepository applicationInstanceRepository;

    @Autowired
    StatusService statusService;

    @Autowired
    EmailService emailService;


    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/statusIsOnline", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean isLoadBalancerOnline(@PathVariable Long id) {
        log.info("StatusController.isLoadBalancerOnline, id={}", id);

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        if (loadBalancer == null) {
            throw new EntityNotFoundException("LoadBalancer", id);
        }
        //Not sure if we should return an empty list or null. But angular currently checks if its null, to see if the proxy is running.
        List<StatusModel> list = statusService.getStatusForLoadBalancer(id);

        if(list==null) return false;
        if(list.isEmpty()) return false;
        if(list.size()<1) return false;
        String status = list.get(0).data.get("status");

        if(status==null || status.equals("offline")) return false;

        return true;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/server-status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApplicationStatusModel getServerStatusForApplication(@PathVariable Long id) {

        Application application = applicationRepository.findOne(id);
        if (application == null) {
            return null;
        }

        ApplicationStatusModel applicationStatusModel = new ApplicationStatusModel();
        application.getApplicationInstances().
                forEach(ai -> applicationStatusModel.applicationInstances.put(ai.getId(), new ApplicationInstanceStatusModel(ai)));

        List<LoadBalancer> loadBalancers = application.getLoadBalancers();

        for (LoadBalancer loadBalancer : loadBalancers) {


            List<StatusModel> statusModelsFromCSV = statusService.getStatusForLoadBalancer(loadBalancer.getId());
            if (statusModelsFromCSV== null || statusModelsFromCSV.isEmpty()) {
                //HAproxy Not running
                /*
                For all loadbalancer that this application has.
                If the load balancer is not running.
                Add all applicationinstances as not running.
                 */
                log.info("");
                for(ApplicationInstance applicationInstance : application.getApplicationInstances()){
                    applicationStatusModel.applicationInstances.put(loadBalancer.getId(),null);
                }
                //applicationStatusModel.data.put(loadBalancer.getId(),null);
            }
            for (StatusModel statusModel : statusModelsFromCSV) {
                if (!statusModel.data.get("pxname").equals(application.getName())) {
                    continue;
                }

                String svname = statusModel.data.get("svname");

                if (!svname.equals("BACKEND")) {
                    applicationStatusModel.getByName(svname).ifPresent(m -> m.statuses.put(loadBalancer.getId(), statusModel.data));
                } else {
                    //FRONTEND
                    applicationStatusModel.data.putAll(statusModel.data);
                }
            }

        }

        return applicationStatusModel;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applicationInstance/{id}/status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<Long, StatusModel> getStatusForOneServer(@PathVariable Long id) {
        log.info("StatusController.getStatusForOneServer, id={}", id);
        ApplicationInstance applicationInstance = applicationInstanceRepository.findOne(id);

        if (applicationInstance == null) {
            throw new EntityNotFoundException("ApplicationInstance", id);
        }

        Application application = applicationInstance.getApplication();

        HashMap<Long, StatusModel> appInstStatusModelList = new HashMap<>();
        for (LoadBalancer lb : application.getLoadBalancers()) {
            List<StatusModel> list = statusService.getStatusForLoadBalancer(lb.getId());

            for (StatusModel statusModel : list) {
                if (statusModel.data.get("svname").equals(applicationInstance.getName())) {
                    appInstStatusModelList.put(lb.getId(), statusModel);
                }
            }
        }

        if (appInstStatusModelList.isEmpty()) {
            return null;
        }
        return appInstStatusModelList;
    }


    /*
    Possibly an unnecessary method
     */
    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/backend-status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<Long, StatusModel> getBackendStatusForApplication(@PathVariable Long id) {
        log.info("StatusController.getBackendStatusForApplication, id={}", id);

        HashMap<Long, StatusModel> hashMap = new HashMap<>();
        Application application = applicationRepository.findOne(id);
        if (application == null) {
            throw new EntityNotFoundException("Application", id);
        }


        List<LoadBalancer> loadBalancers = application.getLoadBalancers();
        for (LoadBalancer loadBalancer : loadBalancers) {

            List<StatusModel> models = statusService.getStatusForLoadBalancer(loadBalancer.getId());

            hashMap.put(loadBalancer.getId(), getBackendServer(models, application));
        }

        return hashMap;
    }

    public StatusModel getBackendServer(List<StatusModel> models, Application application) {
        if (models == null)
            return null;

        for (StatusModel model : models) {
            if (model.data.get("pxname").equals(application.getName()) && model.data.get("svname").equals("BACKEND")) {
                return model;
            }
        }
        return null;
    }

    //Should be private. but used in test. fix later
    public List<String> readCSV(LoadBalancer loadBalancer) {
        log.info("StatusController.getStatus");
        // return  statusService.getStatusForLoadBalancer(loadBalancer.getId());
        return statusService.readCSV(loadBalancer);
    }

    //Should be private. but used in test. fix later
    public List<StatusModel> parseCSV(List<String> csvString) {
        log.info("StatusController.parseCSV");
        return statusService.parseCSV(csvString);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatusModel> getStatusForLoadbalancer(@PathVariable Long id) {
        log.info("StatusController.getStatusForLoadBalancer, id={}", id);

        LoadBalancer loadBalancer = loadBalancerRepository.findOne(id);
        if (loadBalancer == null) {
            throw new EntityNotFoundException("LoadBalancer", id);
        }
        //Not sure if we should return an empty list or null. But angular currently checks if its null, to see if the proxy is running.
        List<StatusModel> list = statusService.getStatusForLoadBalancer(id);
        if(list==null) return null;
        return list.isEmpty() ? null : list;
    }

    // Test metode - kan fjernes
    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/sendEmail/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String sendEmail(@PathVariable Long id) {
        log.info("StatusController.sendEmail, id={}", id);




        return "Sent email, maybe?? ";
    }
}
