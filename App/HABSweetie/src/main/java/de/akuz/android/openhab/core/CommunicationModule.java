package de.akuz.android.openhab.core;

import javax.inject.Named;
import javax.inject.Singleton;

import android.content.Context;

import com.octo.android.robospice.SpiceManager;

import dagger.Module;
import dagger.Provides;
import de.akuz.android.openhab.core.atmosphere.PageAtmosphereXMLConnection;
import de.akuz.android.openhab.settings.wizard.steps.ConnectionWizardConnectionSettingsStep;
import de.akuz.android.openhab.tasker.EditItemActionFragment;
import de.akuz.android.openhab.tasker.EditTaskerActionActivity;
import de.akuz.android.openhab.tasker.SelectInstanceDialog;
import de.akuz.android.openhab.tasker.SelectInstanceDialog.InstanceListAdapter;
import de.akuz.android.openhab.tasker.SelectItemFragment;
import de.akuz.android.openhab.tasker.TaskerActionService;
import de.akuz.android.openhab.ui.BaseActivity;
import de.akuz.android.openhab.ui.BaseFragment;
import de.akuz.android.openhab.ui.EditInstanceFragment;
import de.akuz.android.openhab.ui.ExpandableInstanceListAdapter;
import de.akuz.android.openhab.ui.ManageInstancesActivity;
import de.akuz.android.openhab.ui.ManageInstancesFragment;
import de.akuz.android.openhab.ui.PageActivity;
import de.akuz.android.openhab.ui.PageFragment;

@Module(injects = { BaseActivity.class, BaseFragment.class, PageFragment.class,
		PageAtmosphereXMLConnection.class, PageActivity.class,
		ManageInstancesActivity.class, ManageInstancesFragment.class,
		ExpandableInstanceListAdapter.class, EditInstanceFragment.class,
		ConnectionWizardConnectionSettingsStep.class,
		TaskerActionService.class, EditTaskerActionActivity.class,
		SelectItemFragment.class, EditItemActionFragment.class,
		SelectInstanceDialog.class, InstanceListAdapter.class }, complete = false, library = true)
public class CommunicationModule {

	private Context context;

	public CommunicationModule(Context context) {
		this.context = context;
	}

	@Provides
	@Singleton
	public SpiceManager provideSpiceManager() {
		return new SpiceManager(OpenHABRestService.class);
	}

	@Provides
	@Singleton
	@Named(value = "activity")
	public Context provideActivityContext() {
		return context;
	}

	@Provides
	public PageConnectionInterface providePageConnection(
			SpiceManager spiceManager) {
		return new PageAtmosphereXMLConnection(spiceManager);
	}

}
