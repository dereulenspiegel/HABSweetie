package de.akuz.android.openhab.util.imageloader;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.utils.StorageUtils;

import dagger.ObjectGraph;
import de.akuz.android.openhab.util.ImageLoadHelper;

public class UniversalImageLoaderImpl implements ImageLoadHelper {

	private ImageLoader imageLoader;
	private static ObjectGraph objectGraph;

	public UniversalImageLoaderImpl(ImageLoader imageLoader,
			ObjectGraph objectGraph) {
		this.imageLoader = imageLoader;
		UniversalImageLoaderImpl.objectGraph = objectGraph;
	}

	@Override
	public void displayImage(String url, ImageView view) {
		imageLoader.displayImage(url, view);
	}

	@Override
	public void loadImageAsync(String url, final ImageLoadListener listener) {
		imageLoader.loadImage(url, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				listener.errorOccured(failReason.getCause());

			}

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				listener.imageLoaded(loadedImage);

			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void initialize(Context ctx) {
		File cacheDir = StorageUtils.getCacheDirectory(ctx);

		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder() //
				// .showStubImage(0)
				// .showImageForEmptyUri(0)
				// .showImageOnFail(0)
				.resetViewBeforeLoading() //
				.cacheInMemory() //
				.cacheOnDisc() //
				.bitmapConfig(Bitmap.Config.RGB_565).build(); //

		AuthenticatedHttpImageDownloader downloader = new AuthenticatedHttpImageDownloader(
				ctx);
		objectGraph.inject(downloader);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				ctx) //
				.discCache(new UnlimitedDiscCache(cacheDir)) //
				.threadPoolSize(3) //
				.threadPriority(Thread.NORM_PRIORITY - 1) //
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //
				.memoryCacheSize(2 * 1024 * 1024) //
				.discCacheSize(50 * 1024 * 1024) //
				.discCacheFileCount(100) //
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				//
				.imageDownloader(downloader) //
				.imageDecoder(new BaseImageDecoder()) //
				.defaultDisplayImageOptions(displayOptions) //
				.build(); //
		ImageLoader.getInstance().init(config);
		
	}

}
