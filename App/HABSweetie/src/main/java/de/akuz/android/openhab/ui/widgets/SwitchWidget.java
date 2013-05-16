package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.core.objects.Widget.Mapping;

public class SwitchWidget extends BasicOpenHABWidget implements
		OnCheckedChangeListener {

	private Switch switchWidget;

	public SwitchWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.switch_widget);
		super.buildUi();
		switchWidget = findView(R.id.switch1);
		if (widget.getMapping() != null) {
			String on = null;
			String off = null;
			for (Mapping m : widget.getMapping()) {
				if (m.getCommand().equals("ON")) {
					on = m.getLabel();
				}
				if (m.getCommand().equals("OFF")) {
					off = m.getLabel();
				}
			}
			if (on != null) {
				switchWidget.setTextOn(on);
			}
			if (off != null) {
				switchWidget.setTextOff(off);
			}
		}
		switchWidget.setOnCheckedChangeListener(this);
	}

	public void updateActionView(Item item) {
		if (item == null) {
			return;
		}
		String state = item.state;
		switchWidget.setOnCheckedChangeListener(null);
		if ("ON".equals(state)) {
			switchWidget.setChecked(true);
		} else {
			switchWidget.setChecked(false);
		}
		switchWidget.post(new Runnable() {

			@Override
			public void run() {
				switchWidget.setOnCheckedChangeListener(SwitchWidget.this);
			}

		});

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			sendCommand("ON");
		} else {
			sendCommand("OFF");
		}

	}

	@Override
	public void updateItem(Item item) {
		updateActionView(item);

	}

}
