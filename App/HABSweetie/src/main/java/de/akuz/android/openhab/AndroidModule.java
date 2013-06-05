package de.akuz.android.openhab;

import javax.inject.Singleton;

import android.content.Context;
import android.net.ConnectivityManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.OkHttpClient;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import de.akuz.android.openhab.core.OpenHABRestService;
import de.akuz.android.openhab.core.http.OkHttpTransport;
import de.akuz.android.openhab.settings.wizard.ConnectionWizardActivity;
import de.akuz.android.openhab.ui.ChooseSitemapDialogFragment;
import de.akuz.android.openhab.ui.WidgetListAdapter;
import de.akuz.android.openhab.ui.widgets.AbstractOpenHABWidget;
import de.akuz.android.openhab.ui.widgets.BasicOpenHABWidget;
import de.akuz.android.openhab.ui.widgets.ChartWidget;
import de.akuz.android.openhab.ui.widgets.ColorpickerWidget;
import de.akuz.android.openhab.ui.widgets.FrameWidget;
import de.akuz.android.openhab.ui.widgets.ImageWidget;
import de.akuz.android.openhab.ui.widgets.OpenHABWidgetFactory;
import de.akuz.android.openhab.ui.widgets.SelectionWidget;
import de.akuz.android.openhab.ui.widgets.SetpointWidget;
import de.akuz.android.openhab.ui.widgets.SliderWidget;
import de.akuz.android.openhab.ui.widgets.SwitchWidget;
import de.akuz.android.openhab.ui.widgets.TextWidget;
import de.akuz.android.openhab.ui.widgets.VideoWidget;
import de.akuz.android.openhab.ui.widgets.WebviewWidget;
import de.akuz.android.openhab.util.AuthenticatedHttpImageDownloader;

@Module(library = true, injects = { BootstrapApplication.class,
		WidgetListAdapter.class, OpenHABWidgetFactory.class, FrameWidget.class,
		TextWidget.class, AbstractOpenHABWidget.class, ImageWidget.class,
		SliderWidget.class, SwitchWidget.class, SelectionWidget.class,
		ColorpickerWidget.class, BasicOpenHABWidget.class,
		SetpointWidget.class, WebviewWidget.class, VideoWidget.class,
		ChartWidget.class, ChooseSitemapDialogFragment.class,
		OpenHABRestService.class, OkHttpTransport.class,
		AuthenticatedHttpImageDownloader.class, ConnectionWizardActivity.class })
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
	public OpenHABWidgetFactory provideWidgetFactory(ObjectGraph objectGraph) {
		return new OpenHABWidgetFactory(objectGraph);
	}

	@Provides
	@Singleton
	public ObjectGraph provideObjectGraph() {
		return app.getObjectGraph();
	}

	// @Provides
	// public SQLiteOpenHelper provideSQLiteOpenHelper(Context ctx) {
	// return new OpenHABSQLLiteHelper(ctx);
	// }

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
		return (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

}
