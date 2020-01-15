package rms.alert.utils.mynumberformat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MyNumberFormat {

	public static String CurrencyFormat(Long money) {
		Locale localeVN = new Locale("vi", "VN");
		NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
		String result = currencyVN.format(money);
		return (result.substring(0, result.length() - 2) + " " + currencyVN.getCurrency());
	}

	public static String latLngToFixed(String latLng) {
		double number = Double.parseDouble(latLng);
		DecimalFormat decimalFormat = new DecimalFormat("#0.0000");
		String numberAsString = decimalFormat.format(number);
		return numberAsString;
	}

	public static String to4Fixed(String latLng) {
		double number = Double.parseDouble(latLng);
		DecimalFormat decimalFormat = new DecimalFormat("#0.0000");
		String numberAsString = decimalFormat.format(number);
		return numberAsString;
	}

}
