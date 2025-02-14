package Student;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
//import javax.mail.*;
//import javax.mail.internet.*;

public class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "courstechnologies@gmail.com"; // Change this
    private static final String SMTP_PASSWORD = "ptoo pguy gdph vyjh";   // Change this (Use App Password)

    public static void sendEmail(String to, String subject, String body) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ Email sent successfully to " + to);
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}






//
//package Student;
//import jakarta.mail.*;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeMessage;
//
//
////import javax.mail.*;
////import javax.mail.internet.*;
//import java.util.Properties;
//
//
//    public class EmailSender {
//
//        public static void sendEnrollmentEmail(String toEmail, String studentName, String courseName, String paymentMethod, String transactionId) {
//            final String fromEmail = "courstechnologies@gmail.com";  // Your Gmail ID
//            //   final String emailPassword = "lcje dvqg lmxb xcmz"; // Use Google App Password
//            final String emailPassword = "ptoo pguy gdph vyjh"; // Use Google App Password
//
//            // SMTP Server Configuration
//            Properties props = new Properties();
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.port", "587");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//
//            // Authenticate
//            Session session = Session.getInstance(props, new Authenticator() {
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    // return new PasswordAuthentication("anilnaikof@gmail.com", "lcje dvqg lmxb xcmz");
//                    return new PasswordAuthentication("courstechnologies@gmail.com", "ptoo pguy gdph vyjh");
//
//                }
//            });
//
//            try {
//                // Create Message
//                Message message = new MimeMessage(session);
//                message.setFrom(new InternetAddress(fromEmail));
//                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//                message.setSubject("✅ Course Enrollment Confirmation - " + courseName);
//
//                // Email Content
////                String emailContent = "<h2>Dear " + studentName + ",</h2>"
////                        + "<p>You have successfully enrolled in <strong>" + courseName + "</strong>.</p>"
////                        + "<p><strong>Payment Method:</strong> " + paymentMethod + "</p>"
////                        + "<p><strong>Transaction ID:</strong> " + transactionId + "</p>"
////                        + "<br><p>Thank you for enrolling! 🎉</p>";
////                String emailContent = "<h2>✅ Enrollment Confirmation</h2>"
////                        + "<p>Dear <b>" + studentName + "</b>,</p>"
////                        + "<p><b>You have successfully enrolled in:</b></p>"
////                        + "<h3 style='color:blue;'>" + courseName + "</h3>"
////                        + "<p><b>Payment Method:</b> " + paymentMethod + "</p>"
////                        + "<p><b>Transaction ID:</b> " + transactionId + "</p>"
////                        + "<br><p>Thank you for enrolling! 🎓</p>";
////
////                message.setContent(emailContent, "text/html");
//                String emailContent = "<h2> Enrollment Confirmation</h2>"
//                        + "<p>Dear <b>" + studentName + "</b>,</p>"
//                        + "<p>You have successfully enrolled in <b>" + courseName + "</b>.</p>"
//                        + "<p><b>Payment Method:</b> " + paymentMethod + "</p>"
//                        + "<p><b>Transaction ID:</b> " + transactionId + "</p>"
//                        + "<br><p>Thank you for enrolling! </p>";
//
//                message.setContent(emailContent, "text/html");
//
//                // Send Email
//                Transport.send(message);
//                System.out.println("📧 Enrollment details sent to " + toEmail);
//            } catch (MessagingException e) {
//                System.out.println("❌ Error sending email: " + e.getMessage());
//            }
//        }
//    }
//
//
//
//
//

