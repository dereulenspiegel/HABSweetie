package de.akuz.android.openhab.tasker;

import android.content.Context;
import android.widget.TextView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.ui.views.BaseListView;

public class ItemListItemView extends BaseListView<Item> {

	private TextView itemName;
	private TextView itemType;
	private TextView itemState;

	public ItemListItemView(Context context) {
		super(context);
	}

	@Override
	protected void buildUi() {
		setLayout(R.layout.item_list_item);
		itemName = findView(R.id.itemNameTextView);
		itemType = findView(R.id.typeTextView);
		itemState = findView(R.id.stateTextView);
	}

	@Override
	protected void objectUpdated(Item item) {
		itemName.setText(item.name);
		itemType.setText(item.type);
		itemState.setText(item.state);

	}

}
