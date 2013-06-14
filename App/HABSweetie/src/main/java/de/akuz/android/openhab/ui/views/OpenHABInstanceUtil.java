package de.akuz.android.openhab.ui.views;

import javax.inject.Inject;
import javax.inject.Singleton;

import roboguice.util.temp.Strings;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class OpenHABInstanceUtil {

	private ConnectivityManager conManager;

	@Inject
	@Singleton
	public OpenHABInstanceUtil(ConnectivityManager conManager) {
		this.conManager = conManager;
	}

	public OpenHABConnectionSettings chooseSetting(OpenHABInstance instance) {
		if (instance == null) {
			return null;
		}
		NetworkInfo currentNetwork = conManager.getActiveNetworkInfo();
		if (currentNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
			OpenHABConnectionSettings setting = instance.getExternal();
			if (setting != null && Strings.isEmpty(setting.getBaseUrl())) {
				return instance.getInternal();
			}
			return instance.getExternal();
		}
		return instance.getInternal();
	}

	public String chooseSitemapUrl(OpenHABInstance instance) {
		return chooseSetting(instance).getBaseUrl();
	}

}
