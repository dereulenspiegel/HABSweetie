package de.akuz.android.openhab.ui.widgets;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.util.Log;
import dagger.ObjectGraph;
import de.akuz.android.openhab.core.objects.Widget;

public class OpenHABWidgetFactory {

	private final static String TAG = OpenHABWidgetFactory.class
			.getSimpleName();

	private final static String PACKAGE_BASE = "de.akuz.android.openhab.ui.widgets";
	private final static String WIDGET_CLASS_SUFFIX = "Widget";

	private Map<String, Class<? extends AbstractOpenHABWidget>> widgetMap = new LinkedHashMap<String, Class<? extends AbstractOpenHABWidget>>();

	private final static Class<?>[] constructorParams = { Context.class,
			Widget.class };

	private ObjectGraph objectGraph;

	@Inject
	public OpenHABWidgetFactory(ObjectGraph objectGraph) {
		this.objectGraph = objectGraph;
	}

	// public static OpenHABWidgetFactory getInstance() {
	// return instance;
	// }

	public AbstractOpenHABWidget getFromWidget(Context ctx, Widget widget,
			boolean returnDefault) {
		if (widget == null) {
			throw new IllegalArgumentException(
					"The received Widget must not be NULL");
		}
		Class<? extends AbstractOpenHABWidget> widgetClass = widgetMap
				.get(widget.getType());
		if (widgetClass == null) {
			widgetClass = tryToGetWidgetClass(widget.getType());
			widgetMap.put(widget.getType(), widgetClass);
		}
		if (widgetClass != null) {
			try {
				Constructor<? extends AbstractOpenHABWidget> widgetConstrucor = widgetClass
						.getConstructor(constructorParams);

				AbstractOpenHABWidget openHABWidget = widgetConstrucor
						.newInstance(ctx, widget);
				openHABWidget = objectGraph.inject(openHABWidget);
				openHABWidget.updateWidget(widget);
				return openHABWidget;
			} catch (Exception e) {
				Log.e(TAG,
						"Exception while building widget of type "
								+ widget.getType(), e);
			}
		}
		if (returnDefault) {
			TextWidget textWidget = new TextWidget(ctx, widget);
			textWidget = objectGraph.inject(textWidget);
			return textWidget;
		}
		return null;

	}

	public Class<? extends AbstractOpenHABWidget> tryToGetWidgetClass(
			String widgetName) {
		StringBuffer buf = new StringBuffer();
		buf.append(PACKAGE_BASE);
		buf.append('.');
		buf.append(widgetName);
		buf.append(WIDGET_CLASS_SUFFIX);

		try {
			@SuppressWarnings("unchecked")
			Class<? extends AbstractOpenHABWidget> widgetClass = (Class<? extends AbstractOpenHABWidget>) Class
					.forName(buf.toString());
			return widgetClass;
		} catch (ClassNotFoundException e) {
//			Log.w(TAG, "Can't locate widget class for widget type "
//					+ widgetName);
		}

		return null;
	}

	public void registerWidgetType(String typeName,
			Class<? extends AbstractOpenHABWidget> widgetClass) {
		widgetMap.put(typeName, widgetClass);
	}

	public int getViewTypeCount() {
		return widgetMap.size() + 1;
	}

}
