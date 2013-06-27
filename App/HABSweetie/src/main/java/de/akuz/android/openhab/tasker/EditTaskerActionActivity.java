package de.akuz.android.openhab.tasker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.tasker.SelectInstanceDialog.SelectInstanceListener;
import de.akuz.android.openhab.tasker.SelectItemFragment.ItemSelectedListener;
import de.akuz.android.openhab.ui.BaseActivity;

public class EditTaskerActionActivity extends BaseActivity implements
		SelectInstanceListener, ItemSelectedListener {

	private final static String TAG = EditTaskerActionActivity.class
			.getSimpleName();

	private SelectItemFragment selectItemFragment;
	private EditItemActionFragment editItemActionFragment;

	private OpenHABInstance instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(Activity.RESULT_CANCELED);
		setContentView(R.layout.edit_tasker_action_activity);
		selectItemFragment = new SelectItemFragment();
		replaceFragment(selectItemFragment, false);
	}

	@Override
	public void instanceSelected(OpenHABInstance instance) {
		Log.d(TAG, "Instance selected");
		this.instance = instance;
		selectItemFragment.instanceSelected(instance);

	}

	public OpenHABInstance getInstance() {
		return this.instance;
	}

	@Override
	public void canceled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemSelected(Item item) {
		editItemActionFragment = EditItemActionFragment.build(item);
		replaceFragment(editItemActionFragment);

	}

	private void replaceFragment(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fragmentContainer, fragment);
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}

	private void replaceFragment(Fragment fragment) {
		replaceFragment(fragment, true);
	}

}
