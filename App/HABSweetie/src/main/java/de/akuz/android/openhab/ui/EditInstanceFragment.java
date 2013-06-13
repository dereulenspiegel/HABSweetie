package de.akuz.android.openhab.ui;

import javax.inject.Inject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class EditInstanceFragment extends BaseFragment implements
		OnClickListener {

	public final static String INSTANCE_ID_ARG = "instanceId";

	@Inject
	HABSweetiePreferences prefs;

	private long instanceId;

	private OpenHABInstance instance;

	private EditText editName;

	private TextView interalTextViewUrl;
	private Button editInternalUrlButton;

	private TextView externalUrlTextView;
	private Button editExternalUrlButton;

	private TextView defaultSitemapTextView;
	private Button chooseSitemapButton;

	public static EditInstanceFragment build(long id) {
		EditInstanceFragment fragment = new EditInstanceFragment();
		Bundle args = new Bundle();
		args.putLong(INSTANCE_ID_ARG, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onPause() {
		super.onPause();
		prefs.saveInstance(instance);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		instanceId = args.getLong(INSTANCE_ID_ARG);
		instance = prefs.loadInstance(instanceId);
		setHasOptionsMenu(true);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.edit_instance_fragment);
		editName = findView(R.id.editInstanceName);
		interalTextViewUrl = findView(R.id.interalUrlTextView);
		editInternalUrlButton = findView(R.id.editInternalUrlButton);
		externalUrlTextView = findView(R.id.externalUrlTextView);
		editExternalUrlButton = findView(R.id.editExternalUrlButton);
		chooseSitemapButton = findView(R.id.editDefaultSitemapButton);
		defaultSitemapTextView = findView(R.id.defaultSitemapTextView);

		editExternalUrlButton.setOnClickListener(this);
		editInternalUrlButton.setOnClickListener(this);
		chooseSitemapButton.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		instance = prefs.loadInstance(instanceId);
		editName.setText(instance.getName());
		interalTextViewUrl.setText(instance.getInternal().getBaseUrl());
		externalUrlTextView.setText(instance.getExternal().getBaseUrl());
		defaultSitemapTextView.setText(instance.getDefaultSitemapId());

	}

	@Override
	public void onDetach() {
		prefs.saveInstance(instance);
		super.onDetach();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editInternalUrlButton:
			((ManageInstancesActivity) getActivity()).showConnectionSettings(
					true, instance.getInternal());
			break;
		case R.id.editExternalUrlButton:
			((ManageInstancesActivity) getActivity()).showConnectionSettings(
					false, instance.getInternal());
			break;

		case R.id.editDefaultSitemapButton:
			selectDefaultSitemap();
			break;
		}

	}

	private void selectDefaultSitemap() {

	}

}
