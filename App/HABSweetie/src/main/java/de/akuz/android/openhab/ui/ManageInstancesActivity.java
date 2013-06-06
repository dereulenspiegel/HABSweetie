package de.akuz.android.openhab.ui;

import javax.inject.Inject;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.util.HABSweetiePreferences;
import android.os.Bundle;

public class ManageInstancesActivity extends BaseActivity {

	@Inject
	HABSweetiePreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_instances_activity);
	}

}
