package de.akuz.android.openhab.util;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class AdapterViewHelper {

	public static void updateSelection(final AdapterView<?> view, int selection, final OnItemSelectedListener listener) {
		view.setOnItemSelectedListener(null);
		view.setSelection(selection);
		view.post(new Runnable() {

			@Override
			public void run() {
				if (listener != null) {
					Log.d("SelectionWidget", "Resetting listener");
					view.setOnItemSelectedListener(listener);
				} else {
					Log.d("SelectionWidget", "Listener was NULL, not resetting");
				}

			}
		});
	}
}
