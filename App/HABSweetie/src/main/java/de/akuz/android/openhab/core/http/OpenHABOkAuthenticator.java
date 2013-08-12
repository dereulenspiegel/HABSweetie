package de.akuz.android.openhab.core.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import com.squareup.okhttp.OkAuthenticator;

import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class OpenHABOkAuthenticator implements OkAuthenticator {

	@Inject
	HABSweetiePreferences prefs;

	@Override
	public Credential authenticate(Proxy proxy, URL url,
			List<Challenge> challenges) throws IOException {
		for (Challenge c : challenges) {
			if ("basic".equalsIgnoreCase(c.getScheme())) {
				OpenHABConnectionSettings settings = getAuthorizationStringForUrl(url);
				if (settings != null) {
					return Credential.basic(settings.getUsername(),
							settings.getPassword());
				}
			}
		}
		return null;
	}

	private OpenHABConnectionSettings getAuthorizationStringForUrl(URL url) {
		StringBuffer buf = new StringBuffer();
		buf.append(url.getProtocol());
		buf.append("://");
		buf.append(url.getHost());
		String baseUrl = buf.toString();
		List<OpenHABInstance> instances = prefs.getAllConfiguredInstances();
		for (OpenHABInstance instance : instances) {
			if (instance.getExternal().getBaseUrl().startsWith(baseUrl)) {
				return instance.getExternal();
			}
			if (instance.getInternal().getBaseUrl().startsWith(baseUrl)) {
				return instance.getInternal();
			}
		}
		return null;
	}

	@Override
	public Credential authenticateProxy(Proxy proxy, URL url,
			List<Challenge> challenges) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
