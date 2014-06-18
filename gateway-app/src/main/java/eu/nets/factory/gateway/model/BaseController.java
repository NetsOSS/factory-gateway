package eu.nets.factory.gateway.model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by sleru on 18.06.2014.
 */
@Controller
public class BaseController {


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView hello() {
        return new ModelAndView("index.html");
    }
}
