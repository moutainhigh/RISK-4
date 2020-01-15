package rms.alert.metrics.alert.configs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Configuration
//@PropertySource("file:/GitHub/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/alert.properties")
@PropertySource("file:/home/cong/vpn/D3M/RealTimeDataMonitoring/zp-rms-alert/conf/alert.properties")
//@PropertySource("file:${HOME}/conf/alert.properties")
public class SMTPConfig {

	private static final Logger logger = LogManager.getLogger();

	@Value("${mail.smtp.host}")
	private String smtpHost;

	@Value("${mail.smtp.port}")
	private String smtpPort;

	@Value("${mail.smtp.subject}")
	private String smtpSubject;

	@Value("${mail.smtp.from.email}")
	private String smtpFromEmail;

	@Value("${mail.smtp.from.password}")
	private String smtpFromPassword;

	@Value("${mail.smtp.from.name}")
	private String smtpFromName;

	@Value("${mail.smtp.to}")
	private String smtpTo;

	@Value("${mail.smtp.send.wait.time}")
	private String smtpSendWaitTime;

	private static String smtpHtmlDesign = "";
	private static String smtpHtmlImageDesign = "";

	@Autowired
	private Environment environment;

	private String getConfigPath() {
		try {
			return environment.getProperty("app.path").trim();
		} catch (Exception e) {
			return ".";
		}
	}

	@PostConstruct
	private void LoadHTML() {
		try {
			smtpHtmlDesign = readLineByLineJava8(getConfigPath() + "/conf/html/mail.html");
			smtpHtmlImageDesign = readLineByLineJava8(getConfigPath() + "/conf/html/mailImage.html");

		} catch (Exception e) {
			logger.error("Read HTML mail design file fail");
		}
	}

	private static String readLineByLineJava8(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			logger.error("Read HTML mail design file fail", e);
		}
		return contentBuilder.toString();
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public String getSmtpSubject() {
		return smtpSubject;
	}

	public String getSmtpFromEmail() {
		return smtpFromEmail;
	}

	public String getSmtpFromPassword() {
		return smtpFromPassword;
	}

	public String getSmtpFromName() {
		return smtpFromName;
	}

	public String getSmtpTo() {
		return smtpTo;
	}

	public String getSmtpSendWaitTime() {
		return smtpSendWaitTime;
	}

	public String getSmtpHtmlDesign() {
		return smtpHtmlDesign;
	}

	public String getSmtpHtmlImageDesign() {
		return smtpHtmlImageDesign;
	}


}
