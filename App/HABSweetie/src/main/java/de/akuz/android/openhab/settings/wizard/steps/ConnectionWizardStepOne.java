package de.akuz.android.openhab.settings.wizard.steps;

import roboguice.util.temp.Strings;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;

import org.codepond.wizardroid.persistence.ContextVariable;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABInstance;

public class ConnectionWizardStepOne extends AbstractConnectionWizardStep {

	private final static String TAG = ConnectionWizardStepOne.class
			.getSimpleName();

	private EditText editName;



	@Override
	protected void buildUi(Bundle savedInstanceState) {
		setLayout(R.layout.connection_wizard_step_1);
		editName = findView(R.id.editName);
	}

	@Override
	protected boolean isValid() {
		return !Strings.isEmpty(editName.getText().toString());
	}

	@Override
	protected void collectValues() {
		instance.setName(editName.getText().toString());
	}

}
