package de.akuz.android.openhab.settings.wizard.steps;

import roboguice.util.temp.Strings;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;

@SuppressLint("ValidFragment")
public class ConnectionWizardConnectionSettingsStep extends
		AbstractConnectionWizardStep implements OnCheckedChangeListener {

	private boolean internal;

	private TextView header;
	private TextView introduction;
	private TextView helpUrl;

	private EditText editUrl;
	private EditText editUsername;
	private EditText editPassword;

	private CheckBox authenticateCheckBox;
	private CheckBox useWebsocketsCheckBox;

	private OpenHABConnectionSettings settings;

	@SuppressLint("ValidFragment")
	public ConnectionWizardConnectionSettingsStep(boolean internal) {
		this.internal = internal;
	}

	@Override
	protected void buildUi(Bundle savedInstanceState) {
		setLayout(R.layout.connection_wizard_connection_step);
		header = findView(R.id.textViewHeader);
		introduction = findView(R.id.textViewIntroduction);
		helpUrl = findView(R.id.textViewHelpUrl);
		if (internal) {
			header.setText(R.string.connection_wizard_internal_header);
			introduction
					.setText(R.string.connection_wizard_internal_introduction);
			helpUrl.setText(R.string.connection_wizard_internal_help_url);
		} else {
			header.setText(R.string.connection_wizard_external_header);
			introduction
					.setText(R.string.connection_wizard_external_introduction);
			helpUrl.setText(R.string.connection_wizard_external_help_url);
		}
		editUrl = findView(R.id.editUrl);
		editUsername = findView(R.id.editUsername);
		editPassword = findView(R.id.editPassword);
		editUsername.setEnabled(false);
		editPassword.setEnabled(false);

		authenticateCheckBox = findView(R.id.useAuthnticationCheckBox);
		authenticateCheckBox.setOnCheckedChangeListener(this);
		useWebsocketsCheckBox = findView(R.id.useWebSocketsCheckBox);
	}

	@Override
	protected boolean isValid() {
		// FIXME show error messages
		String url = editUrl.getText().toString();
		if (Strings.isEmpty(url) && internal) {
			return false;
		}
		if (!Strings.isEmpty(url) && !url.startsWith("http")) {
			return false;
		}
		boolean authenticate = authenticateCheckBox.isChecked();
		String username = editUsername.getText().toString();
		String password = editPassword.getText().toString();
		if (authenticate && Strings.isEmpty(username)) {
			return false;
		}
		if (authenticate && Strings.isEmpty(password)) {
			return false;
		}
		return true;
	}

	@Override
	protected void collectValues() {
		settings.setBaseUrl(editUrl.getText().toString());
		settings.setUseWebSockets(useWebsocketsCheckBox.isChecked());
		if (authenticateCheckBox.isChecked()) {
			settings.setUsername(editUsername.getText().toString());
			settings.setPassword(editPassword.getText().toString());
		}

	}

	@Override
	public void onModelBound(Parcelable model) {
		settings = (OpenHABConnectionSettings) model;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		editUsername.setEnabled(isChecked);
		editPassword.setEnabled(isChecked);

	}

}
