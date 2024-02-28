package digibridge;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
import javax.mail.search.SearchTerm;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SubjectTerm;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromStringTerm;
import javax.swing.DefaultListModel;

public class methods {
       public static ArrayList<String> newaccnts = new ArrayList<>();
       private static DefaultListModel<String> recordlist = new DefaultListModel<>();
       
    //Store to DB
public void storeUser(String email, String password) {
        try {
             // Generate a salt
            String salt = BCrypt.gensalt();

            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, salt);
            
            // Establish connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "");
            
            // Prepare SQL statement to insert data into the login_users table
            String sql = "INSERT INTO accounts_tbl (username, password, permission) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, hashedPassword);
            statement.setString(3, "0");

            // Execute the SQL statement
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Email and password inserted successfully!");
            }

            // Close resources
            statement.close();
            connection.close();
        } catch (SQLException e) {
            
        }
    }
    
    //Get email
public int getEmailCount(String email){
    int count = 0;
    try {
        // Establish connection to the database
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "");
        
        // Prepare SQL statement to count rows in accounts_tbl where username matches the given email
        String sql = "SELECT COUNT(*) FROM accounts_tbl WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);

        // Execute the SQL statement
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            count = resultSet.getInt(1);
        }
        // Close resources
        resultSet.close();
        statement.close();
        connection.close();
    } catch (SQLException e) {
        // Handle any SQL exceptions here
        e.printStackTrace();
    }
    return count;
    }
    
    //For log-in checks 
public boolean Login(String email, String password) {
    int count = 0; // Initialize count to 0
    String hashedPassword = null; // Initialize hashedPassword to null
    try {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "");

        // Prepare SQL statement to select the hashed password from the accounts_tbl for the provided username
        String sql = "SELECT password FROM accounts_tbl WHERE username = ? AND permission = 1";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            hashedPassword = resultSet.getString("password"); // Retrieve hashed password from the result set
            count = 1; // Increment count since username exists
        }
        
        // Close resources
        resultSet.close();
        statement.close();
        connection.close();
    } catch (SQLException e) {
        // Handle any SQL exceptions here
        e.printStackTrace();
        return false; // Return false if an exception occurs
    }
    
    // Check if the count is 1 and the provided password matches the hashed password
    return count == 1 && hashedPassword != null && BCrypt.checkpw(password, hashedPassword);
    }
    
    //FOR SENDING ACCESS REQUEST
 public static void sendAccessRequest(String AdminEmail, String RequesteeEmail) {
        String username = "pr0j3ct2722@gmail.com";
        String password = "pbpipeyypuyaopvr";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(AdminEmail)); // Set admin email as recipient

            // Set CC for both admin and requestee emails
            InternetAddress adminAddress = new InternetAddress(AdminEmail);
            InternetAddress requesteeAddress = new InternetAddress(RequesteeEmail);
            message.setRecipient(Message.RecipientType.CC, adminAddress);
            message.setRecipient(Message.RecipientType.CC, requesteeAddress);

            // Generate a unique message ID for each request
            String uniqueMessageID = java.util.UUID.randomUUID().toString() + "@" + "pr0j3ct2722.gmail.com";
            message.setHeader("Message-ID", uniqueMessageID);

            message.setSubject("New Account Access Request: Digibridge EMR");

            // Construct the message text
            String messageText = RequesteeEmail + " is asking for account access in Digibridge: EMR\nGrant access by replying \"YES\" to this message";
            message.setText(messageText);

            Transport.send(message);
            System.out.println("Access request email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


public static void processAccessRequestEmails(String AdminEmail) {
        String host = "imap.gmail.com";
        String username = "pr0j3ct2722@gmail.com"; // Replace with your Gmail address
        String password = "pbpipeyypuyaopvr"; // Replace with your app password
        String RequesteeEmail = "";
        String reply = "";

        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps"); // Using IMAPS for SSL/TLS
        properties.setProperty("mail.imap.ssl.enable", "true"); // Enable SSL
        properties.setProperty("mail.imaps.ssl.protocols", "TLSv1.2"); // Specify TLS version
        properties.setProperty("mail.imap.ssl.ciphersuites", "TLS_AES_128_GCM_SHA256"); // Specify cipher suites
        properties.setProperty("mail.imap.connectiontimeout", "30000"); // Connection timeout (30 seconds)
        properties.setProperty("mail.imap.timeout", "30000"); // Socket timeout (30 seconds)
        properties.setProperty("mail.imap.port", "993");

        try {
            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
            // Connect to the IMAP server
            Store store = session.getStore("imaps");
            store.connect(host, username, password);

            // Open the INBOX folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            // Create the search terms
            SearchTerm searchTerm1 = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            SearchTerm searchTerm2 = new SubjectTerm("Re: New Account Access Request: Digibridge EMR");
            SearchTerm searchTerm3 = new FromStringTerm(AdminEmail);
            SearchTerm searchTerm = new AndTerm(
            new AndTerm(searchTerm1, searchTerm2),
            searchTerm3
            );
            
            // Get messages in the INBOX
            Message[] messages = inbox.search(searchTerm);

            // Process each message
            for (Message message : messages) {
                // Extract the email address of the person who replied
                String replySender = message.getFrom()[0].toString();

                // Process the message
                Object content = message.getContent();
                if (content instanceof MimeMultipart) {
                    MimeMultipart multipart = (MimeMultipart) content;
                    for (int j = 0; j < 1; j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        reply = bodyPart.getContent().toString().toUpperCase().trim().replaceAll("\\s", "");
                    }
                }
                //Get address
                Address[] bccRecipients = message.getRecipients(Message.RecipientType.CC);
                if (bccRecipients != null && bccRecipients.length > 0) {
                    RequesteeEmail = bccRecipients[0].toString();
                }
                
                // Check if the content of the email contains "YES"
                if (reply.substring(0, 3).contains("YES") && replySender.contains(AdminEmail)) {
                    message.setFlag(Flags.Flag.SEEN, true);
                    acceptAccountRequest(RequesteeEmail);
                    newaccnts.add(RequesteeEmail);
                    System.out.println("Access Granted for: "+ newaccnts);
                }
              
            }
            // Close the inbox
            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

public static void acceptAccountRequest(String RequesteeEmail) {
    try {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "");
        
        // Check if the RequesteeEmail is already in the DB and permission is 0
        String selectSql = "SELECT COUNT(*) AS count FROM accounts_tbl WHERE username = ? AND permission = 0";
        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        selectStatement.setString(1, RequesteeEmail);
        ResultSet resultSet = selectStatement.executeQuery();
        int count = 0;
        if (resultSet.next()) {
            count = resultSet.getInt("count");
        }
        resultSet.close();
        selectStatement.close();
        
        // If count is greater than 0, update the permission to 1
        if (count > 0) {
            String updateSql = "UPDATE accounts_tbl SET permission = 1 WHERE username = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, RequesteeEmail);
            updateStatement.executeUpdate();
            updateStatement.close();
        } else {
            System.out.println(RequesteeEmail + " is either not in the database or already has permission.");
        }
        
        connection.close();
    } catch (SQLException e) {
        // Handle any SQL exceptions here
        e.printStackTrace();
    }
}

public void UpdatePass(String email, String password) {
        try {
             // Generate a salt
            String salt = BCrypt.gensalt();

            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, salt);
            
            // Establish connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "");
            
            // Prepare SQL statement to insert data into the login_users table
            String sql = "UPDATE accounts_tbl SET password = ? WHERE username = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, hashedPassword);
            statement.setString(2, email);

            // Execute the SQL statement
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Email and password inserted successfully!");
            }

            // Close resources
            statement.close();
            connection.close();
        } catch (SQLException e) {
            
        }
    }

        public DefaultListModel<String> fetchNamesFromDatabase() {
    DefaultListModel<String> recordlist = new DefaultListModel<>();
    try {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "");
        String selectSql = "SELECT concat(first_name, ', ', last_name) AS name FROM patients";
        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        ResultSet resultSet = selectStatement.executeQuery();
        while (resultSet.next()) {
            String patient = resultSet.getString("name");
            recordlist.addElement(patient);
        }

        // Close resources
        resultSet.close();
        selectStatement.close();
        connection.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return recordlist;
}
        
     public void storePatient(int patientID, String lastName, String firstName, int age, byte[] pdfData) {
        try {
            // Establish connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "");

            // Prepare SQL statement to insert data into the patients table
            String sql = "INSERT INTO patients (patient_id, last_name, first_name, age, pdf_data) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Set values for the parameters
            statement.setInt(1, patientID);
            statement.setString(2, lastName);
            statement.setString(3, firstName);
            statement.setInt(4, age);
            statement.setBytes(5, pdfData);

            // Execute the SQL statement
            statement.executeUpdate();
            
            // Close the statement and connection
            statement.close();
            connection.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL exceptions
        }
    }
     
     public static byte[] convertFileToByteArray(File file) throws IOException {
         
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }
        
}
