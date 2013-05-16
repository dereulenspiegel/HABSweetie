package de.akuz.android.openhab.ui.widgets;

import de.akuz.android.openhab.core.objects.Item;

public interface ItemUpdateListener {
	public void itemUpdateReceived(Item item);
}
