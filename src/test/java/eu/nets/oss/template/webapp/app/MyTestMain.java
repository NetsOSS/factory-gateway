package eu.nets.oss.template.webapp.app;

public class MyTestMain extends MyMain {
    public static void main(String[] args) throws Exception {
        new MyTestMain().run();
    }

    @Override
    protected void setupLogging() throws Exception {
        configureLogback("/local/logback.xml");
        super.setupLogging();
    }
}
