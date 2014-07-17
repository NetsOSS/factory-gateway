package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitTestClass {

    @Autowired
    LoadBalancerController loadBalancerController;

    @Autowired
    ApplicationController applicationController;

    @Autowired
    ApplicationGroupController applicationGroupController;

    @Autowired
    ApplicationInstanceController applicationInstanceController;

    public void init() {

        //LoadBalancers
        LoadBalancer loadBalancerOne = new LoadBalancer("Per", "hostOne", "/instPathOne", "sshOne", 123);
        LoadBalancer loadBalancerTwo = new LoadBalancer("Knut", "hostTwo", "/instPathTwo", "sshTwo", 234);
        LoadBalancer loadBalancerThree = new LoadBalancer("Hans", "hostTwo", "/instPathThree", "sshThree", 345);
        LoadBalancerModel loadModelOne = new LoadBalancerModel(loadBalancerOne);
        LoadBalancerModel loadModelTwo = new LoadBalancerModel(loadBalancerTwo);
        LoadBalancerModel loadModelThree = new LoadBalancerModel(loadBalancerThree);

        loadModelOne = loadBalancerController.create(loadModelOne);
        loadModelTwo = loadBalancerController.create(loadModelTwo);
        loadModelThree = loadBalancerController.create(loadModelThree);


        //ApplicationGroups
        ApplicationGroup groupOne = new ApplicationGroup("GroupOne");
        ApplicationGroup groupTwo = new ApplicationGroup("GroupTwo");
        ApplicationGroup groupThree = new ApplicationGroup("GroupThree");
        AppGroupModel groupModelOne = new AppGroupModel(groupOne);
        AppGroupModel groupModelTwo = new AppGroupModel(groupTwo);
        AppGroupModel groupModelThree = new AppGroupModel(groupThree);

        groupModelOne = applicationGroupController.create(groupModelOne);
        groupModelTwo = applicationGroupController.create(groupModelTwo);
        groupModelThree = applicationGroupController.create(groupModelThree);

        //Applications
        Application applicationOne = new Application("Kamino", "/kamino", applicationGroupController.findEntityById(groupModelOne.getId()), "mailTwo", "/kamino/v1/ping");
        Application applicationTwo = new Application("Grandiosa", "/grandiosa", applicationGroupController.findEntityById(groupModelTwo.getId()), "mailTwo", "/grandiosa/ping");
        Application applicationThree = new Application("Alpha", "/alpha", applicationGroupController.findEntityById(groupModelTwo.getId()),"mailOne", "/alpha/ping");
        AppModel appModelOne = new AppModel(applicationOne);
        AppModel appModelTwo = new AppModel(applicationTwo);
        AppModel appModelThree = new AppModel(applicationThree);
        appModelOne.applicationGroupId = groupModelOne.getId(); //
        appModelTwo.applicationGroupId = groupModelTwo.getId();
        appModelThree.applicationGroupId = groupModelTwo.getId();

        appModelOne = applicationController.create(appModelOne);
        appModelTwo = applicationController.create(appModelTwo);
        appModelThree = applicationController.create(appModelThree);

        //ApplicationInstances
        ApplicationInstance appInstOne = new ApplicationInstance("Kamino1.0", "hostOne", 8080, "/kamino/1.0", applicationController.findEntityById(appModelOne.getId()));
        ApplicationInstance appInstTwo = new ApplicationInstance("Grandiosa1.0", "hostTwo", 8080, "/grandiosa/1.0", applicationController.findEntityById(appModelTwo.getId()));
        ApplicationInstance appInstThree = new ApplicationInstance("Alpha1.0", "hostThree", 8080, "/alpha/1.0", applicationController.findEntityById(appModelTwo.getId()));
        AppInstModel instModelOne = new AppInstModel(appInstOne);
        AppInstModel instModelTwo = new AppInstModel(appInstTwo);
        AppInstModel instModelThree = new AppInstModel(appInstThree);

        applicationInstanceController.create(appModelOne.getId(), instModelOne);
        applicationInstanceController.create(appModelTwo.getId(), instModelTwo);
        applicationInstanceController.create(appModelTwo.getId(), instModelThree);

        //Adding loadBalancer to Application
        loadBalancerController.addApplication(loadModelOne.id, appModelOne.getId());
        loadBalancerController.addApplication(loadModelTwo.id, appModelTwo.getId());
        loadBalancerController.addApplication(loadModelTwo.id, appModelThree.getId());
        loadBalancerController.addApplication(loadModelThree.id, appModelTwo.getId());
    }
}
