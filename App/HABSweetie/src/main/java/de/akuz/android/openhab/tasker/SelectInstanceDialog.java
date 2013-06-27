package de.akuz.android.openhab.tasker;

import java.util.List;

import javax.inject.Inject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.ui.BaseActivity;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class SelectInstanceDialog extends SherlockDialogFragment implements
		OnClickListener {

	private InstanceListAdapter listAdapter;

	@Inject
	HABSweetiePreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inject(this);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		listAdapter = new InstanceListAdapter(getActivity(),
				prefs.getAllConfiguredInstances());
		inject(listAdapter);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setAdapter(listAdapter, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		OpenHABInstance instance = listAdapter.getItem(which);
		((SelectInstanceListener) getActivity()).instanceSelected(instance);

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		((SelectInstanceListener) getActivity()).canceled();
	}

	private void inject(Object o) {
		((BaseActivity) getActivity()).inject(o);
	}

	public static class InstanceListAdapter extends
			ArrayAdapter<OpenHABInstance> {

		@Inject
		HABSweetiePreferences prefs;

		public InstanceListAdapter(Context context,
				List<OpenHABInstance> objects) {
			super(context, -1, -1, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OpenHABInstance instance = getItem(position);
			View layout = null;
			if (convertView != null) {
				layout = convertView;
			} else {
				layout = LayoutInflater.from(getContext()).inflate(
						R.layout.instance_list_view_item, parent, false);
			}
			layout.setTag(instance);
			String name = instance.getName();
			CheckBox defaultCheckBox = (CheckBox) layout
					.findViewById(R.id.checkBoxDefault);
			defaultCheckBox.setVisibility(View.GONE);
			TextView nameTextView = (TextView) layout
					.findViewById(R.id.instanceNameTextView);
			if (instance.getId() == prefs.getDefaultOpenHABInstanceId()) {
				// TODO Replace with translatable text resource
				name = name + " (default)";
			} else {
			}
			nameTextView.setText(name);
			return layout;
		}

	}

	public static interface SelectInstanceListener {
		public void instanceSelected(OpenHABInstance instance);

		public void canceled();
	}

}
