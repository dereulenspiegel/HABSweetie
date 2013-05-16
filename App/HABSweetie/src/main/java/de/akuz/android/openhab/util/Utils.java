package de.akuz.android.openhab.util;

public class Utils {

	public static boolean hasCause(Throwable e,
			Class<? extends Throwable> causeClass) {
		Throwable cause = e;
		while (cause != null) {
			if (cause.getClass().equals(causeClass)) {
				return true;
			}
			cause = cause.getCause();
		}

		return false;
	}

}
