package de.akuz.android.openhab.core.objects;


public class ItemResult extends AbstractOpenHABObject {

    public Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
