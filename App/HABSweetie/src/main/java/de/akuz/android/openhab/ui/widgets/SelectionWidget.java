package de.akuz.android.openhab.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Mapping;
import de.akuz.android.openhab.core.objects.Widget;

public class SelectionWidget extends BasicOpenHABWidget implements
		OnItemSelectedListener {

	private final static String TAG = SelectionWidget.class.getSimpleName();

	private Spinner selection;

	private ArrayAdapter<Mapping> selectionAdapter;

	public SelectionWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.selection_widget);
		super.buildUi();
		selection = findView(R.id.selection);
		List<Mapping> mappings = widget.getMappings();
		if (mappings == null) {
			mappings = new ArrayList<Mapping>(0);
		} else {
			mappings = new ArrayList<Mapping>(mappings.size());
			mappings.addAll(widget.getMappings());
		}
		selectionAdapter = new ArrayAdapter<Mapping>(getContext(),
				android.R.layout.simple_spinner_item, mappings);
		selection.setOnItemSelectedListener(this);
		selection.setAdapter(selectionAdapter);
	}

	@Override
	public void widgetUpdated(Widget widget) {
		super.widgetUpdated(widget);
		List<Mapping> mappings = widget.getMappings();
		if (mappings != null && mappings.size() > 0) {
			Log.d(TAG, "Updating selection mappings");
			selectionAdapter.clear();
			// addAll requires API Level 11 or above
			for (Mapping m : mappings) {
				selectionAdapter.add(m);
			}
		} else {
			Log.w(TAG, "Received Widget update with 0 mappings");
		}
	}

	@Override
	public void updateItem(Item item) {
		List<Mapping> mappings = widget.getMappings();
		if (mappings == null) {
			return;
		}
		int i = 0;
		for (; i < mappings.size(); i++) {
			if (mappings.get(i).getCommand().equals(item.state)) {
				break;
			}
		}
		if (mappings != null && i < mappings.size()) {
			Log.d(TAG, "Updating selection to " + mappings.get(i).getCommand()
					+ " for mapping label " + mappings.get(i).getLabel());
			final OnItemSelectedListener listener = selection
					.getOnItemSelectedListener();
			selection.setOnItemSelectedListener(null);
			selection.setSelection(i, false);
			selection.post(new Runnable() {

				@Override
				public void run() {
					selection.setOnItemSelectedListener(listener);
				}
			});
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view,
			int position, long id) {
		Mapping mapping = selectionAdapter.getItem(position);
		if (mapping != null) {
			Log.d(TAG, "Sending command " + mapping.getCommand()
					+ " for mapping " + mapping.getLabel());
			sendCommand(mapping.getCommand());
		} else if (mapping == null) {
			Log.w(TAG, "Can't find mapping for position " + position);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {
		// TODO Auto-generated method stub

	}

}
