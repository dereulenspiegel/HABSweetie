package de.akuz.android.openhab.ui;

import javax.inject.Inject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.core.requests.SitemapsRequest;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.ui.ChooseSitemapDialogFragment.SelectSitemapListener;
import de.akuz.android.openhab.ui.views.OpenHABInstanceUtil;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class EditInstanceFragment extends BaseFragment implements
		OnClickListener, SelectSitemapListener {

	public final static String INSTANCE_ID_ARG = "instanceId";

	@Inject
	HABSweetiePreferences prefs;

	@Inject
	OpenHABInstanceUtil instanceUtil;

	@Inject
	SpiceManager spiceManager;

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
		final ProgressDialogFragment progressDialog = ProgressDialogFragment
				.build(getString(R.string.message_loading_sitemaps));
		progressDialog.show(getFragmentManager(), "dialog");
		SitemapsRequest request = new SitemapsRequest(
				instanceUtil.chooseSetting(instance));
		spiceManager.execute(request, new RequestListener<SitemapsResult>() {

			@Override
			public void onRequestFailure(SpiceException spiceException) {
				progressDialog.dismissAllowingStateLoss();

			}

			@Override
			public void onRequestSuccess(SitemapsResult result) {
				progressDialog.dismissAllowingStateLoss();
				ChooseSitemapDialogFragment dialog = ChooseSitemapDialogFragment
						.build(result.getSitemap());
				dialog.show(EditInstanceFragment.this.getFragmentManager(),
						"chooseSitemap");

			}
		});
	}

	@Override
	public void sitemapSelected(Sitemap selectedSitemap, boolean useAsDefault) {
		instance.setDefaultSitemapIdFromUrl(selectedSitemap);
		prefs.saveInstance(instance);
		defaultSitemapTextView.setText(instance.getDefaultSitemapId());
	}

	@Override
	public void canceled() {
		// Ignore

	}

}
