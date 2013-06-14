package de.akuz.android.openhab.core.requests;

import java.io.IOException;
import java.io.OutputStream;

import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import de.akuz.android.openhab.core.OpenHABAuthManager;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;

public class ItemCommandRequest extends GoogleHttpClientSpiceRequest<Void> {

	private String itemUrl;
	private String command;

	private OpenHABConnectionSettings setting;

	public ItemCommandRequest(OpenHABConnectionSettings setting,
			String itemUrl, String command) {
		this(itemUrl, command);
		this.setting = setting;
	}

	public ItemCommandRequest(String itemUrl, String command) {
		super(Void.class);
		this.itemUrl = itemUrl;
		this.command = command;
	}

	@Override
	public Void loadDataFromNetwork() throws Exception {
		HttpContent content = new StringHttpContent(command);
		HttpRequest request = getHttpRequestFactory().buildPostRequest(
				new GenericUrl(itemUrl), content);
		if (setting != null && setting.hasCredentials()) {
			BasicAuthentication auth = new BasicAuthentication(
					setting.getUsername(), setting.getPassword());
			request.setInterceptor(auth);
		} else if (OpenHABAuthManager.hasCredentials()) {
			BasicAuthentication auth = new BasicAuthentication(
					OpenHABAuthManager.getUsername(),
					OpenHABAuthManager.getPassword());
			request.setInterceptor(auth);
		}
		request.execute();
		return null;
	}

	private static class StringHttpContent implements HttpContent {

		private String content;

		public StringHttpContent(String content) {
			this.content = content;
		}

		@Override
		public long getLength() throws IOException {
			return content.length();
		}

		@Override
		public String getType() {
			return "text/plain";
		}

		@Override
		public void writeTo(OutputStream out) throws IOException {
			out.write(content.getBytes());

		}

		@Override
		public boolean retrySupported() {
			return true;
		}

	}

}
