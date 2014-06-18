package eu.nets.factory.gateway.web;

import eu.nets.factory.gateway.model.Application;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * Created by sleru on 18.06.2014.
 */
@Controller
public class ApplicationController {

    @RequestMapping(method = RequestMethod.GET, value = "/data/instances", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Application> listAllApps() {
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data/find", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Application findApp(String name) {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/data/find", consumes =APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Application ceateApp(@RequestBody AppModel appModel) {
        Application app = new Application(appModel.name);
        return null;
    }

    public static class AppModel {

        public Long id;
        public String name;
        public Application app;

        public AppModel(Application app) {
            this.app = app;
        }

        public AppModel(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
