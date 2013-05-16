package de.akuz.android.openhab.ui;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.OpenHABAuthManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class ConnectionPreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.connection_prefs);
	}

	@Override
	public void onPause() {
		String username = getPreferenceStringValue(R.string.pref_username_key);
		String password = getPreferenceStringValue(R.string.pref_password_key);
		OpenHABAuthManager.updateCredentials(username, password);
		super.onPause();
	}

	private String getPreferenceStringValue(int resId) {
		return getPreferenceManager().getSharedPreferences().getString(
				getString(resId), null);
	}

}
