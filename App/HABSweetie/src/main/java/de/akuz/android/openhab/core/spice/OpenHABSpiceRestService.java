package de.akuz.android.openhab.core.spice;

import android.net.ConnectivityManager;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.core.AbstractOpenHABRestService;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.core.requests.ItemCommandRequest;
import de.akuz.android.openhab.core.requests.ItemCommandRequest.VoidOpenHABObject;
import de.akuz.android.openhab.core.requests.PageRequest;
import de.akuz.android.openhab.core.requests.SitemapsRequest;
import de.akuz.android.openhab.settings.OpenHABInstance;

public class OpenHABSpiceRestService extends AbstractOpenHABRestService {

	private SpiceManager spiceManager;

	public OpenHABSpiceRestService(SpiceManager spiceManager,
			ConnectivityManager conManager) {
		super(conManager);
		this.spiceManager = spiceManager;
	}

	@Override
	public void loadSitemaps(final OpenHABInstance instance,
			final OpenHABAsyncRestListener listener) {
		SitemapsRequest request = new SitemapsRequest(
				instance.getSettingForCurrentNetwork(conManager));
		spiceManager.execute(request, new RequestListener<SitemapsResult>() {

			@Override
			public void onRequestFailure(SpiceException spiceException) {
				if (canWeRetry(instance)) {
					loadSitemaps(instance);
				} else {
					if (listener == null) {
						notifyException(spiceException);
					} else {
						listener.requestFailed(spiceException);
					}
				}

			}

			@Override
			public void onRequestSuccess(SitemapsResult result) {
				if (listener == null) {
					notifySuccess(result);
				} else {
					listener.success(result);
				}

			}
		});

	}

	@Override
	public void sendCommand(final OpenHABInstance instance, final Item item,
			final String command, final OpenHABAsyncRestListener listener) {
		ItemCommandRequest request = new ItemCommandRequest(
				instance.getSettingForCurrentNetwork(conManager), item.name,
				command);
		spiceManager.execute(request, new RequestListener<VoidOpenHABObject>() {

			@Override
			public void onRequestFailure(SpiceException spiceException) {
				if (canWeRetry(instance)) {
					sendCommand(instance, item, command, listener);
				} else {
					if (listener == null) {
						notifyException(spiceException);
					} else {
						listener.requestFailed(spiceException);
					}
				}

			}

			@Override
			public void onRequestSuccess(VoidOpenHABObject result) {
				if (listener == null) {
					notifySuccess(result);
				} else {
					listener.success(result);
				}

			}
		});
	}

	@Override
	public void loadSitemaps(OpenHABInstance instance) {
		loadSitemaps(instance, null);

	}

	@Override
	public void sendCommand(OpenHABInstance instance, Item item, String command) {
		sendCommand(instance, item, command, null);

	}

	@Override
	public void loadCompletePage(final OpenHABInstance instance,
			final String sitemapId, final String pageId) {
		loadCompletePage(instance, sitemapId, pageId, null);
	}

	@Override
	public void loadCompletePage(final OpenHABInstance instance,
			final String sitemapId, final String pageId,
			final OpenHABAsyncRestListener listener) {
		PageRequest request = new PageRequest(
				instance.getSettingForCurrentNetwork(conManager), sitemapId,
				pageId);
		spiceManager.execute(request, new RequestListener<Page>() {

			@Override
			public void onRequestFailure(SpiceException spiceException) {
				if (canWeRetry(instance)) {
					loadCompletePage(instance, sitemapId, pageId, listener);
				} else {
					if (listener == null) {
						notifyException(spiceException);
					} else {
						listener.requestFailed(spiceException);
					}
				}

			}

			@Override
			public void onRequestSuccess(Page result) {
				if (listener == null) {
					notifySuccess(result);
				} else {
					listener.success(result);
				}

			}
		});

	}
}
