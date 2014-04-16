package de.akuz.android.openhab.settings.wizard;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.wizard.steps.ConnectionWizardStepOne;
import de.akuz.android.openhab.settings.wizard.steps.ExternalConnectionWizardStep;
import de.akuz.android.openhab.settings.wizard.steps.InternalConnectionWizardStep;

/**
 * Created by till on 11.04.14.
 */
public class ConnectionWizardLayout extends BasicWizardLayout {


    @Override
    public WizardFlow onSetup() {

        setNextButtonText(getString(R.string.connection_wizard_next));
        setBackButtonText(getString(R.string.connection_wizard_back));
        setFinishButtonText(getString(R.string.connection_wizard_finish));

        WizardFlow.Builder builder = new WizardFlow.Builder();
        builder.addStep(ConnectionWizardStepOne.class,true);
        // TODO create the next two steps
        builder.addStep(InternalConnectionWizardStep.class,true);
        builder.addStep(ExternalConnectionWizardStep.class, true);
        return builder.create();
    }
}
