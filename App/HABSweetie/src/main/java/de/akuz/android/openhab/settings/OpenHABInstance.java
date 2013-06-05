package de.akuz.android.openhab.settings;

import android.os.Parcel;
import android.os.Parcelable;
import de.akuz.android.openhab.core.objects.Sitemap;

public class OpenHABInstance implements Parcelable {

	public final static transient Creator<OpenHABInstance> CREATOR = new Creator<OpenHABInstance>() {

		@Override
		public OpenHABInstance createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OpenHABInstance[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	private Long _id;

	private OpenHABConnectionSettings external;
	private OpenHABConnectionSettings internal;
	private String name;
	private String defaultSitemapId;

	public OpenHABInstance(Parcel source) {
		createFromParcel(source);
	}

	public OpenHABInstance() {

	}

	private void createFromParcel(Parcel source) {
		_id = source.readLong();
		name = source.readString();
		internal = source.readParcelable(null);
		external = source.readParcelable(null);
		defaultSitemapId = source.readString();
	}

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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeString(name);
		dest.writeParcelable(internal, flags);
		dest.writeParcelable(external, flags);
		dest.writeString(defaultSitemapId);
	}

	public String getDefaultSitemapId() {
		return defaultSitemapId;
	}

	public void setDefaultSitemapIdFromUrl(Sitemap sitemap) {
		String pageUrl = sitemap.homepage.link;
		String[] parts = pageUrl.split("/");
		this.defaultSitemapId = parts[parts.length - 2] + "/"
				+ parts[parts.length - 1];
	}

}
