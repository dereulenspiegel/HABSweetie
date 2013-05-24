package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;

public class FrameWidget extends AbstractOpenHABWidget {

	private LinearLayout widgetListContainer;
	private TextView widgetName;
	private ImageView widgetImage;
	private LinearLayout fragmentHeaderContainer;

	public FrameWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		setView(R.layout.frame_widget);
		widgetListContainer = findView(R.id.widgetList);
		widgetName = findView(R.id.widgetName);
		widgetImage = findView(R.id.widgetLogo);
		widgetListContainer.setScrollContainer(false);
		fragmentHeaderContainer = findView(R.id.frameWidgetHeaderContainer);
//		updateWidget(widget);
	}

	@Override
	protected void widgetUpdated(Widget widget) {
		if ((widget.getLabel() == null || widget.getLabel().length() == 0)
				&& widget.getImageUrl() == null) {
			fragmentHeaderContainer.setVisibility(View.GONE);
		} else {
			ImageLoader.getInstance().displayImage(widget.getImageUrl(),
					widgetImage);
			fragmentHeaderContainer.setVisibility(View.VISIBLE);
			if (widget.getLabel() != null) {
				widgetName.setText(widget.getLabel());
				widgetName.setVisibility(View.VISIBLE);
			} else {
				widgetName.setVisibility(View.GONE);
			}
		}
		widgetListContainer.removeAllViews();
		if (widget.getWidget() != null) {
			for (Widget w : widget.getWidget()) {
				AbstractOpenHABWidget openHABWidget = widgetFactory
						.getFromWidget(getContext(), w, true);
				openHABWidget.setItemCommandInterface(commandInterface);
				openHABWidget.updateWidget(w);
				widgetListContainer.addView(openHABWidget);
			}
		}
	}

	@Override
	public void updateItem(Item item) {
		// TODO Auto-generated method stub

	}
}
