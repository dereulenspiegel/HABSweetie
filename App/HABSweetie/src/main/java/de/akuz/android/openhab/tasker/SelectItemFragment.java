package de.akuz.android.openhab.tasker;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.ItemsResult;
import de.akuz.android.openhab.core.requests.ItemsRequest;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.tasker.SelectInstanceDialog.SelectInstanceListener;
import de.akuz.android.openhab.ui.BaseFragment;
import de.akuz.android.openhab.ui.ProgressDialogFragment;
import de.akuz.android.openhab.util.HABSweetiePreferences;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SelectItemFragment extends BaseFragment implements
		SelectInstanceListener, OnItemClickListener {

	private final static String TAG = SelectItemFragment.class.getSimpleName();

	public static interface ItemSelectedListener {
		public void itemSelected(Item item);
	}

	@Inject
	SpiceManager spiceManager;

	@Inject
	HABSweetiePreferences prefs;

	@Inject
	ConnectivityManager conManager;

	private ListView selectItemListView;

	private ItemListAdapter listAdapter;

	private OpenHABInstance selectedInstance;

	private ProgressDialogFragment progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO implement UI
		selectInstance();
	}

	@Override
	protected void buildUi() {
		setView(R.layout.select_item_fragment);
		selectItemListView = findView(R.id.itemListView);
		selectItemListView.setOnItemClickListener(this);
	}

	private void selectInstance() {
		SelectInstanceDialog dialog = new SelectInstanceDialog();
		dialog.show(getFragmentManager(), "selectInstance");
	}

	private void loadAvailableItems() {
		Log.d(TAG,"Loading available items");
		if (progressDialog != null) {
			progressDialog.dismissAllowingStateLoss();
		}
		progressDialog = ProgressDialogFragment
				.build(getString(R.string.progress_load_items));
		progressDialog.show(getFragmentManager(), null);
		OpenHABConnectionSettings setting = selectedInstance
				.getSettingForCurrentNetwork(conManager);
		ItemsRequest request = new ItemsRequest(setting);

		spiceManager.execute(request, new RequestListener<ItemsResult>() {

			@Override
			public void onRequestFailure(SpiceException spiceException) {
				if (progressDialog != null) {
					progressDialog.dismissAllowingStateLoss();
				}
				makeCrouton(R.string.error_connect, Style.ALERT, spiceException
						.getCause().getMessage());
			}

			@Override
			public void onRequestSuccess(ItemsResult result) {
				if (progressDialog != null) {
					progressDialog.dismissAllowingStateLoss();
				}
				listAdapter = new ItemListAdapter(getActivity(), result
						.getItems());
				selectItemListView.setAdapter(listAdapter);

			}
		});
	}

	@Override
	public void instanceSelected(OpenHABInstance instance) {
		selectedInstance = instance;
		loadAvailableItems();

	}

	@Override
	public void canceled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Item item = listAdapter.getItem(position);
		((ItemSelectedListener) getActivity()).itemSelected(item);
	}

	private static class ItemListAdapter extends ArrayAdapter<Item> {

		public ItemListAdapter(Context context, List<Item> items) {
			super(context, -1, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Item item = getItem(position);
			ItemListItemView view = null;
			if (convertView != null) {
				view = (ItemListItemView) convertView;
			} else {
				view = new ItemListItemView(getContext());
			}
			view.updateObject(item);
			return view;
		}
	}

}
