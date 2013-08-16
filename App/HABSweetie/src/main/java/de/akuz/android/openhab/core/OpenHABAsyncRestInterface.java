package de.akuz.android.openhab.core;

import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.settings.OpenHABInstance;

public interface OpenHABAsyncRestInterface {

	public static interface OpenHABAsyncRestListener {
		public void requestFailed(Exception e);

		public void success(AbstractOpenHABObject object);
	}

	public void loadSitemaps(OpenHABInstance instance);

	public void sendCommand(OpenHABInstance instance, Item item, String command);

	public void loadSitemaps(OpenHABInstance instance,
			OpenHABAsyncRestListener listener);

	public void sendCommand(OpenHABInstance instance, Item item,
			String command, OpenHABAsyncRestListener listener);

	public void loadCompletePage(OpenHABInstance instance, String sitemapId,
			String pageId);

	public void loadCompletePage(OpenHABInstance instance, String sitemapId,
			String pageId, OpenHABAsyncRestListener listener);

	public void registerListener(OpenHABAsyncRestListener listener);

	public void unregisterListener(OpenHABAsyncRestListener listener);
}
