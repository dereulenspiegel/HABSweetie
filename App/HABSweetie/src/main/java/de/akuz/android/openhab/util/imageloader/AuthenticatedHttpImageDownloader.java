package de.akuz.android.openhab.util.imageloader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.net.Uri;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.squareup.okhttp.OkHttpClient;

import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.util.HABSweetiePreferences;
import de.akuz.android.openhab.util.Strings;

public class AuthenticatedHttpImageDownloader extends BaseImageDownloader {

	private static final int MAX_REDIRECT_COUNT = 5;

	@Inject
	OkHttpClient okClient;

	@Inject
	HABSweetiePreferences prefs;

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
		String authorizationHeaderValue = getAuthorizationStringForUrl(encodedUrl);
		if (!Strings.isEmpty(authorizationHeaderValue)) {
			conn.setRequestProperty("Authorization", authorizationHeaderValue);
		}
		conn.connect();
		return conn;
	}

	private String getAuthorizationStringForUrl(String url) {
		Uri uri = Uri.parse(url);
		StringBuffer buf = new StringBuffer();
		buf.append(uri.getScheme());
		buf.append("://");
		buf.append(uri.getHost());
		String baseUrl = buf.toString();
		List<OpenHABInstance> instances = prefs.getAllConfiguredInstances();
		for (OpenHABInstance instance : instances) {
			if (instance.getExternal().getBaseUrl().startsWith(baseUrl)) {
				return instance.getExternal().getAuthorizationHeaderValue();
			}
			if (instance.getInternal().getBaseUrl().startsWith(baseUrl)) {
				return instance.getInternal().getAuthorizationHeaderValue();
			}
		}
		return null;
	}

	public AuthenticatedHttpImageDownloader(Context context) {
		super(context);

	}

}
