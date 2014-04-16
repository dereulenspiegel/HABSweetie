package de.akuz.android.openhab.settings.wizard;

import javax.inject.Inject;

import android.os.Bundle;
import android.util.Log;
import de.akuz.android.openhab.BootstrapApplication;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.settings.wizard.steps.ConnectionWizardConnectionSettingsStep;
import de.akuz.android.openhab.settings.wizard.steps.ConnectionWizardStepOne;
import de.akuz.android.openhab.ui.BaseActivity;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class ConnectionWizardActivity extends BaseActivity {

	private final static String TAG = ConnectionWizardActivity.class
			.getSimpleName();

	@Inject
	HABSweetiePreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection_wizard_activity);
	}

}
