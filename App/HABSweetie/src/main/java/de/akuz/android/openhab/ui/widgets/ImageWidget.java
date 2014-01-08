package de.akuz.android.openhab.ui.widgets;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Named;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import dagger.ObjectGraph;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.ui.ImageViewDialog;
import de.akuz.android.openhab.util.ImageLoadHelper;
import de.akuz.android.openhab.util.ImageLoadHelper.ImageLoadListener;

public class ImageWidget extends BasicOpenHABWidget implements
		ImageLoadListener {

	private ImageView imageView;

	private Timer refreshTimer;

	private ProgressBar imageLoadingBar;

	@Inject
	ImageLoadHelper imageLoader;

	@Inject
	@Named("ui")
	ObjectGraph objectGraph;

	@Inject
	FragmentManager fragmentManager;

	public ImageWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.image_widget);
		super.buildUi();

		imageView = findView(R.id.imageView);
		imageView.setVisibility(View.GONE);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageViewDialog dialog = ImageViewDialog.create(getImageUrl());
				objectGraph.inject(dialog);
				dialog.show(fragmentManager, "imageDialog");
			}
		});

		imageLoadingBar = findView(R.id.imageLoadingBar);
		imageLoadingBar.setIndeterminate(true);
	}

	@Override
	public void widgetUpdated(Widget widget) {
		super.widgetUpdated(widget);
		Log.d(TAG, "Loading image from URL " + widget.getFullUrl());
		imageLoadingBar.setVisibility(View.VISIBLE);
		imageLoader.loadImageAsync(getImageUrl(), this);

	}

	protected String getImageUrl() {
		return widget.getFullUrl();
	}

	private void stopRefreshTimer() {
		if (refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
		}
	}

	private void startRefreshTimer() {
		if (refreshTimer != null) {
			stopRefreshTimer();
		}
		refreshTimer = new Timer();
		refreshTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				imageView.post(new Runnable() {

					@Override
					public void run() {
						imageLoader.displayImage(getImageUrl(), imageView);
					}

				});

			}
		}, widget.getRefresh(), widget.getRefresh());
	}

	@Override
	public void updateItem(Item item) {
		// Ignore

	}

	@Override
	public void imageLoaded(Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
		imageView.setVisibility(View.VISIBLE);
		imageLoadingBar.setVisibility(View.GONE);
		if (widget.getRefresh() != null) {
			startRefreshTimer();
		}
	}

	@Override
	public void errorOccured(Throwable t) {
		// TODO probably show an error image
		imageLoadingBar.setVisibility(View.GONE);
	}
}
