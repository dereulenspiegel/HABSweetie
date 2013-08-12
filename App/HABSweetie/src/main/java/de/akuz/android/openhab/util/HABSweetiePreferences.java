package de.akuz.android.openhab.util;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.qbusict.cupboard.QueryResultIterable;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.settings.OpenHABSQLLiteHelper;

public class HABSweetiePreferences {

	private final static String TAG = HABSweetiePreferences.class
			.getSimpleName();

	private SharedPreferences prefs;

	private Context ctx;

	private SQLiteDatabase db;

	private Map<Long, OpenHABInstance> cachedInstances = new WeakHashMap<Long, OpenHABInstance>();

	@Inject
	@Singleton
	public HABSweetiePreferences(Context ctx) {
		this.ctx = ctx;
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

	}

	public boolean keepScreenOn() {
		return prefs.getBoolean(getString(R.string.pref_keep_screen_on_key),
				false);
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
			return loadInstance(id, true);
		}
		return null;
	}

	public OpenHABInstance loadInstance(long id) {
		return loadInstance(id, true);
	}

	private OpenHABInstance loadInstance(long id, boolean openDatabase) {
		if (openDatabase) {
			open();
		}
		if (id < 0) {
			return null;
		}
		if (cachedInstances.containsKey(id)) {
			return cachedInstances.get(id);
		}
		OpenHABInstance instance = cupboard().withDatabase(db).get(
				OpenHABInstance.class, id);
		if (instance == null) {
			Log.w(TAG, "Tried to load an instance but became NULL! ID: " + id);
			return null;
		}
		OpenHABConnectionSettings internalSettings = cupboard()
				.withDatabase(db).get(instance.getInternal());
		instance.setInternal(internalSettings);
		OpenHABConnectionSettings externalSettings = cupboard()
				.withDatabase(db).get(instance.getExternal());
		instance.setExternal(externalSettings);
		cachedInstances.put(id, instance);
		if (openDatabase) {
			close();
		}
		return instance;
	}

	public void saveInstance(OpenHABInstance instance) {
		open();
		OpenHABConnectionSettings intern = instance.getInternal();
		OpenHABConnectionSettings extern = instance.getExternal();
		cupboard().withDatabase(db).put(intern, extern);
		cupboard().withDatabase(db).put(instance);
		close();
	}

	public List<OpenHABInstance> getAllConfiguredInstances() {
		open();
		QueryResultIterable<OpenHABInstance> iterable = cupboard()
				.withDatabase(db).query(OpenHABInstance.class).query();
		List<OpenHABInstance> instanceList = new ArrayList<OpenHABInstance>();
		for (OpenHABInstance i : iterable) {
			i = loadInstance(i.getId(), false);
			instanceList.add(i);
		}
		Log.d(TAG, " Currently " + instanceList.size() + " configs in database");
		close();
		return instanceList;
	}

	public void saveConnectionSettings(OpenHABConnectionSettings settings) {
		open();
		cupboard().withDatabase(db).put(settings);
		close();
	}

	public OpenHABInstance getDefaultInstance() {
		return loadInstance(getDefaultOpenHABInstanceId());
	}

	public void removeOpenHABInstance(OpenHABInstance instance) {
		open();
		cupboard().withDatabase(db).delete(instance.getExternal());
		cupboard().withDatabase(db).delete(instance.getInternal());
		cupboard().withDatabase(db).delete(instance);
		close();
	}

	public long getCommandSendingDelay() {
		int seconds = Integer.parseInt(prefs.getString(
				getString(R.string.pref_send_delay_key), "1"));
		return (seconds * 1000);
	}

	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	public void open() {
		db = new OpenHABSQLLiteHelper(ctx).getWritableDatabase();
	}

	private String getString(int resId) {
		return ctx.getString(resId);
	}
}
