package org.ihtsdo.otf.ts.services.handlers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.LocalException;

/**
 * Handles exceptions.
 */
public class ExceptionHandler {

  /** Date format */
  public final static SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a");

  /**
   * Handle exception.
   *
   * @param e the e
   * @param whatIsHappening the what is happening
   * @throws Exception the web application exception
   */
  public static void handleException(Exception e, String whatIsHappening)
    throws Exception {
    handleException(e, whatIsHappening, "");
  }

  /**
   * Handle exception. For {@link LocalException} print the stack trace and
   * inform the user with a message generated by the application. For all other
   * exceptions, also send email to administrators with the message and the
   * stack trace.
   *
   * @param e the e
   * @param whatIsHappening the what is happening
   * @param userName the current user
   * @throws Exception the web application exception
   */
  public static void handleException(Exception e, String whatIsHappening,
    String userName) throws Exception {

    e.printStackTrace();
    if (e instanceof LocalException) {
      throw e;
    }
    if (e instanceof WebApplicationException) {
      throw (WebApplicationException) e;
    }

    try {
      Properties config = ConfigUtility.getConfigProperties();
      String subject = "Terminology Server Error Report";
      String from = config.getProperty("mail.smtp.user");
      String recipients = config.getProperty("mail.smtp.to");

      // Bail if no recipients
      if (recipients == null || recipients.isEmpty()) {
        return;
      }

      Properties props = new Properties();
      props.put("mail.smtp.user", config.getProperty("mail.smtp.user"));
      props.put("mail.smtp.password", config.getProperty("mail.smtp.password"));
      props.put("mail.smtp.host", config.getProperty("mail.smtp.host"));
      props.put("mail.smtp.port", config.getProperty("mail.smtp.port"));
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.auth", "true");

      StringBuilder body = new StringBuilder();
      if (!(e instanceof LocalException))
        body.append(
            "Unexpected error trying to " + whatIsHappening
                + ". Please contact the administrator.").append("\n\n");

      try {
        body.append("HOST: " + InetAddress.getLocalHost().getHostName())
            .append("\n");
      } catch (UnknownHostException e1) {
        body.append("HOST:  unable to determine");
      }
      body.append("TIME: " + df.format(new Date())).append("\n");
      body.append("USER: " + userName).append("\n");

      body.append("MESSAGE: " + e.getMessage()).append("\n\n");
      StringWriter out = new StringWriter();
      PrintWriter pw = new PrintWriter(out);
      e.printStackTrace(pw);
      body.append(out.getBuffer());
      Logger.getLogger(ExceptionHandler.class).info(
          "Sending error email : " + props);
      if (config.getProperty("mail.enabled") != null
          && config.getProperty("mail.enabled").equals("true")) {
        sendEmail(subject, from, recipients, body.toString(), props,
        		"true".equals(props.get("mail.smtp.auth")));   
      } else {
        Logger.getLogger(ExceptionHandler.class).info(
            "Sending mail is disabled.");
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      Logger.getLogger(ExceptionHandler.class).error(
          "Unable to handle exception");
    }

  }

  /**
   * Sends email.
   *
   * @param subject the subject
   * @param from the from
   * @param recipients the recipients
   * @param body the body
   * @param details the details
   * @param authFlag the auth flag
   * @throws Exception the exception
   */
  public static void sendEmail(String subject, String from, String recipients,
    String body, Properties details, boolean authFlag) throws Exception {
    Session session = null;
    if (authFlag) {
	  Authenticator auth = new SMTPAuthenticator();
	  session = Session.getInstance(details, auth);
	} else {
	  session = Session.getInstance(details);
	}

    MimeMessage msg = new MimeMessage(session);
    msg.setText(body.toString());
    msg.setSubject(subject);
    msg.setFrom(new InternetAddress(from));
    String[] recipientsArray = recipients.split(";");
    for (String recipient : recipientsArray) {
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
    }
    Transport.send(msg);
  }

  /**
   * SMTPAuthenticator.
   */
  public static class SMTPAuthenticator extends javax.mail.Authenticator {

    /**
     * Instantiates an empty {@link SMTPAuthenticator}.
     */
    public SMTPAuthenticator() {
      // do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.mail.Authenticator#getPasswordAuthentication()
     */
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
      Properties config = null;
      try {
        config = ConfigUtility.getConfigProperties();
      } catch (Exception e) {
        // do nothing
      }
      if (config == null) {
        return null;
      } else {
        return new PasswordAuthentication(config.getProperty("mail.smtp.user"),
            config.getProperty("mail.smtp.password"));
      }
    }
  }

}
