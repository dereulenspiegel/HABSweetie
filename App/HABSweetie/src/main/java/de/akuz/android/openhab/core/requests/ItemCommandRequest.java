package de.akuz.android.openhab.core.requests;

import de.akuz.android.openhab.core.OpenHABRestInterface;
import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;
import de.akuz.android.openhab.core.objects.Item;

public class ItemCommandRequest extends
		AbstractOpenHABRequest<AbstractOpenHABObject> {

	private Item item;
	private String command;

	public ItemCommandRequest(Item item, String command) {
		super(AbstractOpenHABObject.class, parseBaseUrlFromItem(item));
		this.item = item;
		this.command = command;
	}

	private static String parseBaseUrlFromItem(Item item) {
		return item.link.substring(0, (item.name.length() + 11));
	}

	@Override
	public AbstractOpenHABObject loadDataFromNetwork() throws Exception {
		OpenHABRestInterface rest = getRestAdapter();
		rest.sendItemCommand(item.name, command);
		return null;
	}

	@Override
	public void setParameters(String... params) {
		// Ignore

	}

	@Override
	protected AbstractOpenHABObject executeRequest() throws Exception {
		// Ignore
		return null;
	}

}
