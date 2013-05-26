package de.akuz.android.openhab.core.requests;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

import de.akuz.android.openhab.core.objects.Page;

public class PageRequest extends AbstractOpenHABRequest<Page> {

	private final static String TAG = PageRequest.class.getSimpleName();

	private String pageUrl;

	public PageRequest(String baseUrl, String pageUrl) {
		super(Page.class, baseUrl);
		this.pageUrl = pageUrl;
	}

	@Override
	public void setParameters(String... params) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Page executeRequest() throws Exception {
		HttpRequest request = getRequest(pageUrl);
		HttpResponse response = request.execute();

		Page result = parseInputStream(response.getContent());
		result.setReceivedAt(System.currentTimeMillis());
		return result;
	}

}
