package de.akuz.android.openhab.util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.settings.OpenHABSQLLiteHelper;

public class HABSweetiePreferences {

	private SharedPreferences prefs;

	private Context ctx;

	private SQLiteDatabase db;

	@Inject
	public HABSweetiePreferences(Context ctx, OpenHABSQLLiteHelper sqlHelper) {
		this.ctx = ctx;
		db = sqlHelper.getWritableDatabase();
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

	public void setDefaultOpenHABInstanceId(long id) {
		Editor edit = prefs.edit();
		edit.putLong(getString(R.string.pref_default_config_id), id);
		edit.commit();
	}

	public long getDefaultOpenHABInstanceId() {
		return prefs.getLong(getString(R.string.pref_default_config_id), -1);
	}

	public OpenHABInstance getDefaultOpenHABInstance() {
		long id = getDefaultOpenHABInstanceId();
		if (id > -1) {
			return loadInstance(id);
		}
		return null;
	}

	public OpenHABInstance loadInstance(long id) {
		if (id < 0) {
			return null;
		}
		OpenHABInstance instance = cupboard().withDatabase(db).get(
				OpenHABInstance.class, id);
		OpenHABConnectionSettings internalSettings = cupboard()
				.withDatabase(db).get(instance.getInternal());
		instance.setInternal(internalSettings);
		OpenHABConnectionSettings externalSettings = cupboard()
				.withDatabase(db).get(instance.getExternal());
		instance.setExternal(externalSettings);
		return instance;
	}

	public void saveInstance(OpenHABInstance instance) {
		OpenHABConnectionSettings intern = instance.getInternal();
		OpenHABConnectionSettings extern = instance.getExternal();
		cupboard().withDatabase(db).put(intern, extern);
		cupboard().withDatabase(db).put(instance);
	}

	public List<OpenHABInstance> getAllConfiguredInstances() {
		QueryResultIterable<OpenHABInstance> iterable = cupboard()
				.withDatabase(db).query(OpenHABInstance.class).query();
		List<OpenHABInstance> instanceList = new ArrayList<OpenHABInstance>();
		for (OpenHABInstance i : iterable) {
			i = loadInstance(i.getId());
			instanceList.add(i);
		}
		return instanceList;
	}

	public OpenHABInstance getDefaultInstance() {
		return loadInstance(getDefaultOpenHABInstanceId());
	}

	public long getCommandSendingDelay() {
		int seconds = Integer.parseInt(prefs.getString(
				getString(R.string.pref_send_delay_key), "1"));
		return (seconds * 1000);
	}

	private String getString(int resId) {
		return ctx.getString(resId);
	}
}
