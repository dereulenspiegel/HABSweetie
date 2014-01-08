package de.akuz.android.openhab;

import java.net.URL;

import javax.inject.Inject;

import roboguice.util.temp.Ln;
import android.app.Application;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import dagger.ObjectGraph;
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
import de.akuz.android.openhab.util.ImageLoadHelper;
import de.duenndns.ssl.MemorizingTrustManager;
import de.duenndns.ssl.TrustManagerException;

/**
 * HABsweetie application
 */
public class BootstrapApplication extends Application {

	private static BootstrapApplication instance;

	private MemorizingTrustManager trustManager;

	private ObjectGraph objectGraph;

	@Inject
	ImageLoadHelper imageLoadHelper;

	@Inject
	OkHttpClient okHttpClient;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		objectGraph = ObjectGraph.create(new AndroidModule(this));
		objectGraph.inject(this);


		// register MemorizingTrustManager for HTTPS
		try {
			MemorizingTrustManager.registerTrustManager(this);
		} catch (TrustManagerException e) {
			Log.e("openHAB",
					"Can't register custom TrustManager to handle SSL exceptions",
					e);
		}
		URL.setURLStreamHandlerFactory(okHttpClient);

		OpenHABWidgetFactory.registerWidgetType("Switch", SwitchWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Frame", FrameWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Text", TextWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Slider", SliderWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Setpoint", SetpointWidget.class);
		OpenHABWidgetFactory
				.registerWidgetType("Colorpicker", ColorpickerWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Selection", SelectionWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Image", ImageWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Video", VideoWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Webview", WebviewWidget.class);
		OpenHABWidgetFactory.registerWidgetType("Chart", ChartWidget.class);

		imageLoadHelper.initialize(getApplicationContext());
		Ln.getConfig().setLoggingLevel(Log.ERROR);
	}

	public ObjectGraph getObjectGraph() {
		return objectGraph;
	}

	public MemorizingTrustManager getMemorizingTrustManager() {
		return trustManager;
	}

	public static BootstrapApplication getInstance() {
		return instance;
	}
}
