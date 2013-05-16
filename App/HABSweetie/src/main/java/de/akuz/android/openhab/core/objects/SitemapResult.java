package de.akuz.android.openhab.core.objects;

import com.google.api.client.util.Key;

public class SitemapResult {

	@Key
	public Sitemap sitemap;

	public Sitemap getSitemap() {
		return sitemap;
	}

	public void setSitemap(Sitemap sitemap) {
		this.sitemap = sitemap;
	}

}
