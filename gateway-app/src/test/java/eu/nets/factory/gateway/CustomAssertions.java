package eu.nets.factory.gateway;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.web.AppModel;
import org.fest.assertions.Assertions;

public class CustomAssertions extends Assertions {

    public static ApplicationAssert assertThat(Application actual) {
        return new ApplicationAssert(actual);
    }
    public static AppModelAssert assertThat(AppModel actual) {
        return new AppModelAssert(actual);
    }
}
