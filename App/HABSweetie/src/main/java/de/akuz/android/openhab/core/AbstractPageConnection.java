package de.akuz.android.openhab.core;

import java.util.LinkedList;
import java.util.List;

import android.os.Handler;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.core.requests.ItemCommandRequest;
import de.akuz.android.openhab.core.requests.ItemRequest;
import de.akuz.android.openhab.core.requests.PageRequest;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.ui.widgets.ItemUpdateListener;

public abstract class AbstractPageConnection implements
		PageConnectionInterface, RequestListener<Page> {

	private final static String TAG = AbstractPageConnection.class
			.getSimpleName();

	private List<PageUpdateListener> listeners = new LinkedList<PageUpdateListener>();

	protected SpiceManager spiceManager;

	protected String pageId;
	protected String sitemapId;

	protected String pageUrl;

	private Handler uiHandler;

	protected OpenHABConnectionSettings settings;

	public AbstractPageConnection(SpiceManager spiceManager) {
		this.spiceManager = spiceManager;
		uiHandler = new Handler();
	}

	@Override
	public void loadCompletePage() {
		spiceManager.execute(new PageRequest(settings.getBaseUrl(), pageUrl),
				pageUrl, DurationInMillis.ALWAYS_EXPIRED, this);

	}

	@Override
	public void registerUpdateListener(PageUpdateListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}

	}

	@Override
	public void unregisterUpdateListener(PageUpdateListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	@Override
	public void open(OpenHABConnectionSettings settings, String sitemapId,
			String pageId) {
		this.settings = settings;
		this.sitemapId = sitemapId;
		this.pageId = pageId;
		this.pageUrl = getFullPageUrl();
		openWebSocketConnection();
	}

	@Override
	public void open(OpenHABConnectionSettings settings, String pageUrl) {
		String[] urlParts = pageUrl.split("/");
		String pageId = urlParts[urlParts.length - 1];
		String sitemapId = urlParts[urlParts.length - 2];
		open(settings, sitemapId, pageId);
	}

	protected abstract void openWebSocketConnection();

	@Override
	public void sendCommand(final Item item, final String command,
			final ItemUpdateListener listener) {
		spiceManager.execute(new ItemCommandRequest(item.link, command),
				new RequestListener<Void>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						notifyListenersOfException(spiceException.getCause());

					}

					@Override
					public void onRequestSuccess(Void result) {
						if (!isServerPushEnabled() && listener != null) {
							pollItem(item, listener);
						}

					}
				});

	}

	protected void pollItem(Item item, final ItemUpdateListener listener) {
		spiceManager.execute(new ItemRequest(item, settings.getBaseUrl()),
				new RequestListener<Item>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						notifyListenersOfException(spiceException.getCause());

					}

					@Override
					public void onRequestSuccess(Item result) {
						listener.itemUpdateReceived(result);

					}
				});
	}

	@Override
	public void onRequestFailure(SpiceException spiceException) {
		Throwable cause = spiceException.getCause();
		notifyListenersOfException(cause);

	}

	@Override
	public void onRequestSuccess(Page result) {
		notifyListenersOfPageUpdate(result);

	}

	protected void notifyListenersOfWidgetUpdate(final Widget widget) {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (final PageUpdateListener l : listeners) {
					l.widgetUpdateReceived(widget);

				}
			}
		});
	}

	protected void notifyListenersOfPageUpdate(final Page page) {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (final PageUpdateListener l : listeners) {
					l.pageUpdateReceived(page);

				}
			}
		});
	}

	protected void notifyListenersOfException(final Throwable t) {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (final PageUpdateListener l : listeners) {
					l.exceptionOccured(t);

				}
			}
		});
	}

	protected void notifyListenersConnected() {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (final PageUpdateListener l : listeners) {
					l.connected();

				}
			}
		});
	}

	protected void notifyListenersDisconnected() {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (final PageUpdateListener l : listeners) {
					l.disconnected();

				}
			}
		});
	}

	protected String getFullPageUrl() {
		StringBuilder b = new StringBuilder();
		b.append(settings.getBaseUrl());
		if (!settings.getBaseUrl().endsWith("/")) {
			b.append('/');
		}
		b.append("rest/sitemaps/");
		b.append(sitemapId);
		b.append("/");
		b.append(pageId);
		return b.toString();
	}
}
