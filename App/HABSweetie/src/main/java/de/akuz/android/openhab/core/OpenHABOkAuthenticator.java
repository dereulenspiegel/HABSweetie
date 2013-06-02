package de.akuz.android.openhab.core;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import com.squareup.okhttp.OkAuthenticator;
import com.squareup.okhttp.OkAuthenticator.Challenge;
import com.squareup.okhttp.OkAuthenticator.Credential;

public class OpenHABOkAuthenticator implements OkAuthenticator {

	@Override
	public Credential authenticate(Proxy proxy, URL url,
			List<Challenge> challenges) throws IOException {
		for (Challenge c : challenges) {
			if ("basic".equalsIgnoreCase(c.getScheme())) {
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
