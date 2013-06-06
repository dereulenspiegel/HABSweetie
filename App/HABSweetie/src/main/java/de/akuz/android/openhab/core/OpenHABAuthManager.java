package de.akuz.android.openhab.core;

import com.google.api.client.util.Base64;

import de.akuz.android.openhab.util.AuthenticatedHttpImageDownloader;

public class OpenHABAuthManager {

	private static String username;
	private static String password;

	public static void updateCredentials(String username, String password) {
		OpenHABAuthManager.username = username;
		OpenHABAuthManager.password = password;
		AuthenticatedHttpImageDownloader.setPassword(password);
		AuthenticatedHttpImageDownloader.setUsername(username);
	}

	public static boolean hasCredentials() {
		return username != null && password != null;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getEncodedCredentials() {
		String encoded = Base64.encodeBase64String((username + ":" + password)
				.getBytes());
		return encoded;
	}

	public static String getAuthorizationHeaderValue() {
		return "basic " + getEncodedCredentials();
	}

}
