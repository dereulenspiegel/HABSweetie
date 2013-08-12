package de.akuz.android.openhab.core.requests;

import java.io.IOException;
import java.io.OutputStream;

import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import de.akuz.android.openhab.settings.OpenHABConnectionSettings;

public class ItemCommandRequest extends GoogleHttpClientSpiceRequest<Void> {

	private String itemUrl;
	private String command;

	private OpenHABConnectionSettings setting;

	private final static String REST_ITEM_PATH = "/rest/items/";

	public ItemCommandRequest(OpenHABConnectionSettings setting,
			String itemName, String command) {
		this(buildItemUrl(setting, itemName), command);
		this.setting = setting;
	}

	private ItemCommandRequest(String itemUrl, String command) {
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
		}
		request.execute();
		return null;
	}

	private static String buildItemUrl(OpenHABConnectionSettings setting,
			String itemName) {
		String baseUrl = setting.getBaseUrl();
		StringBuffer buffer = new StringBuffer(baseUrl.length()
				+ REST_ITEM_PATH.length() + itemName.length());
		buffer.append(baseUrl).append(REST_ITEM_PATH).append(itemName);
		return buffer.toString();
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
