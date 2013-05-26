package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;

public class SetpointWidget extends BasicOpenHABWidget {

	private Button increaseButton;
	private Button decreaseButton;
	private EditText valueText;

	private float value;

	private float step = 0.1f;
	private float max = 100.0f;
	private float min = 0.0f;

	private TextWatcher valueWatcher;

	public SetpointWidget(Context context, Widget widget) {
		super(context, widget);
		valueWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					value = Float.parseFloat(s.toString());
					if (value > max) {
						value = max;

					}
					if (value < min) {
						value = min;
					}
					valueText.removeTextChangedListener(valueWatcher);
					s.replace(0, s.length() - 1, df.format(value));
					valueText.addTextChangedListener(valueWatcher);
					sendCommandDelayed(df.format(value));
				} catch (NumberFormatException e) {
					// Ignore
				}
			}
		};
	}

	@Override
	protected void buildUi() {
		setView(R.layout.setpoint_widget);
		super.buildUi();
		increaseButton = findView(R.id.increaseButton);
		decreaseButton = findView(R.id.decreaseButton);
		valueText = findView(R.id.valueText);

		increaseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				valueText.removeTextChangedListener(valueWatcher);
				if ((value + step) <= max) {
					value += step;
					updateValue();
				}
				valueText.addTextChangedListener(valueWatcher);
			}
		});

		decreaseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				valueText.removeTextChangedListener(valueWatcher);
				if ((value - step) >= min) {
					value -= step;
					updateValue();
				}
				valueText.addTextChangedListener(valueWatcher);
			}
		});
	}

	private void updateValue() {
		String state = df.format(value);
		valueText.setText(state);
		sendCommandDelayed(state);
	}

	@Override
	public void updateItem(Item item) {
		if (item == null) {
			return;
		}
		String valueState = item.state;
		value = Float.parseFloat(valueState);
		if (widget.getStep() != null) {
			step = widget.getStep();
		}
		if (widget.getMaxValue() != null) {
			max = widget.getMaxValue();
		}
		if (widget.getMinValue() != null) {
			min = widget.getMinValue();
		}
		valueText.setText(df.format(value));

	}

}
