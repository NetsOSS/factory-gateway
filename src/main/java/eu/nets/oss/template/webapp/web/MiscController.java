package eu.nets.oss.template.webapp.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MiscController {

    @RequestMapping("/")
    public String hello(HttpServletRequest request) {
        System.out.println(request.getServletPath());
        return "wat.html";
    }
}
