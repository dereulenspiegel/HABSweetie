package de.akuz.android.openhab.core.requests;

import de.akuz.android.openhab.core.objects.Item;

public class ItemRequest extends AbstractOpenHABRequest<Item> {

	private Item item;

	public ItemRequest(Item item, String baseUrl) {
		super(Item.class, baseUrl);
		this.item = item;
	}

	@Override
	public void setParameters(String... params) {
		// Ignore

	}

	@Override
	protected Item executeRequest() throws Exception {
		return getRestAdapter().getItem(item.name);
	}

}
