package de.akuz.android.openhab.settings.wizard;

import javax.inject.Inject;

import org.codepond.android.wizardroid.WizardActivity;
import org.codepond.android.wizardroid.WizardFlow;

import android.os.Bundle;
import android.util.Log;
import de.akuz.android.openhab.BootstrapApplication;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.settings.wizard.steps.ConnectionWizardConnectionSettingsStep;
import de.akuz.android.openhab.settings.wizard.steps.ConnectionWizardStepOne;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class ConnectionWizardActivity extends WizardActivity {

	private final static String TAG = ConnectionWizardActivity.class
			.getSimpleName();

	private OpenHABInstance openHABInstance = new OpenHABInstance();;

	private OpenHABConnectionSettings internalSettings = new OpenHABConnectionSettings();
	private OpenHABConnectionSettings externalSettings = new OpenHABConnectionSettings();

	@Inject
	HABSweetiePreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((BootstrapApplication) getApplication()).getObjectGraph().inject(this);
		setContentView(R.layout.connection_wizard_activity);
	}

	@Override
	public void onSetup(WizardFlow flow) {
		WizardFlow.Builder builder = new WizardFlow.Builder();
		builder.setContainerId(R.id.step_container);
		builder.setActivity(this);
		builder.addStep(new ConnectionWizardStepOne(), openHABInstance);
		builder.addStep(new ConnectionWizardConnectionSettingsStep(true),
				internalSettings);
		builder.addStep(new ConnectionWizardConnectionSettingsStep(false),
				externalSettings);
		flow = builder.create();
		super.onSetup(flow);
	}

	@Override
	public void onWizardDone() {
		openHABInstance.setExternal(externalSettings);
		openHABInstance.setInternal(internalSettings);
		prefs.saveInstance(openHABInstance);
		Log.d(TAG, "Name of new instance is " + openHABInstance.getName());
		if (prefs.getAllConfiguredInstances().size() == 1) {
			Log.d(TAG,
					"The added instance is the only instance configured, setting as default");
			prefs.setDefaultOpenHABInstanceId(openHABInstance.getId());
		}
		finish();
	}

}
