package rms.alert.metrics.alert.beans;

import java.io.File;

public class SMTPContent {

	private String htmlContent;
	private File imgFile;
	private String imgFileName;

	public SMTPContent(String htmlContent, String imgFileName, File imgFile) {
		super();
		this.htmlContent = htmlContent;
		this.imgFileName = imgFileName;
		this.imgFile = imgFile;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public String getImgFileName() {
		return imgFileName;
	}

	public File getImgFile() {
		return imgFile;
	}

	public boolean deleteImgFile() {
		return this.imgFile.delete();
	}

}
