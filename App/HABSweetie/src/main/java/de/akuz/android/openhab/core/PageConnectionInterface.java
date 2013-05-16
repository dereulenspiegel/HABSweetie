package de.akuz.android.openhab.core;

import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.ui.widgets.ItemUpdateListener;

public interface PageConnectionInterface {

	public void loadCompletePage();

	public void registerUpdateListener(PageUpdateListener listener);

	public void unregisterUpdateListener(PageUpdateListener listener);

	public void open(String baseUrl, String pageUrl);

	public void close();

	public void sendCommand(Item item, String command, ItemUpdateListener listener);
	
	public boolean isServerPushEnabled();

}
