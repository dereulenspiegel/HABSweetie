package de.akuz.android.openhab.settings.wizard.steps;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.ui.BaseActivity;

public abstract class AbstractConnectionWizardStep extends WizardStep {

    private final static String TAG = AbstractConnectionWizardStep.class.getSimpleName();

	protected Button nextButton;

	private View rootView;
	private ViewGroup containerView;
	private LayoutInflater layoutInflater;

	protected boolean isWizardStep = true;

    @ContextVariable
    protected OpenHABInstance instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity activity = (BaseActivity) getActivity();
        activity.inject(this);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layoutInflater = inflater;
		containerView = container;
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
    public void onExit(int exitCode) {
        switch(exitCode){
            case WizardStep.EXIT_NEXT:
                if(isValid()){
                    collectValues();
                }
                break;
            case WizardStep.EXIT_PREVIOUS:
                break;
            default:
                Log.w(TAG, "Unknown exit code "+exitCode);
        }
    }
}
