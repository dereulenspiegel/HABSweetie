package de.akuz.android.openhab.core.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import com.squareup.okhttp.OkAuthenticator;

import de.akuz.android.openhab.core.OpenHABAuthManager;

public class OpenHABOkAuthenticator implements OkAuthenticator {

	@Override
	public Credential authenticate(Proxy proxy, URL url,
			List<Challenge> challenges) throws IOException {
		for (Challenge c : challenges) {
			if ("basic".equalsIgnoreCase(c.getScheme())
					&& OpenHABAuthManager.hasCredentials()) {
				return Credential.basic(OpenHABAuthManager.getUsername(),
						OpenHABAuthManager.getPassword());
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
