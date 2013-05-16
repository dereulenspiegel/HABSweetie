package de.akuz.android.openhab.core.objects;

import java.util.ArrayList;

import org.codehaus.jackson.map.annotate.JsonRootName;

import com.google.api.client.util.Key;

@JsonRootName("widgets")
public class Widgets extends AbstractOpenHABObject {

	@Key
	private ArrayList<Widget> widgets;

	public ArrayList<Widget> getWidgets() {
		return widgets;
	}

	public void setWidgets(ArrayList<Widget> widgets) {
		this.widgets = widgets;
	}

}
