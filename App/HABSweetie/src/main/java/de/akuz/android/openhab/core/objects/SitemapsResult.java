package de.akuz.android.openhab.core.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.util.Key;

public class SitemapsResult extends AbstractSitemapsResult {

	@Key
	private ArrayList<Sitemap> sitemap;

	public void setSitemap(ArrayList<Sitemap> sitemaps) {
		this.sitemap = sitemaps;
	}

	@Override
	public List<Sitemap> getSitemap() {
		return sitemap != null ? Collections.unmodifiableList(sitemap) : null;
	}

}
