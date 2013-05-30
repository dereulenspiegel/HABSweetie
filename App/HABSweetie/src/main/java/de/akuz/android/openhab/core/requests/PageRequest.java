package de.akuz.android.openhab.core.requests;

import de.akuz.android.openhab.core.objects.Page;

public class PageRequest extends AbstractOpenHABRequest<Page> {

	private final static String TAG = PageRequest.class.getSimpleName();

	private String pageId;
	private String sitemapId;

	public PageRequest(String baseUrl, String sitemapId, String pageId) {
		super(Page.class, baseUrl);
		this.pageId = pageId;
		this.sitemapId = sitemapId;
	}

	@Override
	public void setParameters(String... params) {
		// Ignore

	}

	@Override
	protected Page executeRequest() throws Exception {
		Page page = getRestAdapter().getPage(sitemapId, pageId);
		page.setReceivedAt(System.currentTimeMillis());
		return page;
	}
}
