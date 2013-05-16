package de.akuz.android.openhab.core.objects;

import com.google.api.client.util.Key;

public class WidgetsSingleResult extends AbstractOpenHABObject {
	
	@Key
	private Widget widgets;

	public Widget getWidgets() {
		return widgets;
	}

	public void setWidgets(Widget widget) {
		this.widgets = widget;
	}

}
