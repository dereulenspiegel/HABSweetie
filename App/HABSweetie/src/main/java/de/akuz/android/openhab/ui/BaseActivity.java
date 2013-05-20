package de.akuz.android.openhab.ui;

import java.text.MessageFormat;

import javax.inject.Inject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.octo.android.robospice.SpiceManager;

import dagger.ObjectGraph;
import de.akuz.android.openhab.BootstrapApplication;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.CommunicationModule;
import de.akuz.android.openhab.core.OpenHABAuthManager;
import de.akuz.android.openhab.core.OpenHABRestService;
import de.duenndns.ssl.InteractionReceiver;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BaseActivity extends FragmentActivity {

	@Inject
	protected SpiceManager spiceManager;

	protected InteractionReceiver sslInteractionReceiver;

	protected ObjectGraph activityGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BootstrapApplication app = (BootstrapApplication) getApplication();
		activityGraph = app.getObjectGraph()
				.plus(new CommunicationModule(this));
		activityGraph.inject(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sslInteractionReceiver == null) {
			sslInteractionReceiver = InteractionReceiver.registerReceiver(this);
		}
		String username = getPreferenceStringValue(R.string.pref_username_key);
		String password = getPreferenceStringValue(R.string.pref_password_key);
		OpenHABAuthManager.updateCredentials(username, password);
	}

	@Override
	protected void onPause() {
		if (sslInteractionReceiver != null) {
			sslInteractionReceiver.unregister();
			sslInteractionReceiver = null;
		}
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();

		spiceManager.start(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		spiceManager.shouldStop();
	}

	@Override
	protected void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}

	public SpiceManager getSpiceManager() {
		return spiceManager;
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T findView(int resId) {
		View view = findViewById(resId);
		if (view == null) {
			return null;
		}
		return (T) view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		new MenuInflater(this).inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;
		switch (item.getItemId()) {
		case R.id.main_settings:
			loadOptions();
			result = true;
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}
		return result;
	}

	private void loadOptions() {

		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
	}

	protected String getPreferenceStringValue(int resId) {
		return PreferenceManager.getDefaultSharedPreferences(this).getString(
				getString(resId), null);
	}

	protected void makeCrouton(int resId, Style style, Object... params) {
		String message = getString(resId);
		if (params != null && params.length > 0) {
			message = MessageFormat.format(message, params);
		}
		Crouton.makeText(this, message, style).show();
	}

	public void inject(Object o) {
		activityGraph.inject(o);
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<?> clazz) {
		return (T) activityGraph.get(clazz);
	}

}
