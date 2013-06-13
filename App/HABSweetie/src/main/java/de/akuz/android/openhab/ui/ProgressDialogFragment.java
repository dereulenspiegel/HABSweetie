package de.akuz.android.openhab.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {

	public final static String MESSAGE_ARG = "message";

	private String message;

	public static ProgressDialogFragment build(String message) {
		Bundle args = new Bundle();
		args.putString(MESSAGE_ARG, message);
		ProgressDialogFragment fragment = build();
		fragment.setArguments(args);
		return fragment;
	}

	public static ProgressDialogFragment build() {
		ProgressDialogFragment fragment = new ProgressDialogFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		message = getArguments().getString(MESSAGE_ARG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);
		dialog.setCanceledOnTouchOutside(false);
		if (message != null) {
			dialog.setMessage(message);
		}
		return dialog;
	}

}
