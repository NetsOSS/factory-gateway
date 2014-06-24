package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

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
        LoadBalancer loadBalancerOne = new LoadBalancer("Per", "hostOne", "instPathOne", "sshOne");
        LoadBalancer loadBalancerTwo = new LoadBalancer("Knut", "hostOne", "instPathOne", "sshOne");
        LoadBalancer loadBalancerThree = new LoadBalancer("Hans", "hostOne", "instPathOne", "sshOne");
        LoadBalancerModel loadModelOne = new LoadBalancerModel(loadBalancerOne);
        LoadBalancerModel loadModelTwo = new LoadBalancerModel(loadBalancerTwo);
        LoadBalancerModel loadModelThree = new LoadBalancerModel(loadBalancerThree);

        loadBalancerController.create(loadModelOne);
        loadBalancerController.create(loadModelTwo);
        loadBalancerController.create(loadModelThree);


        //ApplicationGroups
        ApplicationGroup groupOne = new ApplicationGroup("GroupOne");
        ApplicationGroup groupTwo = new ApplicationGroup("GroupTwo");
        ApplicationGroup groupThree = new ApplicationGroup("GroupThree");
        AppGroupModel groupModelOne = new AppGroupModel(groupOne);
        AppGroupModel groupModelTwo = new AppGroupModel(groupTwo);
        AppGroupModel groupModelThree = new AppGroupModel(groupThree);

        applicationGroupController.create(groupModelOne);
        applicationGroupController.create(groupModelTwo);
        applicationGroupController.create(groupModelThree);

        //Applications
        Application applicationOne = new Application("Kamino", "www.kamino.no", groupOne);
        Application applicationTwo = new Application("Grandiosa", "www.grandiosa.no", groupTwo);
        Application applicationThree = new Application("Alpha", "www.alpha.no", groupThree);
        AppModel appModelOne = new AppModel(applicationOne);
        AppModel appModelTwo = new AppModel(applicationTwo);
        AppModel appModelThree = new AppModel(applicationThree);

        applicationController.create(appModelOne);
        applicationController.create(appModelTwo);
        applicationController.create(appModelThree);


        //ApplicationInstances
        ApplicationInstance appInstOne = new ApplicationInstance("Kamino 1.0", "hostOne", 8080, "www.kamino.no/1.0", applicationOne);
        ApplicationInstance appInstTwo = new ApplicationInstance("Grandiosa 1.0", "hostTwo", 8080, "www.grandiosa.no/1.0", applicationTwo);
        ApplicationInstance appInstThree = new ApplicationInstance("Alpha 1.0", "hostThree", 8080, "www.alpha.no/1.0", applicationThree);
        AppInstModel instModelOne = new AppInstModel(appInstOne);
        AppInstModel instModelTwo = new AppInstModel(appInstTwo);
        AppInstModel instModelthree = new AppInstModel(appInstThree);

        applicationInstanceController.create(appInstOne.getApplication().getId(), instModelOne);
        applicationInstanceController.create(appInstTwo.getApplication().getId(), instModelTwo);
        applicationInstanceController.create(appInstThree.getApplication().getId(), instModelthree);

        //Adding loadBalancer to Application
        loadBalancerController.addApplication(loadBalancerOne.getId(), applicationOne);
        loadBalancerController.addApplication(loadBalancerTwo.getId(), applicationTwo);
        loadBalancerController.addApplication(loadBalancerThree.getId(), applicationThree);



    }
}
