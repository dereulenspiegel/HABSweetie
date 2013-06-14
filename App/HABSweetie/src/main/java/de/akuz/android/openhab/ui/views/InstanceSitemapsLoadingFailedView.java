package de.akuz.android.openhab.ui.views;

import de.akuz.android.openhab.R;
import android.content.Context;

public class InstanceSitemapsLoadingFailedView extends BaseListView<Object> {

	public InstanceSitemapsLoadingFailedView(Context context) {
		super(context);
	}

	@Override
	protected void buildUi() {
		setLayout(R.layout.instance_list_loading_failed);

	}

	@Override
	protected void objectUpdated(Object object) {
		// TODO Auto-generated method stub

	}

}
