package de.akuz.android.openhab.ui.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;

import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener;

import de.akuz.android.openhab.R;

public class ColorPickerDialog extends DialogFragment implements
		OnColorChangedListener, OnClickListener {

	private ColorPicker colorPicker;

	private ColorSelectedListener listener;

	private int selectedColor;

	public static ColorPickerDialog build(ColorSelectedListener listener) {
		ColorPickerDialog dialog = new ColorPickerDialog();
		dialog.setListener(listener);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View rootView = getActivity().getLayoutInflater().inflate(
				R.layout.colorpicker_dialog, null, false);
		colorPicker = (ColorPicker) rootView.findViewById(R.id.picker);
		colorPicker.setOnColorChangedListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(rootView);
		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, this);

		return builder.create();
	}

	public void setListener(ColorSelectedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onColorChanged(int color) {
		selectedColor = color;

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			colorSelectionFinished();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			colorSelectionCanceled();
			break;
		}

	}

	private void colorSelectionFinished() {
		if (listener != null) {
			listener.colorSelected(selectedColor);
		}
		dismiss();
	}

	private void colorSelectionCanceled() {
		if (listener != null) {
			listener.selectionCanceled();
		}
		dismiss();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (listener != null) {
			listener.selectionCanceled();
		}
		super.onDismiss(dialog);
	}

	public static interface ColorSelectedListener {
		public void colorSelected(int color);

		public void selectionCanceled();
	}

}
