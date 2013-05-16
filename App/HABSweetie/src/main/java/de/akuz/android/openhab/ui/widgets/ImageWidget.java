package de.akuz.android.openhab.ui.widgets;

import java.util.Timer;
import java.util.TimerTask;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.widget.ImageView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;

public class ImageWidget extends BasicOpenHABWidget {

	private ImageView imageView;

	private Timer refreshTimer;

	public ImageWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.image_widget);
		super.buildUi();
		findView(R.id.imageView);
	}

	@Override
	public void widgetUpdated(Widget widget) {
		super.widgetUpdated(widget);
		ImageLoader.getInstance().displayImage(widget.getUrl(), imageView);
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
				ImageLoader.getInstance().displayImage(widget.getUrl(),
						imageView);
			}
		}, widget.getRefresh(), widget.getRefresh());
	}

	@Override
	public void updateItem(Item item) {
		// Ignore

	}
}
