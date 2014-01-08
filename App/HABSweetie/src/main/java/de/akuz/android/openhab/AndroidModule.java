package de.akuz.android.openhab;

import javax.inject.Named;
import javax.inject.Singleton;

import android.content.Context;
import android.net.ConnectivityManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.OkHttpClient;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import de.akuz.android.openhab.core.CommunicationModule;
import de.akuz.android.openhab.core.spice.OpenHABRestService;
import de.akuz.android.openhab.settings.wizard.ConnectionWizardActivity;
import de.akuz.android.openhab.ui.ChooseSitemapDialogFragment;
import de.akuz.android.openhab.ui.ImageViewDialog;
import de.akuz.android.openhab.ui.ManageInstancesFragment;
import de.akuz.android.openhab.util.ImageLoadHelper;
import de.akuz.android.openhab.util.imageloader.AuthenticatedHttpImageDownloader;
import de.akuz.android.openhab.util.imageloader.UniversalImageLoaderImpl;

@Module(injects = { BootstrapApplication.class,
		ChooseSitemapDialogFragment.class, OpenHABRestService.class,
		AuthenticatedHttpImageDownloader.class, ConnectionWizardActivity.class,
		ManageInstancesFragment.InstanceListAdapter.class,
		ImageViewDialog.class }, complete = true, library = false, includes = { CommunicationModule.class })
public class AndroidModule {

	private final BootstrapApplication app;

	public AndroidModule(BootstrapApplication app) {
		this.app = app;
	}

	@Provides
	@Singleton
	public Context provideApplicationContext() {
		return app;
	}

	@Provides
	@Singleton
	public ImageLoader provideImageLoader() {
		return ImageLoader.getInstance();
	}

	@Provides
	@Singleton
	@Named("app")
	public ObjectGraph provideObjectGraph() {
		return app.getObjectGraph();
	}

	@Provides
	@Singleton
	public OkHttpClient provideOkHttpClient() {
		OkHttpClient client = new OkHttpClient();
		client.setFollowProtocolRedirects(true);
		return client;
	}

	@Provides
	@Singleton
	public ConnectivityManager provideConnectivityManager(Context ctx) {
		return (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	@Provides
	@Singleton
	public ImageLoadHelper provideImageLoadHelper(ImageLoader loader,
			@Named("app") ObjectGraph graph) {
		return new UniversalImageLoaderImpl(loader, graph);
	}

}
