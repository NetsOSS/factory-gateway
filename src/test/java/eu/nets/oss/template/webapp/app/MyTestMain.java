package eu.nets.oss.template.webapp.app;

public class MyTestMain extends MyMain {
    public static void main(String[] args) throws Exception {
        new MyTestMain().run("local");
    }

    @Override
    protected void installSlf4j() throws Exception {
        configureLogback("/local/logback.xml");
        super.installSlf4j();
    }
}
