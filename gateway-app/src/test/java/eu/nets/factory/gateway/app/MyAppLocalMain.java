package eu.nets.factory.gateway.app;

import eu.nets.factory.gateway.web.GatewayDataJsGenerator;

public class MyAppLocalMain {
    public static void main(String[] args) throws Exception {
        GatewayDataJsGenerator.main(new String[0]);
        new MyAppMain().run();
    }
}
