package eu.nets.factory.gateway.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Transactional
public class ScenarioController {

    private final Logger log = getLogger(getClass());

    @RequestMapping(method = GET, value = "/data/scenario", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ScenarioModel> getScenarios() {
        List<ScenarioModel> scenarios = new ArrayList<>();
        return scenarios;
    }

    public static class ScenarioModel {
    }
}
