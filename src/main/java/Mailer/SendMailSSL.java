package Mailer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
public class SendMailSSL {
    private String emailAddress;
    private String password;
    private Properties props;
    private Session session;
    public SendMailSSL(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
        initProperties();
        initSession();

    }
    public SendMailSSL() {
        this.emailAddress = "harukakasugano31@gmail.com";
        this.password = "moimwcdzokheodrz";
        initProperties();
        initSession();
    }
    private void initProperties() {
        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
    }
    private void initSession() {
        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAddress, password);
            }
        });
    }
    public void  sendMail(String to, String subject, String body) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAddress));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
