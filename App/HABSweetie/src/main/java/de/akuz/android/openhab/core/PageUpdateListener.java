package de.akuz.android.openhab.core;

import java.util.List;

import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.Widget;

public interface PageUpdateListener {

	public void widgetUpdateReceived(Widget widget);

	public void pageUpdateReceived(Page page);

	public void exceptionOccured(Throwable t);

	public void connected();

	public void disconnected();

	public void sitemapsReceived(List<Sitemap> sitemaps);

}
