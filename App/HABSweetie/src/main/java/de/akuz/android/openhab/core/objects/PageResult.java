package de.akuz.android.openhab.core.objects;

import com.google.api.client.util.Key;

public class PageResult {

	@Key
	private Page page;

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
