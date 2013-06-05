package de.akuz.android.openhab.settings;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import javax.inject.Inject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHABSQLLiteHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "configuredSites";
	private final static int DATABASE_VERSION = 1;

	static {
		cupboard().register(OpenHABConnectionSettings.class);
		cupboard().register(OpenHABInstance.class);
	}

	@Inject
	public OpenHABSQLLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		cupboard().withDatabase(db).createTables();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		cupboard().withDatabase(db).upgradeTables();

	}

}
