package de.akuz.android.openhab.core.objects;

import com.google.api.client.util.Key;

public class Mapping {

	@Key("command")
	protected String command;
	@Key("label")
	protected String label;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

}
