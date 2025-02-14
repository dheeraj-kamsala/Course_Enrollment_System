package Student;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSenderStudent {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "courstechnologies@gmail.com"; // Replace with your Gmail
    private static final String SENDER_PASSWORD = "ptoo pguy gdph vyjh"; // Replace with App Password

    public static void sendVerificationEmail(String recipientEmail, String otp) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Admin Registration - OTP Verification");
            message.setText("Your OTP for registration is: " + otp + "\n\nPlease enter this OTP to complete your registration.");

            Transport.send(message);
            System.out.println("📧 OTP sent to " + recipientEmail);

        } catch (MessagingException e) {
            System.out.println("❌ Error sending email: " + e.getMessage());
        }
    }
}