package de.akuz.android.openhab.core;

import de.akuz.android.openhab.settings.OpenHABConnectionSettings;

public interface PageConnectionInterface {

	public void registerUpdateListener(PageUpdateListener listener);

	public void unregisterUpdateListener(PageUpdateListener listener);

	public void open(OpenHABConnectionSettings settings, String sitemapId,
			String pageId);

	public void open(OpenHABConnectionSettings settings, String pageUrl);

	public void close();

	public boolean isServerPushEnabled();

}
