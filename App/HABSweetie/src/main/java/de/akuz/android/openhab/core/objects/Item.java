package de.akuz.android.openhab.core.objects;

import com.google.api.client.util.Key;

public class Item extends AbstractOpenHABObject {
	@Key
	public String type;
	@Key
	public String name;
	@Key
	public String state;
	@Key
	public String link;

}
