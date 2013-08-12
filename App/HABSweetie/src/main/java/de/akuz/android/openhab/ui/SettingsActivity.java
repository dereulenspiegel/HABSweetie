package de.akuz.android.openhab.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import de.akuz.android.openhab.R;

public class SettingsActivity extends SherlockPreferenceActivity implements
		OnPreferenceClickListener {

	Preference manageInstances;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.connection_prefs);

		manageInstances = findPreference("manageInstances");
		manageInstances.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(manageInstances.getKey())) {
			Intent i = new Intent(this, ManageInstancesActivity.class);
			startActivity(i);
			return true;
		}
		return false;
	}
}
