package de.akuz.android.openhab.ui;

import java.text.MessageFormat;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.SpiceManager;

import dagger.ObjectGraph;
import de.akuz.android.openhab.BootstrapApplication;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.util.InteractionReceiver;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BaseActivity extends SherlockFragmentActivity {

	@Inject
	SpiceManager spiceManager;

	protected InteractionReceiver sslInteractionReceiver;

	protected ObjectGraph activityGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BootstrapApplication app = (BootstrapApplication) getApplication();
		activityGraph = app.getObjectGraph().plus(new UiModule(this));
		activityGraph.inject(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sslInteractionReceiver == null) {
			sslInteractionReceiver = InteractionReceiver.registerReceiver(this);
		}
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
		case R.id.settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			result = true;
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}
		return result;
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

	public ObjectGraph getObjectGraph() {
		return activityGraph;
	}

}
