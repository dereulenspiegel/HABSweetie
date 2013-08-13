package de.akuz.android.openhab.core.requests;

import java.io.IOException;
import java.io.OutputStream;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;

import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;
import de.akuz.android.openhab.core.requests.ItemCommandRequest.VoidOpenHABObject;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;

public class ItemCommandRequest extends
		AbstractOpenHABRequest<VoidOpenHABObject> {

	private String itemUrl;
	private String command;

	private OpenHABConnectionSettings setting;

	private final static String REST_ITEM_PATH = "/rest/items/";

	public ItemCommandRequest(OpenHABConnectionSettings setting,
			String itemName, String command) {
		super(VoidOpenHABObject.class, setting);
		this.setting = setting;
		itemUrl = buildItemUrl(setting, itemName);
		this.command = command;
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

	public static class VoidOpenHABObject extends AbstractOpenHABObject {

	}

	@Override
	public void setParameters(String... params) {
		// TODO Auto-generated method stub

	}

	@Override
	protected VoidOpenHABObject executeRequest() throws Exception {
		HttpContent content = new StringHttpContent(command);
		HttpRequest request = getHttpRequestFactory().buildPostRequest(
				new GenericUrl(itemUrl), content);
		configureRequest(request);
		request.execute();
		return null;
	}

}
