package de.akuz.android.openhab.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.ui.widgets.AbstractOpenHABWidget;
import de.akuz.android.openhab.ui.widgets.AbstractOpenHABWidget.ItemCommandInterface;
import de.akuz.android.openhab.ui.widgets.OpenHABWidgetFactory;

public class WidgetListAdapter extends BaseAdapter {

	private final static String TAG = WidgetListAdapter.class.getSimpleName();

	private Map<String, Widget> idToWidgetMap = new LinkedHashMap<String, Widget>();

	private List<Widget> widgetList = new ArrayList<Widget>();

	@Inject
	OpenHABWidgetFactory widgetFactory;

	private ItemCommandInterface commandInterface;

	public WidgetListAdapter(ItemCommandInterface commandInterface) {
		this.commandInterface = commandInterface;
	}

	public void batchAddOrUpdateWidgets(Collection<Widget> widgets) {
		if (widgets == null) {
			return;
		}
		for (Widget w : widgets) {
			updateOrAddWidget(w);
		}
		notifyDataSetChanged();
	}

	public void clearAdapter() {
		widgetList.clear();
		idToWidgetMap.clear();
		notifyDataSetChanged();
	}

	public void addWidget(Widget widget) {
		updateOrAddWidget(widget);
		notifyDataSetChanged();
	}

	public void updateWidget(Widget widget) {
		updateOrAddWidget(widget);
		notifyDataSetChanged();
	}

	private void updateOrAddWidget(Widget widget) {
		Widget oldWidget = idToWidgetMap.get(widget.getWidgetId());
		if (oldWidget != null) {
			int indexToReplace = widgetList.indexOf(oldWidget);
			if (indexToReplace > -1) {
				widgetList.remove(indexToReplace);
				widgetList.add(indexToReplace, widget);
			} else {
				widgetList.add(widget);
			}
			idToWidgetMap.put(widget.getWidgetId(), widget);
		} else {
			boolean isSubWidget = false;
			for (Widget w : widgetList) {
				if (w.updateSubWidget(widget)) {
					isSubWidget = true;
					break;
				}
			}
			if (!isSubWidget) {
				Log.w(TAG,
						"Found a widget which doesn't seem to belong to any container: "
								+ widget.getWidgetId());
				widgetList.add(widget);
				idToWidgetMap.put(widget.getWidgetId(), widget);
			}

		}
	}

	@Override
	public int getCount() {
		return idToWidgetMap != null ? idToWidgetMap.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return widgetList.get(position);
	}

	@Override
	public long getItemId(int position) {
		Widget w = (Widget) getItem(position);
		return w.getWidgetId().hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Widget widget = (Widget) getItem(position);
		AbstractOpenHABWidget openHABWidget = null;
		if (convertView != null) {
			openHABWidget = (AbstractOpenHABWidget) convertView;
		} else {

			openHABWidget = widgetFactory.getFromWidget(parent.getContext(),
					widget, true);
			openHABWidget.setItemCommandInterface(commandInterface);
			openHABWidget.setWidgetBackground(R.drawable.bg_card);
			openHABWidget.setMargins(8, 8, 5, 5);
		}
		openHABWidget.updateWidget(widget);
		return openHABWidget;
	}
	
	@Override
	public int getItemViewType(int position) {
		Widget w = (Widget) getItem(position);
		return widgetFactory.getViewTypeId(w.getType());
	}

	@Override
	public int getViewTypeCount() {
		return widgetFactory.getViewTypeCount();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}
