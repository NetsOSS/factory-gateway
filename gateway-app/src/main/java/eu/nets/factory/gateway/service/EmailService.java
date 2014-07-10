package eu.nets.factory.gateway.service;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class EmailService {

    @Autowired
    MailConfig mailConfig;

    private final Logger log = getLogger(getClass());

    public void sendEmail() {
        log.info("EmailService.sendEmail");
        log.info("\temailService. Not implemented yet ");

        JavaMailSender mailSender = mailConfig.javaMailService();

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            mimeMessage.setFrom("ofbje@nets.eu");
            mimeMessage.addRecipients(Message.RecipientType.TO, "ogamm@nets.eu");
            mimeMessage.setSubject("Test");
            mimeMessage.setText("Test yo!");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
