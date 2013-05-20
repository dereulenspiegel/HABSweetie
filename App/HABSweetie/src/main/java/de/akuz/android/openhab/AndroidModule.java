package de.akuz.android.openhab;

import javax.inject.Singleton;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module(library = true)
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

}
