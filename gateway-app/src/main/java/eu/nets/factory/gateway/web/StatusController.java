package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.*;
import eu.nets.factory.gateway.service.EmailService;
import eu.nets.factory.gateway.service.StatusService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
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
    private LoadBalancerController loadBalancerController;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private ApplicationInstanceController applicationInstanceController;


    @Autowired
    private LoadBalancerRepository loadBalancerRepository;

    //TODO: remove
    /*
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationInstanceRepository applicationInstanceRepository;
    */


    @Autowired
    StatusService statusService;

    @Autowired
    EmailService emailService;


    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/statusIsOnline", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean isLoadBalancerOnline(@PathVariable Long id) {
        loadBalancerController.findEntityById(id);

        StatusService.Status status = statusService.getStatusForLoadBalancer(id);
        return status.up;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/applications/{id}/server-status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<Long,StatusService.BackendStatus> getServerStatusForApplication(@PathVariable Long id) {
        Application application = applicationController.findEntityById(id);

        return statusService.getStatusForApplication(application);
    }

    //TODO: remove method - this method always returns null
    @RequestMapping(method = RequestMethod.GET, value = "/data/applicationInstance/{id}/status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<Long, StatusModel> getStatusForOneServer(@PathVariable Long id) {
        log.info("StatusController.getStatusForOneServer, id={}", id);
        ApplicationInstance applicationInstance = applicationInstanceController.findEntityById(id);

//        applicationInstanceRepository.findOne(id);
//        if (applicationInstance == null) {
//            throw new EntityNotFoundException("ApplicationInstance", id);
//        }

        Application application = applicationInstance.getApplication();
        return null;
    }

    public StatusModel getBackendServer(List<StatusModel> models, Application application) {
        if (models == null || application == null)
            return null;

        for (StatusModel model : models) {
            if (model.data.get("pxname").equals(application.getName()) && model.data.get("svname").equals("BACKEND")) {
                return model;
            }
        }
        return null;
    }

    protected List<String> readCSV(LoadBalancer loadBalancer) {
        log.info("StatusController.getStatus");
        return statusService.readCSV(loadBalancer);
    }

    protected List<StatusModel> parseCSV(List<String> csvString) {
        log.info("StatusController.parseCSV");
        return statusService.parseCSV(csvString);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/load-balancers/{id}/status2", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public StatusService.Status getStatusForLoadbalancer2(@PathVariable Long id) {
        loadBalancerController.findEntityById(id);
        return  statusService.getStatusForLoadBalancer(id);
    }

    //TODO: remove method - this method always returns an empty HashMap
    @RequestMapping(method = RequestMethod.GET, value = "/data/all-load-alancers/status", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, List<StatusModel>> getStatusForAllLoadbalancers() {
        HashMap<String, List<StatusModel>> map = new HashMap<>();

        List<LoadBalancer> loadBalancers = loadBalancerRepository.findAll();
        if (loadBalancers.size() == 0) {
            return null;
        }

//        for (LoadBalancer loadBalancer : loadBalancers) {
//            if (isLoadBalancerOnline(loadBalancer.getId())) {
//                List<StatusModel> list = statusService.getStatusForLoadBalancer(loadBalancer.getId());
//                map.put(loadBalancer.getName(), list);
//
//            } else {
//                map.put(loadBalancer.getName(), null);
//            }
//
//        }

        return map;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/status/checkStatusAPI/{lbId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public StatusChange changeStatusAPI(HttpServletRequest request,HttpServletResponse response, @PathVariable Long lbId, @RequestBody StatusChange statusChange) {
        log.info("StatusController.checkStatusAPI, lbid={}, status= {}", lbId, statusChange);
        LoadBalancer loadBalancer = loadBalancerController.findEntityById(lbId); //loadBalancerRepository.findOne(lbId);

        String statsPage = "http://"+loadBalancer.getHost()+":"+(loadBalancer.getStatsPort())+"/proxy-stats";
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(statsPage);

            List<NameValuePair> params = new ArrayList<NameValuePair>(3);
            params.add(new BasicNameValuePair("s", statusChange.s));
            params.add(new BasicNameValuePair("action",statusChange.action));
            params.add(new BasicNameValuePair("b","#"+statusChange.b));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse responseClient = httpclient.execute(httppost);
            response.setStatus(responseClient.getStatusLine().getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(503);
        }

        //Saving state
        String name = statusChange.s;
        String idStr = name.substring(name.lastIndexOf("_")+1);
        try{
            Long id = Long.parseLong(idStr);
            ApplicationInstance applicationInstance = applicationInstanceController.findEntityById(id);//applicationInstanceRepository.findOne(id);
            HaProxyState haProxyState = HaProxyState.valueOf(statusChange.action.toUpperCase());
            applicationInstance.setHaProxyStateValue(haProxyState);
        }catch(NumberFormatException e){
            e.printStackTrace();
        }
        return statusChange;
    }
}
