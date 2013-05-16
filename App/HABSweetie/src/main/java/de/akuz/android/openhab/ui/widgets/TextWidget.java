package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;

public class TextWidget extends BasicOpenHABWidget {

	private ImageView childIndicator;

	public TextWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.text_widget);
		super.buildUi();
		childIndicator = findView(R.id.textWidgetChildIndicator);
	}

	public void widgetUpdated(Widget widget) {
		super.widgetUpdated(widget);
		if (hasChildren()) {
			childIndicator.setVisibility(View.VISIBLE);
		} else {
			childIndicator.setVisibility(View.GONE);
		}
		widgetText.setText(widget.getLabel());
	}

	@Override
	public void updateItem(Item item) {
		// TODO Auto-generated method stub

	}

}
