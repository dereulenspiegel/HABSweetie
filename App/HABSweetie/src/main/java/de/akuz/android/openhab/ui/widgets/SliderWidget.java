package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;

public class SliderWidget extends BasicOpenHABWidget implements
		OnSeekBarChangeListener {

	private final static String TAG = SliderWidget.class.getSimpleName();

	private SeekBar slider;

	private float minValue = 0;

	public SliderWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.slider_widget);
		slider = findView(R.id.slider);
		if (widget.getMinValue() != null) {
			minValue = widget.getMinValue();
		}
		super.buildUi();
		slider.setOnSeekBarChangeListener(this);
	}

	private int getMaxValue() {
		float max = 100.0f;
		if (widget.getMaxValue() != null) {
			max = widget.getMaxValue();
		}
		float min = 0.0f;
		if (widget.getMinValue() != null) {
			min = widget.getMinValue();
		}
		float step = 0.1f;
		if (widget.getStep() != null) {
			step = widget.getStep();
		}
		max = ((max - min) * step);
		return (int) max;
	}

	@Override
	public void onProgressChanged(SeekBar seekbar, int progress,
			boolean fromUser) {
		if (fromUser) {
			float userValue = getUserValue();
			String command = null;
			if (userValue == 0.0f) {
				command = "OFF";
			} else if (userValue == 100.0f) {
				command = "ON";
			} else {
				command = df.format(userValue);
			}
			Log.d(TAG, "Sending dimmer command: " + command);
			sendCommandDelayed(command);
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	private float getUserValue() {
		int progress = slider.getProgress();
		if (progress == 0) {
			return minValue;
		}
		float step = 0.1f;
		if (widget.getStep() != null) {
			step = widget.getStep();
		}
		float min = 0.0f;
		if (widget.getMinValue() != null) {
			min = widget.getMinValue();
		}
		return (min + (progress / step));
	}

	@Override
	public void updateItem(Item item) {
		float state = 0.0f;
		try {
			state = Float.parseFloat(item.state);
		} catch (NumberFormatException e) {
			state = 0.0f;
		}
		slider.setMax(getMaxValue());
		slider.setProgress((int) state);

	}
}
