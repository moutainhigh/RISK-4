package rms.alert.utils.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {

	public static String getCurrentDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		Date date = new Date();
		return formatter.format(date);
	}

}
