package de.akuz.android.openhab.core.requests;

import de.akuz.android.openhab.core.objects.ItemsResult;

public class ItemsRequest extends AbstractOpenHABRequest<ItemsResult> {

	public ItemsRequest(String baseUrl) {
		super(ItemsResult.class, baseUrl);
	}

	@Override
	public void setParameters(String... params) {
		// Nothing todo

	}

	@Override
	protected ItemsResult executeRequest() throws Exception {
		return getRestAdapter().getAllItems();
	}
}
