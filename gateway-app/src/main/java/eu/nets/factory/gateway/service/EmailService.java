package eu.nets.factory.gateway.service;


import eu.nets.factory.gateway.model.GatewaySettings;
import org.h2.util.IOUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class EmailService {

    @Autowired
    MailConfig mailConfig;

    @Autowired
    private GatewaySettings settings;

    private final Logger log = getLogger(getClass());

    public boolean sendEmail(String to, String subject, String message) {
        if(settings.isLocal()){
            log.info("EmailService.sendEmail : Cannot send on localhost");
            return false;
        }


        log.info("EmailService.sendEmail to:{} , subject:{} , msg: {}",to,subject,message);
        JavaMailSender mailSender = mailConfig.javaMailService();

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            mimeMessage.setFrom("haproxy@nets.eu");
            mimeMessage.addRecipients(Message.RecipientType.TO, to);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
