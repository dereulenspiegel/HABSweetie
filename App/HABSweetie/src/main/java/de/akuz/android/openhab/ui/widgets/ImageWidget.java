package de.akuz.android.openhab.ui.widgets;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;

public class ImageWidget extends BasicOpenHABWidget {

	private ImageView imageView;

	private Timer refreshTimer;

	@Inject
	ImageLoader imageLoader;

	public ImageWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.image_widget);
		super.buildUi();
		imageView = findView(R.id.imageView);
	}

	@Override
	public void widgetUpdated(Widget widget) {
		super.widgetUpdated(widget);
		if (widget.getIcon() == null && widget.getLabel() == null) {
			hideHeader(true);
		} else {
			hideHeader(false);
		}
		Log.d(TAG, "Loading image from URL " + widget.getFullUrl());
		imageLoader.displayImage(widget.getFullUrl(), imageView);
		if (widget.getRefresh() != null) {
			startRefreshTimer();
		}
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
				ImageLoader.getInstance().displayImage(widget.getFullUrl(),
						imageView);
			}
		}, widget.getRefresh(), widget.getRefresh());
	}

	@Override
	public void updateItem(Item item) {
		// Ignore

	}
}
