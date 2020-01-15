package rms.alert.metrics.alert.classes;

import org.springframework.core.env.Environment;
import rms.alert.metrics.alert.beans.SMTPContent;
import rms.alert.metrics.alert.configs.SMTPConfig;
import rms.alert.utils.datetime.DateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Properties;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

// Ref of AtomicBoolean: https://www.tutorialspoint.com/java_concurrency/concurrency_atomicboolean.htm

@Component
public class SMTP extends TimerTask {

	private Message message;
	private AtomicBoolean isUsing = new AtomicBoolean(false);
	private Vector<SMTPContent> contentArray = new Vector<SMTPContent>();
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private SMTPConfig smtpConfig;

	@Autowired
	private Environment environment;

	private String getConfigPath() {
		try {
			return environment.getProperty("app.path").trim();
		} catch (Exception e) {
			return ".";
		}
	}

	@Bean
	private void loadSMTPMessage() {
		Properties gmailProp = new Properties();
		gmailProp.put("mail.smtp.host", smtpConfig.getSmtpHost());
		gmailProp.put("mail.smtp.port", smtpConfig.getSmtpPort());
		gmailProp.put("mail.smtp.auth", "true");
		gmailProp.put("mail.smtp.starttls.enable", "true");

//		Session session = Session.getDefaultInstance(gmailProp);
		Session session = Session.getInstance(gmailProp, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpConfig.getSmtpFromEmail(), smtpConfig.getSmtpFromPassword());
			}
		});
		message = new MimeMessage(session);
	}

	public String getWaitTime() {
		return smtpConfig.getSmtpSendWaitTime();
	}

	@Override
	public void run() {
		this.autoSendMessage();
	}

	public void addAlertContent(String title, String description, String imgFileName) {
		while (true) {
			if (isUsing.compareAndSet(false, true)) {
				break;
			}
		}
		
		String htmlContent;
		SMTPContent content;

		if (!imgFileName.equals("default")) {
			htmlContent = String.format(smtpConfig.getSmtpHtmlImageDesign(), title, DateTime.getCurrentDateTime(), description, imgFileName);
			File imgFile = new File(getConfigPath() + "/conf/image/" + imgFileName + ".jpeg");
			content = new SMTPContent(htmlContent, imgFileName, imgFile);
		}
		else {
			if (title.contains("TET")){
				htmlContent = String.format(smtpConfig.getSmtpHtmlDesign(), title, DateTime.getCurrentDateTime(), description);
				htmlContent = htmlContent.replace("white"," #FFE9EC");
				htmlContent = htmlContent.replace("black","#ff4a63");
				content = new SMTPContent(htmlContent, imgFileName, null);
			}
			else{
				htmlContent = String.format(smtpConfig.getSmtpHtmlDesign(), title, DateTime.getCurrentDateTime(), description);
				content = new SMTPContent(htmlContent, imgFileName, null);
			}
		}
		contentArray.add(content);
		isUsing.set(false);
	}

	public void sendMessage(MimeMultipart multipart) {
		try {
			String[] toAddressList = smtpConfig.getSmtpTo().trim().split(",");
			InternetAddress[] mailAddressTo = new InternetAddress[toAddressList.length];
			for (int i=0; i<toAddressList.length; ++i) {
				mailAddressTo[i] = new InternetAddress(toAddressList[i]);
			}

			message.setFrom(new InternetAddress(smtpConfig.getSmtpFromEmail(), smtpConfig.getSmtpFromName()));
			message.setRecipients(Message.RecipientType.TO, mailAddressTo);
			message.setSubject(smtpConfig.getSmtpSubject());
			message.setContent(multipart);
			
			Transport.send(message);
			logger.info("Sending message successfully");

			contentArray.clear();
		} catch (MessagingException | UnsupportedEncodingException e) {
			logger.error("Sending message fail - {}", e.getMessage());
		} finally {
			isUsing.set(false);
		}
	}

	private void autoSendMessage() {
		while (true) {
			if (isUsing.compareAndSet(false, true)) {
				break;
			}
		}
		int contArrSize = contentArray.size();
		if (contArrSize == 0) {
			isUsing.set(false);
			logger.info("Empty content for sending message");
			return;
		}

		String emailContent = "";
		for (SMTPContent ele : contentArray) {
			emailContent += ele.getHtmlContent();
		}

		MimeMultipart multipart = null;
		try {
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(emailContent, "text/html");

			multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			for (SMTPContent ele : contentArray) {
				if (!ele.getImgFileName().equals("default")){
					MimeBodyPart imagePart = new MimeBodyPart();
					imagePart.setHeader("Content-ID", "<" + ele.getImgFileName() + ">");
					imagePart.setDisposition(MimeBodyPart.INLINE);
					imagePart.attachFile(ele.getImgFile());
					multipart.addBodyPart(imagePart);
				}
			}
		} 
		catch (MessagingException | IOException e) {
			logger.error("Exception of multipart - {}", e.getMessage());
			isUsing.set(false);
			return;
		} 
		finally {
			if (multipart == null) {
				logger.error("Null multipart");
				isUsing.set(false);
				return;
			}
			
			this.sendMessage(multipart);
		}
	}

}
