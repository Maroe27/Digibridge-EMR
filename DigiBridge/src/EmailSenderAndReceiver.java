import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSenderAndReceiver {
    public static void main(String[] args) {
        final String senderEmail = "pr0j3ct2722@gmail.com";
        final String senderPassword = "esdummhnjxzrooae";
        String recipientEmail = "maroebacungan27@gmail.com";

        // Email configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Explicitly set TLSv1.2
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

        try {
            // Send the email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Digibridge EMR: New Account Access Request");
            message.setText("Account Access Request for " + recipientEmail + ". Reply YES IF ALLOW ACCOUNT ACCESS");
            Transport.send(message);
            System.out.println("Email sent successfully!");

            // Wait for response
            Thread.sleep(20000); // Wait for 10 seconds
            checkForResponse(session, senderEmail, senderPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private static String checkForResponse(Session session, String senderEmail, String senderPassword) throws Exception {
    Properties props = new Properties();
    props.setProperty("mail.imaps.ssl.protocols", "TLSv1.2"); // Set SSL/TLS protocol explicitly to TLSv1.2 for IMAPS
    props.setProperty("mail.imaps.ssl.enable", "true");

    Store store = session.getStore("imaps");
    store.connect("imap.gmail.com", 993, senderEmail, senderPassword);

    Folder inbox = store.getFolder("INBOX");
    inbox.open(Folder.READ_ONLY);

    Message[] messages = inbox.getMessages();
    for (Message msg : messages) {
        if (msg.getSubject().equals("Digibridge EMR: New Account Access Request")) {
            Object content = msg.getContent();
            if (content instanceof String) {
                return ((String) content).trim().toLowerCase();
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.getContentType().startsWith("text/plain")) {
                        System.out.println( bodyPart.getContent().toString().trim().toLowerCase());
                        return bodyPart.getContent().toString().trim().toLowerCase();
                    }
                }
            }
        }
    }

    inbox.close(false);
    store.close();

    return null; // Return null if no response is found
}



}
