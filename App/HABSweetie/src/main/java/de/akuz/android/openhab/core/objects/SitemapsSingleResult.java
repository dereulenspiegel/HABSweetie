package de.akuz.android.openhab.core.objects;

import java.util.ArrayList;
import java.util.List;

public class SitemapsSingleResult extends AbstractSitemapsResult {

	private Sitemap sitemap;

	@Override
	public List<Sitemap> getSitemap() {
		ArrayList<Sitemap> list = new ArrayList<Sitemap>(1);
		list.add(sitemap);
		return list;
	}

	public void setSitemap(Sitemap sitemap) {
		this.sitemap = sitemap;
	}

}
