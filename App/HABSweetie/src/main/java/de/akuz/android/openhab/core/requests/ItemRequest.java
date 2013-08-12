package de.akuz.android.openhab.core.requests;

import com.google.api.client.http.HttpRequest;

import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;

public class ItemRequest extends AbstractOpenHABRequest<Item> {

	private Item item;

	public ItemRequest(OpenHABConnectionSettings settings, Item item) {
		super(Item.class, settings);
		this.item = item;
	}

	@Override
	public void setParameters(String... params) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Item executeRequest() throws Exception {
		HttpRequest request = getRequest(item.link);
		return parseInputStream(request.execute().getContent());
	}

}
