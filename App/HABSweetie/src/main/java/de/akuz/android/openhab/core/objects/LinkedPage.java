package de.akuz.android.openhab.core.objects;

import com.google.api.client.util.Key;

public class LinkedPage extends AbstractOpenHABObject {

	@Key
	private String id;

	@Key
	private String title;

	@Key
	private String icon;

	@Key
	private String link;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
