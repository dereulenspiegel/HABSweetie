package de.akuz.android.openhab.core.objects;

import com.google.api.client.util.Key;

public class Sitemap extends AbstractOpenHABObject {

	@Key
	public String name;

	@Key
	public String link;

	@Key
	public Homepage homepage;

	public static class Homepage {
		@Key
		public String link;

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String url) {
		this.link = url;
	}

	public Homepage getHomepage() {
		return homepage;
	}

	public void setHomepage(Homepage homepage) {
		this.homepage = homepage;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
