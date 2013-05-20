package de.akuz.android.openhab;

import java.io.File;

import roboguice.util.temp.Ln;
import android.app.Application;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.utils.StorageUtils;

import dagger.ObjectGraph;
import de.akuz.android.openhab.ui.widgets.ColorpickerWidget;
import de.akuz.android.openhab.ui.widgets.FrameWidget;
import de.akuz.android.openhab.ui.widgets.OpenHABWidgetFactory;
import de.akuz.android.openhab.ui.widgets.SelectionWidget;
import de.akuz.android.openhab.ui.widgets.SetpointWidget;
import de.akuz.android.openhab.ui.widgets.SliderWidget;
import de.akuz.android.openhab.ui.widgets.SwitchWidget;
import de.akuz.android.openhab.ui.widgets.TextWidget;
import de.akuz.android.openhab.util.AuthenticatedHttpImageDownloader;
import de.duenndns.ssl.MemorizingTrustManager;
import de.duenndns.ssl.TrustManagerException;

/**
 * HABsweetie application
 */
public class BootstrapApplication extends Application {

	private static BootstrapApplication instance;

	private MemorizingTrustManager trustManager;

	private ObjectGraph objectGraph;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		objectGraph = ObjectGraph.create(new AndroidModule(this));

		// register MemorizingTrustManager for HTTPS
		try {
			MemorizingTrustManager.registerTrustManager(this);
		} catch (TrustManagerException e) {
			Log.e("openHAB",
					"Can't register custom TrustManager to handle SSL exceptions",
					e);
		}

		OpenHABWidgetFactory.getInstance().registerWidgetType("Switch",
				SwitchWidget.class);
		OpenHABWidgetFactory.getInstance().registerWidgetType("Frame",
				FrameWidget.class);
		OpenHABWidgetFactory.getInstance().registerWidgetType("Text",
				TextWidget.class);
		OpenHABWidgetFactory.getInstance().registerWidgetType("Slider",
				SliderWidget.class);
		OpenHABWidgetFactory.getInstance().registerWidgetType("Setpoint",
				SetpointWidget.class);
		OpenHABWidgetFactory.getInstance().registerWidgetType("Colorpicker",
				ColorpickerWidget.class);
		OpenHABWidgetFactory.getInstance().registerWidgetType("Selection",
				SelectionWidget.class);

		initializeImageLoader();
		Ln.getConfig().setLoggingLevel(Log.ERROR);
	}

	public ObjectGraph getObjectGraph() {
		return objectGraph;
	}

	public void initializeImageLoader() {
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());

		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder() //
				// .showStubImage(0)
				// .showImageForEmptyUri(0)
				// .showImageOnFail(0)
				.resetViewBeforeLoading() //
				.cacheInMemory() //
				.cacheOnDisc() //
				.bitmapConfig(Bitmap.Config.RGB_565).build(); //

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()) //
				.discCache(new UnlimitedDiscCache(cacheDir)) //
				.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR) //
				.threadPoolSize(3) //
				.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR) //
				.threadPriority(Thread.NORM_PRIORITY - 1) //
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //
				.memoryCacheSize(2 * 1024 * 1024) //
				.discCacheSize(50 * 1024 * 1024) //
				.discCacheFileCount(100) //
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				//
				.imageDownloader(
						new AuthenticatedHttpImageDownloader(
								getApplicationContext())) //
				.imageDecoder(new BaseImageDecoder()) //
				.defaultDisplayImageOptions(displayOptions) //
				.build(); //
		ImageLoader.getInstance().init(config);
	}

	public MemorizingTrustManager getMemorizingTrustManager() {
		return trustManager;
	}

	public static BootstrapApplication getInstance() {
		return instance;
	}
}