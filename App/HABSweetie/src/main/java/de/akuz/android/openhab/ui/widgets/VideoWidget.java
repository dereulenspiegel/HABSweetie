package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.VideoView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;

public class VideoWidget extends BasicOpenHABWidget {

	private final static String TAG = VideoWidget.class.getSimpleName();

	private VideoView videoView;

	public VideoWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.video_widget);
		super.buildUi();
		videoView = findView(R.id.videoView);
	}

	@Override
	protected void widgetUpdated(Widget widget) {
		super.widgetUpdated(widget);
		Log.d(TAG, "Starting videoplayback for widget " + widget.getFullUrl());
		if (!this.widget.getFullUrl().equals(widget.getFullUrl())) {
			Log.d(TAG, "Stopping playback");
			videoView.stopPlayback();
		}
		if (!videoView.isPlaying()) {
			Log.d(TAG, "Setting video uri and starting playback");
			videoView.setVideoURI(Uri.parse(widget.getFullUrl()));
			videoView.start();
		}
	}

	@Override
	public void updateItem(Item item) {
		// Ignore
	}

	@Override
	protected void onDetachedFromWindow() {
		videoView.stopPlayback();
		super.onDetachedFromWindow();
	}

}
