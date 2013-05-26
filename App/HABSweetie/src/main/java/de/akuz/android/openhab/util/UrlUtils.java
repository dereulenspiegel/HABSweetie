package de.akuz.android.openhab.util;

public class UrlUtils {

	private final static String HTTP_PREFIX = "http";

	private UrlUtils() {

	}

	public static boolean isRelative(String url) {
		return !url.trim().startsWith(HTTP_PREFIX);
	}

	public static String concat(String base, String path) {
		StringBuilder builder = new StringBuilder(base.length() + path.length()
				+ 1);
		builder.append(base);
		if (!base.endsWith("/")) {
			builder.append('/');
		}
		String cleanPath = path;
		if (cleanPath.startsWith("/")) {
			cleanPath = cleanPath.substring(1);
		}
		builder.append(cleanPath);
		return builder.toString();
	}
}
