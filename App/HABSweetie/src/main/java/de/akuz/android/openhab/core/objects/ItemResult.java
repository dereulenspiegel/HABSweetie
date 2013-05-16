package de.akuz.android.openhab.core.objects;

import com.google.api.client.util.Key;

public class ItemResult extends AbstractOpenHABObject {

	@Key
    public Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
