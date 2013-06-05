package de.akuz.android.openhab.settings.wizard.steps;

import org.codepond.android.wizardroid.WizardStep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import de.akuz.android.openhab.R;

public abstract class AbstractConnectionWizardStep extends WizardStep implements
		OnClickListener {

	protected Button nextButton;

	private View rootView;
	private ViewGroup containerView;
	private LayoutInflater layoutInflater;

	protected final String key = String.format("%s#WizardStepModel", getClass()
			.getName());

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layoutInflater = inflater;
		containerView = container;
		nextButton = (Button) getActivity().findViewById(R.id.next_button);
		nextButton.setOnClickListener(this);
		buildUi(savedInstanceState);
		return rootView;
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T findView(int id) {
		return (T) rootView.findViewById(id);
	}

	protected abstract void buildUi(Bundle savedInstanceState);

	protected abstract boolean isValid();

	protected abstract void collectValues();

	protected void setLayout(int layoutId) {
		rootView = layoutInflater.inflate(layoutId, containerView, false);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == nextButton.getId() && isValid()) {
			collectValues();
			done();
		}
	}

}
