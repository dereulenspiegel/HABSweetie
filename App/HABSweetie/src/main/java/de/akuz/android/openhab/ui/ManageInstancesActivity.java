package de.akuz.android.openhab.ui;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.settings.wizard.steps.ConnectionWizardConnectionSettingsStep;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class ManageInstancesActivity extends BaseActivity {

	@Inject
	HABSweetiePreferences prefs;

	private ManageInstancesFragment manageInstanceFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_instances_activity);
		manageInstanceFragment = new ManageInstancesFragment();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fragmentContainer, manageInstanceFragment);
		ft.commit();

	}

	public void showInstanceDetails(OpenHABInstance instance) {
		EditInstanceFragment fragment = EditInstanceFragment.build(instance
				.getId());
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		inject(fragment);
		ft.replace(R.id.fragmentContainer, fragment);
		ft.commit();
	}

	public void showConnectionSettings(boolean internal,
			OpenHABConnectionSettings conSettings) {
		ConnectionWizardConnectionSettingsStep fragment = ConnectionWizardConnectionSettingsStep
				.build(internal, conSettings);
		inject(fragment);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fragmentContainer, fragment);
		ft.commit();
	}

	public void backToOverview() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ManageInstancesFragment fragment = new ManageInstancesFragment();
		inject(fragment);
		ft.replace(R.id.fragmentContainer, fragment);
		ft.commit();
	}
}
