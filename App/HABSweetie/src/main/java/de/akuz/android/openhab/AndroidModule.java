package de.akuz.android.openhab;

import javax.inject.Singleton;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.akuz.android.openhab.ui.WidgetListAdapter;
import de.akuz.android.openhab.ui.widgets.OpenHABWidgetFactory;

@Module(library = true, injects={BootstrapApplication.class, WidgetListAdapter.class})
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
	public OpenHABWidgetFactory provideWidgetFactory() {
		return OpenHABWidgetFactory.getInstance();
	}

}
