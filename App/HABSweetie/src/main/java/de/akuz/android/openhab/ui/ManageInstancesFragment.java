package de.akuz.android.openhab.ui;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.settings.wizard.ConnectionWizardActivity;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class ManageInstancesFragment extends BaseFragment implements
		OnItemLongClickListener, OnItemClickListener, OnClickListener {

	@Inject
	HABSweetiePreferences prefs;

	private ListView instanceListView;
	private InstanceListAdapter listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.manage_instances_fragment);
		instanceListView = findView(R.id.instancesListView);
		instanceListView.setOnItemLongClickListener(this);
		instanceListView.setOnItemClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		List<OpenHABInstance> instances = prefs.getAllConfiguredInstances();
		listAdapter = new InstanceListAdapter(getActivity(), instances, this);
		inject(listAdapter);
		instanceListView.setAdapter(listAdapter);
	}

	public static class InstanceListAdapter extends
			ArrayAdapter<OpenHABInstance> implements OnCheckedChangeListener {

		@Inject
		HABSweetiePreferences prefs;
		
		private OnClickListener clickListener;

		public InstanceListAdapter(Context context,
				List<OpenHABInstance> objects, OnClickListener listener) {
			super(context, -1, -1, objects);
			this.clickListener = listener;
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
				layout.setOnClickListener(clickListener);
			}
			layout.setTag(instance);
			String name = instance.getName();
			CheckBox defaultCheckBox = (CheckBox) layout
					.findViewById(R.id.checkBoxDefault);
			defaultCheckBox.setTag(instance);
			defaultCheckBox.setOnCheckedChangeListener(this);
			TextView nameTextView = (TextView) layout
					.findViewById(R.id.instanceNameTextView);
			if (instance.getId() == prefs.getDefaultOpenHABInstanceId()) {
				// TODO Replace with translatable text resource
				name = name + " (default)";
				defaultCheckBox.setOnCheckedChangeListener(null);
				defaultCheckBox.setChecked(true);
				defaultCheckBox.setOnCheckedChangeListener(this);
			} else {
				defaultCheckBox.setOnCheckedChangeListener(null);
				defaultCheckBox.setChecked(false);
				defaultCheckBox.setOnCheckedChangeListener(this);
			}
			nameTextView.setText(name);
			return layout;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				prefs.setDefaultOpenHABInstanceId(((OpenHABInstance) buttonView
						.getTag()).getId());
			}
			notifyDataSetChanged();

		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.manage_instances_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_instance:
			startWizard();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startWizard() {
		Intent i = new Intent(getActivity(), ConnectionWizardActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View itemView,
			int position, long id) {
		OpenHABInstance instance = listAdapter.getItem(position);
		removeInstance(instance);
		return true;
	}

	private void removeInstance(OpenHABInstance instance) {
		// TODO ask the user if he really wants to remove the instance
		if (instance.getId() == prefs.getDefaultOpenHABInstanceId()) {
			prefs.setDefaultOpenHABInstanceId(-1);
		}
		prefs.removeOpenHABInstance(instance);
		listAdapter.remove(instance);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		OpenHABInstance instance = listAdapter.getItem(position);
		((ManageInstancesActivity) getActivity()).showInstanceDetails(instance);
	}

	@Override
	public void onClick(View v) {
		OpenHABInstance instance = (OpenHABInstance) v.getTag();
		((ManageInstancesActivity) getActivity()).showInstanceDetails(instance);
	}

}
