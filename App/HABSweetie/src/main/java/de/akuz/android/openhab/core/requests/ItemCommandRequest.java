package de.akuz.android.openhab.core.requests;

import java.io.IOException;
import java.io.OutputStream;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import de.akuz.android.openhab.core.OpenHABAuthManager;

public class ItemCommandRequest extends GoogleHttpClientSpiceRequest<Void> {

	private String itemUrl;
	private String command;

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
		if (OpenHABAuthManager.hasCredentials()) {
			HttpHeaders headers = request.getHeaders();
			headers.setBasicAuthentication(OpenHABAuthManager.getUsername(),
					OpenHABAuthManager.getPassword());
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
		public String getEncoding() {
			return "UTF-8";
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
