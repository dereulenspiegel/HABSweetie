package de.akuz.android.openhab.core.objects;


public class Sitemap extends AbstractOpenHABObject {

	public String name;

	public String link;

	public Homepage homepage;

	public static class Homepage {

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
