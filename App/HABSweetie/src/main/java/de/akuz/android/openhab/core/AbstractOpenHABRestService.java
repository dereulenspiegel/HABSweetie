package de.akuz.android.openhab.core;

import java.util.HashSet;
import java.util.Set;

import android.net.ConnectivityManager;
import android.os.Handler;
import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;

public abstract class AbstractOpenHABRestService implements
		OpenHABAsyncRestInterface {

	private Set<OpenHABAsyncRestListener> listeners = new HashSet<OpenHABAsyncRestInterface.OpenHABAsyncRestListener>();

	private Handler uiHandler = new Handler();

	protected ConnectivityManager conManager;

	protected AbstractOpenHABRestService(ConnectivityManager conManager) {
		uiHandler = new Handler();
		this.conManager = conManager;
	}

	@Override
	public void registerListener(OpenHABAsyncRestListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}

	}

	@Override
	public void unregisterListener(OpenHABAsyncRestListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}

	}

	protected void notifyException(final Exception e) {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OpenHABAsyncRestListener l : listeners) {
					l.requestFailed(e);
				}

			}
		});
	}

	protected void notifySuccess(final AbstractOpenHABObject result) {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OpenHABAsyncRestListener l : listeners) {
					l.success(result);
				}

			}
		});
	}

	protected boolean canWeRetry(OpenHABInstance instance) {
		OpenHABConnectionSettings settings = instance
				.getSettingForCurrentNetwork(conManager);
		instance.notifyInternalConnectFailed();
		OpenHABConnectionSettings newSettings = instance
				.getSettingForCurrentNetwork(conManager);
		if (newSettings.getId() != settings.getId()) {
			settings = newSettings;
			return true;
		}
		return false;
	}

}
