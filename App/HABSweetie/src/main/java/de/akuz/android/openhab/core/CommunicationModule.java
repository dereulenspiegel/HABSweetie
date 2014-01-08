package de.akuz.android.openhab.core;

import javax.inject.Singleton;

import android.net.ConnectivityManager;

import com.octo.android.robospice.SpiceManager;

import dagger.Module;
import dagger.Provides;
import de.akuz.android.openhab.core.atmosphere.PageAtmosphereXMLConnection;
import de.akuz.android.openhab.core.spice.OpenHABRestService;
import de.akuz.android.openhab.core.spice.OpenHABSpiceRestService;
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
import de.akuz.android.openhab.ui.ManageInstancesFragment;
import de.akuz.android.openhab.ui.PageActivity;
import de.akuz.android.openhab.ui.PageFragment;
import de.akuz.android.openhab.util.HABSweetiePreferences;

@Module(injects = { BaseActivity.class, BaseFragment.class, PageFragment.class,
		PageAtmosphereXMLConnection.class, PageActivity.class,
		ManageInstancesFragment.class, ExpandableInstanceListAdapter.class,
		EditInstanceFragment.class, TaskerActionService.class,
		EditTaskerActionActivity.class, SelectItemFragment.class,
		EditItemActionFragment.class, SelectInstanceDialog.class,
		InstanceListAdapter.class }, complete = false, library = false)
public class CommunicationModule {

	public CommunicationModule() {

	}

	@Provides
	@Singleton
	public SpiceManager provideSpiceManager() {
		return new SpiceManager(OpenHABRestService.class);
	}

	@Provides
	public PageConnectionInterface providePageConnection(
			SpiceManager spiceManager, HABSweetiePreferences prefs,
			ConnectivityManager conManager) {
		return new PageAtmosphereXMLConnection(spiceManager, prefs, conManager);
	}

	@Provides
	@Singleton
	public OpenHABAsyncRestInterface provideOpenHABAsyncRestService(
			SpiceManager spiceManager, ConnectivityManager conManager) {
		return new OpenHABSpiceRestService(spiceManager, conManager);
	}

}
