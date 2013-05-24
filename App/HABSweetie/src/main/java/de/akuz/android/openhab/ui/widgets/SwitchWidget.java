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

	private String mappingOn = "ON";
	private String mappingOff = "OFF";

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
				if (m.getCommand().equals(mappingOn)) {
					on = m.getLabel();
				}
				if (m.getCommand().equals(mappingOff)) {
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
		if (mappingOn.equals(state)) {
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
		sendCommand(getCommand(isChecked));

	}

	private String getCommand(boolean onOff) {
		// FIXME: Somewhat hackish. We should be able to map from itemtype to
		// available commands
		if (onOff) {
			return mappingOn;
		} else {
			return mappingOff;
		}
	}

	@Override
	public void updateItem(Item item) {
		if ("RollershutterItem".equals(item.type)) {
			mappingOff = "0";
			mappingOn = "100";
		}
		updateActionView(item);

	}

}
