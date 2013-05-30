package de.akuz.android.openhab.core.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SitemapsResult extends AbstractSitemapsResult {

	private ArrayList<Sitemap> sitemap;

	public void setSitemap(ArrayList<Sitemap> sitemaps) {
		this.sitemap = sitemaps;
	}

	@Override
	public List<Sitemap> getSitemap() {
		return sitemap != null ? Collections.unmodifiableList(sitemap) : null;
	}

}
