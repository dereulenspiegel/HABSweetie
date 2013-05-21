package de.akuz.android.openhab.core;

import javax.inject.Named;
import javax.inject.Singleton;

import android.app.Activity;
import android.content.Context;

import com.octo.android.robospice.SpiceManager;

import dagger.Module;
import dagger.Provides;
import de.akuz.android.openhab.BootstrapApplication;
import de.akuz.android.openhab.ui.BaseActivity;
import de.akuz.android.openhab.ui.BaseFragment;
import de.akuz.android.openhab.ui.PageActivity;
import de.akuz.android.openhab.ui.PageFragment;

@Module(injects = { BaseActivity.class, BaseFragment.class, PageFragment.class,
		PageXMLConnection.class, PageActivity.class }, complete = false, library = true)
public class CommunicationModule {

	private Activity context;

	public CommunicationModule(Activity context) {
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
	public PageConnectionInterface providePageConnection(PageXMLConnection con) {
		return con;
	}

}
