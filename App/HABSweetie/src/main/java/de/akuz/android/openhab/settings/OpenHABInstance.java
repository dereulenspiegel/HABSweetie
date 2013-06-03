package de.akuz.android.openhab.settings;

public class OpenHABInstance {

	private Long _id;

	private OpenHABConnectionSettings external;
	private OpenHABConnectionSettings internal;
	private String name;

	public OpenHABConnectionSettings getExternal() {
		return external;
	}

	public void setExternal(OpenHABConnectionSettings external) {
		this.external = external;
	}

	public OpenHABConnectionSettings getInternal() {
		return internal;
	}

	public void setInternal(OpenHABConnectionSettings internal) {
		this.internal = internal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		this._id = id;
	}

}
