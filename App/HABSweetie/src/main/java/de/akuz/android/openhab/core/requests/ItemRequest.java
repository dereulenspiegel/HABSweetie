package de.akuz.android.openhab.core.requests;

import com.google.api.client.http.HttpRequest;

import de.akuz.android.openhab.core.objects.Item;

public class ItemRequest extends AbstractOpenHABRequest<Item> {

	private Item item;

	public ItemRequest(Item item, String baseUrl) {
		super(Item.class, baseUrl);
		this.item = item;
	}

	@Override
	public void setParameters(String... params) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Item executeRequest() throws Exception {
		HttpRequest request = getRequest(item.link);
		return request.execute().parseAs(Item.class);
	}

}
