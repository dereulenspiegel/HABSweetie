package de.akuz.android.openhab.core.autobahn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.octo.android.robospice.SpiceManager;

import de.akuz.android.openhab.core.AbstractPageConnection;
import de.tavendo.autobahn.WebSocket.ConnectionHandler;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

public class PageAutobahnXMLConnection extends AbstractPageConnection implements
		ConnectionHandler {

	private final static String TAG = PageAutobahnXMLConnection.class
			.getSimpleName();

	private WebSocketConnection con;

	private UUID atmosphereId;

	public PageAutobahnXMLConnection(SpiceManager spiceManager) {
		super(spiceManager);
		atmosphereId = UUID.randomUUID();
	}

	@Override
	public void close() {
		con.disconnect();
	}

	@Override
	public boolean isServerPushEnabled() {
		return con != null && con.isConnected();
	}

	@Override
	protected void openWebSocketConnection() {
		con = new WebSocketConnection();
		WebSocketOptions options = new WebSocketOptions();
		List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
		if (settings.hasCredentials()) {
			headers.add(new BasicNameValuePair("Authorization", settings
					.getAuthorizationHeaderValue()));
		}
		headers.add(new BasicNameValuePair("Accept", "application/xml"));
		headers.add(new BasicNameValuePair("X-Atmosphere-tracking-id",
				atmosphereId.toString()));
		headers.add(new BasicNameValuePair("Accept-Charset", "utf-8"));
		try {
			String wssUrl = pageUrl;
			if (!wssUrl.startsWith("ws")) {
				wssUrl = "ws://" + pageUrl.substring(pageUrl.indexOf('/') + 2);
			}
			Log.d(TAG, "Connecting to " + wssUrl);
			con.connect(wssUrl, null, this, options, headers);
		} catch (WebSocketException e) {
			notifyListenersOfException(e);
		}

	}

	@Override
	public void onOpen() {
		Log.d(TAG, "Connection is open");

	}

	@Override
	public void onClose(int code, String reason) {
		Log.d(TAG, "Connection closed with code " + code + " and reason "
				+ reason);

	}

	@Override
	public void onTextMessage(String payload) {
		Log.d(TAG, "Received text message: " + payload);

	}

	@Override
	public void onRawTextMessage(byte[] payload) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBinaryMessage(byte[] payload) {
		// TODO Auto-generated method stub

	}

}
