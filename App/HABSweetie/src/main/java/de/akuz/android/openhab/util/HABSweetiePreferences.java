package de.akuz.android.openhab.util;

import javax.inject.Inject;

import de.akuz.android.openhab.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class HABSweetiePreferences {

	private SharedPreferences prefs;

	private Context ctx;

	@Inject
	public HABSweetiePreferences(Context ctx) {
		this.ctx = ctx;
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

	}

	public String getBaseUrl() {
		return prefs.getString(getString(R.string.pref_url_key), null);
	}

	public boolean isAuthenticationEnabled() {
		return prefs.getBoolean(getString(R.string.pref_authenticate_key),
				false);
	}

	public String getUsername() {
		return prefs.getString(getString(R.string.pref_username_key), null);
	}

	public String getPassword() {
		return prefs.getString(getString(R.string.pref_password_key), null);
	}

	public String getDefaultSitemapUrl() {
		return prefs.getString(
				getString(R.string.pref_default_sitemap_url_key), null);
	}

	public boolean useWebSockets() {
		return prefs.getBoolean(getString(R.string.pref_use_websockets_key),
				false);
	}

	public void setDefaultSitemapUrl(String url) {
		Editor edit = prefs.edit();
		edit.putString(getString(R.string.pref_default_sitemap_url_key), url);
		edit.commit();
	}

	public long getCommandSendingDelay() {
		return 1000;
	}

	private String getString(int resId) {
		return ctx.getString(resId);
	}
}
