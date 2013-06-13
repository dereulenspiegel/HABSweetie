package de.akuz.android.openhab.ui.views;

import android.content.Context;
import android.widget.TextView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.settings.OpenHABInstance;

public class InstanceListTopView extends BaseListView<OpenHABInstance> {

	private TextView instanceName;

	public InstanceListTopView(Context context) {
		super(context);
	}

	@Override
	protected void buildUi() {
		setLayout(R.layout.instance_list_top_item);
		instanceName = findView(R.id.instanceName);
	}

	@Override
	protected void objectUpdated(OpenHABInstance object) {
		instanceName.setText(object.getName());
	}

}
