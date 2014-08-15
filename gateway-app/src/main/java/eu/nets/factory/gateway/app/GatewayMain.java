package eu.nets.factory.gateway.app;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.GenericConfigurator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.nets.factory.gateway.web.WebConfig;
import eu.nets.oss.jetty.ContextPathConfig;
import eu.nets.oss.jetty.EmbeddedJettyBuilder;
import eu.nets.oss.jetty.EmbeddedSpringBuilder;
import eu.nets.oss.jetty.StaticConfig;
import eu.nets.oss.jetty.StdoutRedirect;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import static eu.nets.oss.jetty.EmbeddedJettyBuilder.isStartedWithAppassembler;
import static eu.nets.oss.jetty.EmbeddedSpringBuilder.createSpringContextLoader;
import static java.lang.Integer.parseInt;
import static org.slf4j.bridge.SLF4JBridgeHandler.install;
import static org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger;
import static org.springframework.core.io.support.PropertiesLoaderUtils.fillProperties;

public class GatewayMain {
    public static void main(String[] args) throws Exception {
        new GatewayMain().run();
    }

    public void run() throws Exception {
        installSlf4j();

        configureLogback();

        boolean onServer = isStartedWithAppassembler();

        Properties properties = loadProperties();

        int port = parseInt(properties.getProperty("port", "9002"));
        ContextPathConfig webAppSource = new StaticConfig("/", port);
        final EmbeddedJettyBuilder builder = new EmbeddedJettyBuilder(webAppSource, !onServer);

        if (onServer) {
            StdoutRedirect.tieSystemOutAndErrToLog();
            builder.addHttpAccessLogAtRoot();
        }

        WebApplicationContext spring = EmbeddedSpringBuilder.createApplicationContext("App", WebConfig.class);

        String env = properties.getProperty("environment");
        if (env == null) {
            System.err.println("Missing required property 'environment' in environment.properties.");
        }
        ConfigurableEnvironment environment = ((AnnotationConfigWebApplicationContext) spring).getEnvironment();
        environment.setActiveProfiles(env);
        environment.getPropertySources().addFirst(new PropertiesPropertySource("properties", properties));

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

    @SuppressFBWarnings("DM_EXIT")
    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        Resource envProperties = new ClassPathResource("/environment.properties");
        if (envProperties.isReadable()) {
            fillProperties(properties, envProperties);
        }

        Resource envPropertiesFile = new FileSystemResource("environment.properties");
        if (!envPropertiesFile.isReadable()) {
            System.err.println("Missing required file: " + envPropertiesFile.getFile().getAbsolutePath());
            System.exit(1);
        } else {
            fillProperties(properties, envPropertiesFile);
        }

        ClassPathResource classPathResource = new ClassPathResource("/build.properties");
        if (classPathResource.isReadable()) {
            fillProperties(properties, classPathResource);
        }

        return properties;
    }

    protected void installSlf4j() throws Exception {
        removeHandlersForRootLogger();
        install();
    }

    protected void configureLogback() throws Exception {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        GenericConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);

        File f = new File("logback.xml");

        if (f.exists()) {
            configurator.doConfigure(f);
        } else {
            throw new RuntimeException("Logback properties file: " + f + " not found");
        }
    }
}
