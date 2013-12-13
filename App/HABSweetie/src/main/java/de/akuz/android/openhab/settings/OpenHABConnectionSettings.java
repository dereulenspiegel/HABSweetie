package de.akuz.android.openhab.settings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Base64;

import de.akuz.android.openhab.util.Strings;

public class OpenHABConnectionSettings implements Parcelable {

	public final static transient Creator<OpenHABConnectionSettings> CREATOR = new Creator<OpenHABConnectionSettings>() {

		@Override
		public OpenHABConnectionSettings createFromParcel(Parcel source) {
			return new OpenHABConnectionSettings(source);
		}

		@Override
		public OpenHABConnectionSettings[] newArray(int size) {
			return new OpenHABConnectionSettings[size];
		}
	};

	private Long _id;

	private long parentId;
	private String baseUrl;
	private String username;
	private String password;
	private boolean useWebSockets;
	private int sendDelay;
	private boolean useCertificatePinning;

	public OpenHABConnectionSettings() {

	}

	public OpenHABConnectionSettings(Parcel source) {
		createFromParcel(source);
	}

	private void createFromParcel(Parcel source) {
		_id = source.readLong();
		baseUrl = source.readString();
		password = source.readString();
		username = source.readString();
		useWebSockets = source.readInt() == 1;
		parentId = source.readLong();
		sendDelay = source.readInt();
		useCertificatePinning = source.readInt() == 1;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getId() {
		return _id;
	}

	public void setId(Long id) {
		this._id = id;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id != null ? _id : -1);
		dest.writeString(baseUrl);
		dest.writeString(password);
		dest.writeString(username);
		dest.writeInt(useWebSockets ? 1 : 0);
		dest.writeLong(parentId);
		dest.writeInt(sendDelay);
		dest.writeInt(useCertificatePinning ? 1 : 0);
	}

	public boolean isUseWebSockets() {
		return useWebSockets;
	}

	public void setUseWebSockets(boolean useWebSockets) {
		this.useWebSockets = useWebSockets;
	}

	public boolean hasCredentials() {
		return !Strings.isEmpty(username) && !Strings.isEmpty(password);
	}

	public String getAuthorizationHeaderValue() {
		String encoded = Base64.encodeBase64String((username + ":" + password)
				.getBytes());
		return "Basic " + encoded;
	}

	public int getSendDelay() {
		return sendDelay;
	}

	public void setSendDelay(int sendDelay) {
		this.sendDelay = sendDelay;
	}

	public boolean isUseCertificatePinning() {
		return useCertificatePinning;
	}

	public void setUseCertificatePinning(boolean useCertificatePinning) {
		this.useCertificatePinning = useCertificatePinning;
	}

}
