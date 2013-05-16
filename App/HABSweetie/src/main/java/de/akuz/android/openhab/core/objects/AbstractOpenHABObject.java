package de.akuz.android.openhab.core.objects;

public class AbstractOpenHABObject {
	
	protected String baseUrl;
	
	protected long receivedAt;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public long getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(long receivedAt) {
		this.receivedAt = receivedAt;
	}

}
