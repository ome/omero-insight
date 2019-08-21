package org.openmicroscopy.shoola.agents.fsimporter.mde.util;


//import java.util.*;
//import javax.mail.*;
//import javax.mail.internet.*;
//import javax.activation.*;



public class ErrorMail 
{
//	public void sentFileMail(String to, String from, String subject, String text, String attacheFile)
//	{
////		// Recipient's email ID needs to be mentioned.
////		String to = "abcd@gmail.com";
////
////		// Sender's email ID needs to be mentioned
////		String from = "web@gmail.com";
//
//		// Assuming you are sending email from localhost
//		String host = "localhost";
//
//		// Get system properties
//		Properties properties = System.getProperties();
//
//		// Setup mail server
//		properties.setProperty("mail.smtp.host", host);
//
//		// Get the default Session object.
//		Session session = Session.getDefaultInstance(properties);
//
//		try{
//			// Create a default MimeMessage object.
//			MimeMessage message = new MimeMessage(session);
//
//			// Set From: header field of the header.
//			message.setFrom(new InternetAddress(from));
//
//			// Set To: header field of the header.
//			message.addRecipient(Message.RecipientType.TO,
//					new InternetAddress(to));
//
//			// Set Subject: header field
//			message.setSubject(subject);
//
//			// Create the message part 
//			BodyPart messageBodyPart = new MimeBodyPart();
//
//			// Fill the message
//			messageBodyPart.setText(text);
//
//			// Create a multipar message
//			Multipart multipart = new MimeMultipart();
//
//			// Set text message part
//			multipart.addBodyPart(messageBodyPart);
//
//			// Part two is attachment
//			messageBodyPart = new MimeBodyPart();
//			String filename = attacheFile;
//			DataSource source = new FileDataSource(filename);
//			messageBodyPart.setDataHandler(new DataHandler(source));
//			messageBodyPart.setFileName(filename);
//			multipart.addBodyPart(messageBodyPart);
//
//			// Send the complete message parts
//			message.setContent(multipart );
//
//			// Send message
//			Transport.send(message);
//			System.out.println("Sent message successfully....");
//		}catch (MessagingException mex) {
//			mex.printStackTrace();
//		}
//	}
	

    /**
     * Send email using GMail SMTP server.
     *
     * @param username GMail username
     * @param password GMail password
     * @param recipientEmail TO recipient
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param title title of the message
     * @param message message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
//    public static void Send(final String username, final String password, String recipientEmail, String ccEmail, String title, String message) throws AddressException, MessagingException {
//        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
//
//        // Get a Properties object
//        Properties props = System.getProperties();
//        props.setProperty("mail.smtps.host", "smtp.gmail.com");
//        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
//        props.setProperty("mail.smtp.socketFactory.fallback", "false");
//        props.setProperty("mail.smtp.port", "465");
//        props.setProperty("mail.smtp.socketFactory.port", "465");
//        props.setProperty("mail.smtps.auth", "true");
//
//        /*
//        If set to false, the QUIT command is sent and the connection is immediately closed. If set 
//        to true (the default), causes the transport to wait for the response to the QUIT command.
//
//        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
//                http://forum.java.sun.com/thread.jspa?threadID=5205249
//                smtpsend.java - demo program from javamail
//        */
//        props.put("mail.smtps.quitwait", "false");
//
//        Session session = Session.getInstance(props, null);
//
//        // -- Create a new message --
//        final MimeMessage msg = new MimeMessage(session);
//
//        // -- Set the FROM and TO fields --
//        msg.setFrom(new InternetAddress(username + "@gmail.com"));
//        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
//
//        if (ccEmail.length() > 0) {
//            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
//        }
//
//        msg.setSubject(title);
//        msg.setText(message, "utf-8");
//        msg.setSentDate(new Date());
//
//        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");
//
//        t.connect("smtp.gmail.com", username, password);
//        t.sendMessage(msg, msg.getAllRecipients());      
//        t.close();
//    }
}
