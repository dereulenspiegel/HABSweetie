package de.akuz.android.openhab.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageLoadHelper {

	public interface ImageLoadListener {
		public void imageLoaded(Bitmap bitmap);

		public void errorOccured(Throwable t);
	}

	public void displayImage(String url, ImageView view);

	public void loadImageAsync(String url, ImageLoadListener listener);
	
	public void initialize(Context ctx);

}
