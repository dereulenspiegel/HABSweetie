package de.akuz.android.openhab.core.requests;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

import de.akuz.android.openhab.core.objects.Page;

public class PageRequest extends AbstractOpenHABRequest<Page> {

	ObjectMapper mapper = new ObjectMapper();
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
		Page result = response.parseAs(Page.class);
		result.setReceivedAt(System.currentTimeMillis());
		return result;
	}

}
