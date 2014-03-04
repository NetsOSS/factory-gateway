package eu.nets.oss.template.webapp.app;

import java.io.File;
import java.net.URL;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.GenericConfigurator;
import eu.nets.oss.jetty.ContextPathConfig;
import eu.nets.oss.jetty.EmbeddedJettyBuilder;
import eu.nets.oss.jetty.EmbeddedSpringBuilder;
import eu.nets.oss.jetty.PropertiesFileConfig;
import eu.nets.oss.jetty.StaticConfig;
import eu.nets.oss.jetty.StdoutRedirect;
import eu.nets.oss.template.webapp.web.WebConfig;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import static eu.nets.oss.jetty.EmbeddedJettyBuilder.isStartedWithAppassembler;
import static eu.nets.oss.jetty.EmbeddedSpringBuilder.createSpringContextLoader;
import static org.slf4j.bridge.SLF4JBridgeHandler.install;
import static org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger;

public class MyMain {
    /**
     * This requires that the environment name is passed in as an argument. Normally Nets applications will detect
     * this from a file on disk.
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: bin/startapp <env>");
            System.exit(0);
        }

        new MyMain().run(args[0]);
    }

    public void run(String env) throws Exception {
        installSlf4j();

        configureLogback(env);

        boolean onServer = isStartedWithAppassembler();

        ContextPathConfig webAppSource = onServer ? new PropertiesFileConfig() : new StaticConfig("/", 8080);
        final EmbeddedJettyBuilder builder = new EmbeddedJettyBuilder(webAppSource, !onServer);

        if (onServer) {
            StdoutRedirect.tieSystemOutAndErrToLog();
            builder.addHttpAccessLogAtRoot();
        }

        WebApplicationContext spring = EmbeddedSpringBuilder.createApplicationContext("My app", WebConfig.class);
        ((AnnotationConfigWebApplicationContext) spring).getEnvironment().setActiveProfiles(env);
        ContextLoaderListener springContextLoader = createSpringContextLoader(spring);

        EmbeddedJettyBuilder.ServletContextHandlerBuilder ctx = builder.createRootServletContextHandler("");
        ctx.addEventListener(springContextLoader);
        ctx.addServlet(new DispatcherServlet(spring)).mountAtPath("/*");

        try {
            builder.startJetty();
            builder.verifyServerStartup();
        } catch (Exception e) {
            System.err.println("Unable to start application");
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected void installSlf4j() throws Exception {
        removeHandlersForRootLogger();
        install();
    }

    protected void configureLogback(String env) throws Exception {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        GenericConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);

        File f = new File("properties/" + env, "logback.xml");
        System.out.println("f = " + f);

        if (f.exists()) {
            configurator.doConfigure(f);
        } else {
            if (!env.equals("local")) {
                System.err.println("Only 'local' env is allowed to read log config from classpath");
                System.exit(1);
            }

            URL url = getClass().getResource(env + "/logback.xml");
            configurator.doConfigure(url);
        }
    }
}
