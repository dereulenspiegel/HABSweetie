package de.akuz.android.openhab.ui.views;

import android.content.Context;
import de.akuz.android.openhab.R;

public class LoadingSitemapsView extends BaseListView<Object> {

	public LoadingSitemapsView(Context context) {
		super(context);
	}

	@Override
	protected void buildUi() {
		setLayout(R.layout.instance_list_load_sitemaps_view);
	}

	@Override
	protected void objectUpdated(Object object) {
		// Ignore

	}

}
