package de.akuz.android.openhab.core.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemsResult extends AbstractOpenHABObject{

    private List<Item> item = new ArrayList<Item>(0);

    public void setItems(List<Item> items) {
        this.item = items;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(item);
    }

}
