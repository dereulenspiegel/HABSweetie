package de.akuz.android.openhab.ui;

import java.util.List;

import javax.inject.Inject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class ChooseSitemapDialogFragment extends DialogFragment implements
		OnClickListener, OnCheckedChangeListener, OnItemClickListener {

	private List<Sitemap> sitemapList;

	private ArrayAdapter<Sitemap> sitemapAdapter;

	private ListView sitemapListView;
	private CheckBox useAsDefaultCheckBox;

	private boolean useAsDefault = false;

	private SelectSitemapListener listener;

	@Inject
	HABSweetiePreferences prefs;

	public static ChooseSitemapDialogFragment build(List<Sitemap> sitemaps) {
		ChooseSitemapDialogFragment fragment = new ChooseSitemapDialogFragment();
		fragment.setSitemaps(sitemaps);
		return fragment;
	}

	public void setSitemaps(List<Sitemap> sitemaps) {
		this.sitemapList = sitemaps;
	}

	public void setListener(SelectSitemapListener listener) {
		this.listener = listener;
	}

	@Override
	public void onAttach(Activity activity) {
		listener = (SelectSitemapListener) activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		sitemapAdapter = new ArrayAdapter<Sitemap>(getActivity(),
				android.R.layout.simple_list_item_1, sitemapList);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View customLayout = getActivity().getLayoutInflater().inflate(
				R.layout.select_sitemap_dialog, null);
		sitemapListView = (ListView) customLayout
				.findViewById(R.id.sitemapListView);
		sitemapListView.setOnItemClickListener(this);
		useAsDefaultCheckBox = (CheckBox) customLayout
				.findViewById(R.id.useAsDefaultCheckBox);
		useAsDefaultCheckBox.setOnCheckedChangeListener(this);
		sitemapListView.setAdapter(sitemapAdapter);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(false);
		builder.setView(customLayout);
		builder.setNegativeButton(R.string.select_sitemap_dialog_cancel, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (listener != null) {
			listener.canceled();
		}
		dismiss();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		useAsDefault = isChecked;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Sitemap sitemap = sitemapAdapter.getItem(position);
		if (useAsDefault) {
			prefs.setDefaultSitemapUrl(sitemap.link);
		}
		if (listener != null) {
			listener.sitemapSelected(sitemap);
		}
		dismiss();

	}

	public static interface SelectSitemapListener {
		public void sitemapSelected(Sitemap selectedSitemap);

		public void canceled();
	}

}
