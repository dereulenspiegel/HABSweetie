package de.akuz.android.openhab.core.requests;

import de.akuz.android.openhab.core.objects.SitemapsResult;

public class SitemapsRequest extends AbstractOpenHABRequest<SitemapsResult> {

	public SitemapsRequest(String baseUrl) {
		super(SitemapsResult.class, baseUrl);
	}

	@Override
	public void setParameters(String... params) {
		// Nothing Todo

	}

	@Override
	protected SitemapsResult executeRequest() throws Exception {
		return getRestAdapter().getAllSitemaps();
	}

}
