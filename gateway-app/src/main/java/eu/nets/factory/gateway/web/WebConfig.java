package eu.nets.factory.gateway.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nets.factory.gateway.model.GatewaySettings;
import eu.nets.factory.gateway.service.MailConfig;
import eu.nets.factory.gateway.service.ServiceConfig;
import eu.nets.factory.gateway.web.jackson.GatewayObjectMapper;
import java.nio.charset.Charset;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ComponentScan(basePackageClasses = WebConfig.class)
@Import({eu.nets.factory.gateway.model.ModelConfig.class, ServiceConfig.class, MailConfig.class})
@EnableTransactionManagement
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private GatewaySettings settings;

    private static MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new GatewayObjectMapper();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        converter.setPrettyPrint(true);

        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
        converters.add(new StringHttpMessageConverter(Charset.forName("utf-8")));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String webLocation = "classpath:/web/";
        if (settings.loadResourcesFromDisk()) {
            webLocation = "file:src/main/resources/web/";
        }
        registry.addResourceHandler("/web/**").addResourceLocations(webLocation).setCachePeriod(0);
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver r = new InternalResourceViewResolver();
        r.setViewClass(InternalResourceView.class);
        r.setPrefix("/web/");
        return r;
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType("json", MediaType.APPLICATION_JSON).
                mediaType("xml", MediaType.APPLICATION_XML);
    }
}
