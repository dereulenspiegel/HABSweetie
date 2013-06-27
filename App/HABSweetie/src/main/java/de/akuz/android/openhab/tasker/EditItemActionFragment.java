package de.akuz.android.openhab.tasker;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.util.internal.ReusableIterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.ui.BaseFragment;

public class EditItemActionFragment extends BaseFragment implements
		OnClickListener, OnItemSelectedListener {

	private static class ItemStateSpinnerAdapter extends ArrayAdapter<String> {

		public ItemStateSpinnerAdapter(Context context, List<String> objects) {
			super(context, android.R.layout.simple_spinner_item, objects);
		}

	}

	private Item item;

	private TextView itemName;
	private TextView itemType;

	private EditText editTextState;

	private Spinner itemStateSpinner;

	private Button saveButton;

	private OpenHABInstance instance;

	public static EditItemActionFragment build(Item item) {
		EditItemActionFragment fragment = new EditItemActionFragment();
		fragment.setItem(item);
		return fragment;
	}

	@Override
	protected void buildUi() {
		setView(R.layout.edit_tasker_item_action);
		itemStateSpinner = findView(R.id.itemStateSpinner);
		itemName = findView(R.id.itemName);
		itemType = findView(R.id.itemType);
		editTextState = findView(R.id.editTextState);
		editTextState.setEnabled(false);

		saveButton = findView(R.id.saveButton);
		saveButton.setOnClickListener(this);

		itemStateSpinner.setAdapter(new ItemStateSpinnerAdapter(getActivity(),
				getItemStates()));
		itemStateSpinner.setOnItemSelectedListener(this);
		itemType.setText(item.type);
		itemName.setText(item.name);
	}

	private List<String> getItemStates() {
		List<String> stateList = new ArrayList<String>();

		if ("ContactItem".equals(item.type)) {
			stateList.add("OPEN");
			stateList.add("CLOSE");
		} else if ("NumberItem".equals(item.type)) {
			stateList.add(getString(R.string.state_number));
		} else if ("RollershutterItem".equals(item.type)) {
			stateList.add("UP");
			stateList.add("DOWN");
			stateList.add("STOP");
			stateList.add("MOVE");
			stateList.add(getString(R.string.state_percent));
		} else if ("SwitchItem".equals(item.type)) {
			stateList.add("ON");
			stateList.add("OFF");
		} else if ("DimmerItem".equals(item.type)) {
			stateList.add("ON");
			stateList.add("OFF");
			stateList.add("INCREASE");
			stateList.add("DECREASE");
			stateList.add(getString(R.string.state_percent));
		} else if ("ColorItem".equals(item.type)) {
			// TODO create view to edit color settings
		}
		stateList.add(getString(R.string.state_text));
		return stateList;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.saveButton:
			saveResult();
			break;
		}

	}

	private void saveResult() {
		String state = null;
		if (editTextState.isEnabled()) {
			state = editTextState.getText().toString();
		} else {
			state = (String) itemStateSpinner.getSelectedItem();
		}
		Bundle extras = new Bundle();
		extras.putLong(TaskerActionService.EXTRA_INSTANCE_ID,
				((EditTaskerActionActivity) getActivity()).getInstance()
						.getId());
		extras.putString(TaskerActionService.EXTRA_ITEM_ID, item.name);
		extras.putString(TaskerActionService.EXTRA_ITEM_COMMAND, state);

		String blurbFormat = getString(R.string.blurb_format);
		String blurb = MessageFormat.format(blurbFormat, state, item.name);

		Intent resultIntent = new Intent();
		resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE,
				extras);
		resultIntent.putExtra(
				com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);
		getActivity().setResult(Activity.RESULT_OK, resultIntent);
		getActivity().finish();
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view,
			int position, long id) {
		String state = (String) itemStateSpinner.getItemAtPosition(position);
		if (state.equals(getString(R.string.state_number))
				|| state.equals(getString(R.string.state_percent))) {
			editTextState.setEnabled(true);
			editTextState.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		} else if (state.equals(getString(R.string.state_text))) {
			editTextState.setEnabled(true);
			editTextState.setInputType(InputType.TYPE_CLASS_TEXT);
		} else {
			editTextState.setEnabled(false);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
