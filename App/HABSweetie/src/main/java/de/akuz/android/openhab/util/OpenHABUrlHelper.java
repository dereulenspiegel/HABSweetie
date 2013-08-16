package de.akuz.android.openhab.util;

public class OpenHABUrlHelper {

	public static String extractPageId(String pageUrl) {
		return getUrlPartFromEnd(pageUrl, 1);
	}

	private static String getUrlPartFromEnd(String url, int countFromEnd) {
		String[] parts = url.split("/");
		return parts[parts.length - countFromEnd];
	}

	public static String extractSitemapId(String pageUrl) {
		return getUrlPartFromEnd(pageUrl, 2);
	}
}
