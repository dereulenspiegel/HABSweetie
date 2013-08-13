package de.akuz.android.openhab.util;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class AdapterViewHelper {

	public static void updateSelection(final AdapterView<?> view, int selection) {
		final OnItemSelectedListener listener = view
				.getOnItemSelectedListener();
		view.setOnItemSelectedListener(null);
		view.setSelection(selection);
		view.post(new Runnable() {

			@Override
			public void run() {
				view.setOnItemSelectedListener(listener);

			}
		});
	}
}
