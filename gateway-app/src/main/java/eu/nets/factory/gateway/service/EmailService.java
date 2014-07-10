package eu.nets.factory.gateway.service;


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

    private final Logger log = getLogger(getClass());

    public void sendEmail() {
        log.info("EmailService.sendEmail");
        email1();
    }

    public void email1() {
        log.info("\temailService. Not implemented yet ");

        JavaMailSender mailSender = mailConfig.javaMailService();

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            mimeMessage.setFrom("ofbje@nets.eu");
            mimeMessage.addRecipients(Message.RecipientType.TO, "ofbje@nets.eu");
            mimeMessage.setSubject("Test");
            mimeMessage.setText("Test yo!");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void email2() {

        // Recipient's email ID needs to be mentioned.
        String to = "ofbje@nets.eu";

        // Sender's email ID needs to be mentioned
        String from = "localhost";

        // Assuming you are sending email from localhost
        String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Now set the actual message
            message.setText("This is actual message");

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public void email3() {

        String message = "Hello world";
        String subject = "MySubject";

        Process p=null;
        try {
             p = Runtime.getRuntime().exec(new String[]{"/usr/bin/mail", "-s", subject, "ofbje@nets.eu"});
            OutputStream out = p.getOutputStream();
            ByteArrayInputStream input = new ByteArrayInputStream(message.getBytes("ISO-8859-1"));
            IOUtils.copy(input, out);


        } catch (Exception e) {
            e.printStackTrace();
        }
        if(p==null)
            return;

        while (p.isAlive()) {
            try {
                Thread.sleep(1000);
                log.info("Waiting for mail...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
