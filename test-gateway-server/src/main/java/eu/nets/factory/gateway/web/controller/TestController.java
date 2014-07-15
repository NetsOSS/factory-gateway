package eu.nets.factory.gateway.web.controller;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
public class TestController {

    private final Logger log = getLogger(getClass());

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Map<String, String>> dumpRequest(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, Map<String, String>> returnMap = new HashMap<>();
        Map<String, String> headerMap = new HashMap<>();

        // Loop through header fields
        Enumeration headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String currentHeaderName = (String) headerNames.nextElement();
            headerMap.put(currentHeaderName, request.getHeader(currentHeaderName));
            log.debug(currentHeaderName + ": " + headerMap.get(currentHeaderName));
        }

        // Get other info
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("Server-IP", request.getLocalAddr());

        int requestCount = 0;
        if (request.getSession().getAttribute("Request-Count") != null) {
            requestCount = (int) request.getSession().getAttribute("Request-Count");
        }
        request.getSession().setAttribute("Request-Count", ++requestCount);
        contextMap.put("Request-Count", String.valueOf(requestCount));

        returnMap.put("Headers", headerMap);
        returnMap.put("Context", contextMap);
        return returnMap;
    }

    @RequestMapping(value = "/session", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public String createSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session.getId();
    }

    @RequestMapping(value = "/session", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();
    }
}
