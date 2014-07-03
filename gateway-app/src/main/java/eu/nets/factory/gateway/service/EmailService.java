package eu.nets.factory.gateway.service;


import org.slf4j.Logger;
import org.springframework.stereotype.Service;


import static org.slf4j.LoggerFactory.getLogger;

@Service
public class EmailService {
    private final Logger log = getLogger(getClass());

    public void sendEmail(){
        log.info("EmailService.sendEmail");
        log.info("\temailService. Not implemented yet ");


    }
}
