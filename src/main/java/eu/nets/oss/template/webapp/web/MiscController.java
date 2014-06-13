package eu.nets.oss.template.webapp.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MiscController {

    @RequestMapping("/")
    public ModelAndView hello() {
        return new ModelAndView("index.html");
    }
}
