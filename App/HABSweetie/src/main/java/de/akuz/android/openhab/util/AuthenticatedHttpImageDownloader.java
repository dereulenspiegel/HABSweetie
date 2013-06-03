package de.akuz.android.openhab.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.squareup.okhttp.OkHttpClient;

public class AuthenticatedHttpImageDownloader extends BaseImageDownloader {

	private static final int MAX_REDIRECT_COUNT = 5;

	private static String username;
	private static String password;

	@Inject
	OkHttpClient okClient;

	@Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra)
			throws IOException {
		HttpURLConnection conn = connectTo(imageUri);

		int redirectCount = 0;
		while (conn.getResponseCode() / 100 == 3
				&& redirectCount < MAX_REDIRECT_COUNT) {
			conn = connectTo(conn.getHeaderField("Location"));
			redirectCount++;
		}

		return new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE);
	}

	private HttpURLConnection connectTo(String url) throws IOException {
		String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
		HttpURLConnection conn = okClient.open(new URL(encodedUrl));
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		if (username != null && password != null) {
			byte[] encoded = Base64.encode(
					(username + ":" + password).getBytes(), Base64.DEFAULT);
			conn.setRequestProperty("Authorization", "Basic "
					+ new String(encoded));
		}
		conn.connect();
		return conn;
	}

	public AuthenticatedHttpImageDownloader(Context context) {
		super(context);

	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		AuthenticatedHttpImageDownloader.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		AuthenticatedHttpImageDownloader.password = password;
	}

}
