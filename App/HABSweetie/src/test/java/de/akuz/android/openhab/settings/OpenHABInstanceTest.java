package de.akuz.android.openhab.settings;

import static org.junit.Assert.*;

import org.junit.Test;

import android.net.ConnectivityManager;

public class OpenHABInstanceTest {

	@Test
	public void testConfigurationSelection() throws Exception {
		OpenHABInstance instance = new OpenHABInstance();
		OpenHABConnectionSettings internal = new OpenHABConnectionSettings();
		OpenHABConnectionSettings external = new OpenHABConnectionSettings();

		instance.setInternal(internal);
		instance.setExternal(external);
		internal.setBaseUrl("http://demo.openhab.org:8080");
		OpenHABConnectionSettings chosenSettings = instance
				.getSettingForCurrentNetwork(ConnectivityManager.TYPE_WIFI);
		assertEquals("Expected internal connection settings", chosenSettings,
				internal);

		// External config is not valid, falling back to internal
		chosenSettings = instance
				.getSettingForCurrentNetwork(ConnectivityManager.TYPE_MOBILE);
		assertEquals("Expected internal connection settings", internal,
				chosenSettings);
		// Notifying connection failure, to simulate that we are not on our home
		// wifi
		instance.notifyInternalConnectFailed();
		chosenSettings = instance
				.getSettingForCurrentNetwork(ConnectivityManager.TYPE_WIFI);
		assertEquals("Expected internal connection settings", internal,
				chosenSettings);

		instance.clearInternConnectFailed();

		// Now using a valid external configuration
		external.setBaseUrl("http://demo.openhab.org:8080");
		chosenSettings = instance
				.getSettingForCurrentNetwork(ConnectivityManager.TYPE_MOBILE);
		assertEquals("Expected external configuratuon", external,
				chosenSettings);
		chosenSettings = instance
				.getSettingForCurrentNetwork(ConnectivityManager.TYPE_WIFI);
		assertEquals("Expected internal connection settings", internal,
				chosenSettings);

		instance.notifyInternalConnectFailed();
		chosenSettings = instance
				.getSettingForCurrentNetwork(ConnectivityManager.TYPE_WIFI);
		assertEquals("Expected external configuratuon", external,
				chosenSettings);
	}

}
