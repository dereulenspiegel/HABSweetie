package de.akuz.android.openhab.core.requests;

import com.google.api.client.http.HttpRequest;

import de.akuz.android.openhab.core.objects.ItemsResult;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;

public class ItemsRequest extends AbstractOpenHABRequest<ItemsResult> {

	public ItemsRequest(OpenHABConnectionSettings setting) {
		this(setting.getBaseUrl());
		super.setting = setting;
	}

	private ItemsRequest(String baseUrl) {
		super(ItemsResult.class, baseUrl);
	}

	@Override
	public void setParameters(String... params) {
		// Nothing todo

	}

	@Override
	protected ItemsResult executeRequest() throws Exception {
		HttpRequest request = getRequest(baseUrl + "/rest/items");
		return parseInputStream(request.execute().getContent());
	}
}
