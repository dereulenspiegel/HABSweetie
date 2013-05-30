package de.akuz.android.openhab.core.objects;

import java.util.ArrayList;

public class Widgets extends AbstractOpenHABObject {

	private ArrayList<Widget> widgets;

	public ArrayList<Widget> getWidgets() {
		return widgets;
	}

	public void setWidgets(ArrayList<Widget> widgets) {
		this.widgets = widgets;
	}

}
