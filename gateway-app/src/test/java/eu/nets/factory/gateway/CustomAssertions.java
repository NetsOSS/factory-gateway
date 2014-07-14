package eu.nets.factory.gateway;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.web.AppModel;
/*
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import eu.nets.factory.gateway.web.AppGroupModel;
import eu.nets.factory.gateway.web.AppInstModel;
import eu.nets.factory.gateway.web.LoadBalancerModel;
*/
import org.fest.assertions.Assertions;


public class CustomAssertions extends Assertions {

    public static ApplicationAssert assertThat(Application actual) {
        return new ApplicationAssert(actual);
    }
    /*
    public static ApplicationInstanceAssert assertThat(ApplicationInstance actual) {
        return new ApplicationInstanceAssert(actual);
    }

    public static ApplicationGroupAssert assertThat(ApplicationGroup actual) {
        return new ApplicationGroupAssert(actual);
    }

    public static LoadBalancerAssert assertThat(LoadBalancer actual) {
        return new LoadBalancerAssert(actual);
    }
    */
    public static AppModelAssert assertThat(AppModel actual) {
        return new AppModelAssert(actual);
    }
    /*
    public static AppInstModelAssert assertThat(AppInstModel actual) {
        return new AppInstModelAssert(actual);
    }

    public static AppGroupModelAssert assertThat(AppGroupModel actual) {
        return new AppGroupModelAssert(actual);
    }

    public static LoadBalancerModelAssert assertThat(LoadBalancerModel actual) {
        return new LoadBalancerModelAssert(actual);
    }
    */
}
