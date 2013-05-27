package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.ToggleButton;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Mapping;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.ui.custom.ToggleGroup;

public class SwitchWidget extends BasicOpenHABWidget implements
		OnCheckedChangeListener,
		de.akuz.android.openhab.ui.custom.ToggleGroup.OnCheckedChangeListener {

	private Switch switchWidget;

	private String mappingOn = "ON";
	private String mappingOff = "OFF";

	private RelativeLayout rootLayout;

	private ToggleGroup toggleGroup;

	public SwitchWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.switch_widget);
		super.buildUi();
		switchWidget = findView(R.id.switch1);
		if (doWeHaveMappings()) {
			buildToggleButtons();
		} else {
			switchWidget.setOnCheckedChangeListener(this);
		}
	}

	@Override
	protected void widgetUpdated(Widget widget) {
		super.widgetUpdated(widget);
		if (doWeHaveMappings()) {
			updateToggleButtonMappings();
		}
	}

	private void buildToggleButtons() {
		rootLayout = (RelativeLayout) getWidgetRootView();
		rootLayout.removeView(switchWidget);
		toggleGroup = new ToggleGroup(getContext());
		toggleGroup.setOnCheckedChangeListener(this);
		toggleGroup.setOrientation(LinearLayout.HORIZONTAL);
		android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
		// FIXME Correct layout params so the toggle buttons appear below the
		// widgetBase
		params.width = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
		params.height = android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT;
		params.addRule(RelativeLayout.BELOW, widgetBase.getId());
		// params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rootLayout.addView(toggleGroup, params);
		updateToggleButtonMappings();
	}

	private void updateToggleButtonMappings() {
		toggleGroup.removeAllViews();
		for (Mapping m : widget.getMappings()) {
			// FIXME: toggle buttons should retain their label
			ToggleButton button = new ToggleButton(getContext());
			button.setText(m.getLabel());
			button.setTextOff(m.getLabel());
			button.setTextOn(m.getLabel());
			button.setTag(m.getCommand());
			toggleGroup.addView(button);
		}
	}

	public void updateActionView(Item item) {
		if (item == null) {
			return;
		}
		String state = item.state;
		if (!doWeHaveMappings()) {
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
		} else {
			int childCount = toggleGroup.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View button = toggleGroup.getChildAt(i);
				if (state.equals(button.getTag())) {
					toggleGroup.check(button.getId(), false);
					break;
				}
			}
		}

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

	protected boolean doWeHaveMappings() {
		return widget.getMappings() != null && widget.getMappings().size() > 0;
	}

	@Override
	public void onCheckedChanged(ToggleGroup group, int checkedId) {
		View toggleButton = group.findViewById(checkedId);
		String command = (String) toggleButton.getTag();
		sendCommand(command);

	}

}
