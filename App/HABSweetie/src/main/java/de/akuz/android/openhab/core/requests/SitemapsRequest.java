package de.akuz.android.openhab.core.requests;

import com.google.api.client.http.HttpRequest;

import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;

public class SitemapsRequest extends AbstractOpenHABRequest<SitemapsResult> {

	public SitemapsRequest(OpenHABConnectionSettings setting) {
		super(SitemapsResult.class, setting);
	}

//	public SitemapsRequest(String baseUrl) {
//		super(SitemapsResult.class, baseUrl);
//	}

	@Override
	public void setParameters(String... params) {
		// Nothing Todo

	}

	@Override
	protected SitemapsResult executeRequest() throws Exception {
		HttpRequest request = getRequest(baseUrl + "/rest/sitemaps");
		return parseInputStream(request.execute().getContent());
	}

}
