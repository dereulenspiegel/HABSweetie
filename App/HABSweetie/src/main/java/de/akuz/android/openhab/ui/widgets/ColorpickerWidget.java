package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageButton;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.ui.widgets.ColorPickerDialog.ColorSelectedListener;

public class ColorpickerWidget extends BasicOpenHABWidget implements
		ColorSelectedListener {

	private ImageButton colorView;

	public ColorpickerWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.rgb_widget);
		super.buildUi();

		colorView.setOnClickListener(this);
	}

	@Override
	public void updateItem(Item item) {
		String colorState = item.state;
		String[] hsvStrings = colorState.split(",");
		int[] hsv = new int[3];
		for (int i = 0; i < hsvStrings.length; i++) {
			hsv[i] = Integer.parseInt(hsvStrings[i]);
		}
		int color = Color.HSVToColor(new float[] { hsv[0], hsv[1] / 100,
				hsv[2] / 100 });
		ColorDrawable drawable = new ColorDrawable(color);
		colorView.setImageDrawable(drawable);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == colorView.getId()) {
			ColorPickerDialog dialog = ColorPickerDialog.build(this);
			commandInterface.showDialog(dialog);
		} else {
			super.onClick(v);
		}
	}

	@Override
	public void colorSelected(int color) {
		ColorDrawable drawable = new ColorDrawable(color);
		colorView.setImageDrawable(drawable);
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		StringBuffer buf = new StringBuffer();
		buf.append(hsv[0]);
		buf.append(',');
		buf.append(hsv[1] * 100);
		buf.append(',');
		buf.append(hsv[2] * 100);
		sendCommand(buf.toString());
	}

	@Override
	public void selectionCanceled() {
		// TODO Auto-generated method stub

	}

}
