package de.akuz.android.openhab.core.atmosphere;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.OptionsBuilder;
import org.atmosphere.wasync.Request;
import org.atmosphere.wasync.Request.METHOD;
import org.atmosphere.wasync.Request.TRANSPORT;
import org.atmosphere.wasync.RequestBuilder;
import org.atmosphere.wasync.Socket;
import org.atmosphere.wasync.Socket.STATUS;
import org.atmosphere.wasync.impl.AtmosphereRequest.AtmosphereRequestBuilder;
import org.atmosphere.wasync.impl.DefaultClient;
import org.atmosphere.wasync.impl.DefaultOptions;
import org.atmosphere.wasync.impl.DefaultOptionsBuilder;

import android.net.ConnectivityManager;
import android.util.Log;

import com.octo.android.robospice.SpiceManager;

import de.akuz.android.openhab.core.AbstractPageConnection;
import de.akuz.android.openhab.core.BasicJackson2XmlDecoder;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.core.objects.Widgets;
import de.akuz.android.openhab.util.HABSweetiePreferences;

public class PageAtmosphereXMLConnection extends AbstractPageConnection {

	private final static String TAG = PageAtmosphereXMLConnection.class
			.getSimpleName();

	private Socket socket;

	private UUID atmosphereId;

	private boolean shouldBeClosed = false;

	public PageAtmosphereXMLConnection(SpiceManager spiceManager,
			HABSweetiePreferences prefs, ConnectivityManager conManager) {
		super(prefs, conManager);
		atmosphereId = UUID.randomUUID();
	}

	private void openWebSocket(Request request) {
		try {
			Log.d(TAG, "Opening Atmoshpere connection");
			socket.open(request);
		} catch (IOException e) {
			if (canWeRetry()) {
				openWebSocket(request);
			} else {
				notifyListenersOfException(e);
			}
		}
	}

	private Request setupWebSocket() {
		DefaultClient client = ClientFactory.getDefault().newClient(
				DefaultClient.class);
		OptionsBuilder<DefaultOptions, DefaultOptionsBuilder> optionsBuilder = client
				.newOptionsBuilder();

		optionsBuilder.reconnect(true);
		// optionsBuilder.requestTimeoutInSeconds(10);
		RequestBuilder builder = client.newRequestBuilder();
		if (settings.hasCredentials()) {
			builder.header("Authorization",
					settings.getAuthorizationHeaderValue());
		}
		String atmosphereTransports = "long-polling|streaming";
		if (settings.isUseWebSockets()) {
			builder.transport(TRANSPORT.WEBSOCKET);
			atmosphereTransports = "websocket|long-polling|streaming";
		}
		if (builder instanceof AtmosphereRequestBuilder) {
			((AtmosphereRequestBuilder) builder).trackMessageLength(true);
		}

		Request pageRequest = builder.method(METHOD.GET)
		//
				.uri(pageUrl)
				//
				.header("Accept", "application/xml")
				//
				.header("X-Atmosphere-tracking-id", atmosphereId.toString())
				//
				.header("Accept-Charset", "utf-8")
				//
				.header("X-Atmosphere-Transport", atmosphereTransports)
				//
				.decoder(
						new BasicJackson2XmlDecoder<Page>(
								settings.getBaseUrl(), Page.class))
				//
				.decoder(
						new BasicJackson2XmlDecoder<Widget>(settings
								.getBaseUrl(), Widget.class)) //
				.decoder(
						new BasicJackson2XmlDecoder<Widgets>(settings
								.getBaseUrl(), Widgets.class)) //
				.transport(TRANSPORT.STREAMING) //
				.transport(TRANSPORT.LONG_POLLING) //
				.build(); //

		socket = client.create(optionsBuilder.build());
		socket.on(new WidgetsFunction());
		socket.on(new WidgetFunction());
		socket.on(new PageFunction());
		socket.on(new ExceptionFunction());
		socket.on(Event.ERROR.name(), new ExceptionFunction());
		socket.on(Event.OPEN.name(), new Function<Object>() {

			@Override
			public void on(Object t) {
				notifyListenersConnected();
				String type = "NULL";
				if (t != null) {
					type = t.toString();
				}
				Log.i(TAG, "WSS connection has been openend: " + type);
			}
		});
		socket.on(Event.HEADERS.name(), new Function<String>() {

			@Override
			public void on(String t) {
				Log.d(TAG, "Received headers: " + t);
			}

		});
		socket.on(Event.CLOSE.name(), new Function<Object>() {

			@Override
			public void on(Object t) {
				notifyListenersDisconnected();
				Log.d(TAG, "WebSocket closed");
				if (!shouldBeClosed) {
					Log.d(TAG,
							"Connection closed, even if it should stayed open");
					// openWebSocketConnection();
				}
			}
		});
		socket.on(Event.TRANSPORT.name(), new Function<String>() {

			@Override
			public void on(String t) {
				Log.d(TAG, "Using transport: " + t);

			}
		});
		socket.on(Event.STATUS.name(), new Function<String>() {

			@Override
			public void on(String t) {
				Log.d(TAG, "Status changed: " + t);

			}
		});
		return pageRequest;
	}

	@Override
	public void close() {
		shouldBeClosed = true;
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				Log.e(TAG, "An exception occured while closing the connection",
						e);
			}
		}

	}

	private class PageFunction implements Function<Page> {

		@Override
		public void on(Page page) {
			Log.d(TAG, "Received page update");
			notifyListenersOfPageUpdate(page);
		}
	}

	private class ExceptionFunction implements Function<Throwable> {

		@Override
		public void on(Throwable t) {
			if (canWeRetry()) {
				openWebSocketConnection();
			} else {
				Log.e(TAG, "Got error from WSS", t);
				notifyListenersOfException(t);
			}
		}

	}

	private class WidgetFunction implements Function<Widget> {

		@Override
		public void on(final Widget widget) {
			Log.d(TAG, "Received widget update");
			notifyListenersOfWidgetUpdate(widget);
		}
	}

	private class WidgetsFunction implements Function<Widgets> {

		@Override
		public void on(Widgets t) {
			Log.d(TAG, "Received widgets(!) update");
			if (t.getWidgets() != null) {
				for (Widget w : t.getWidgets()) {
					notifyListenersOfWidgetUpdate(w);
				}
			}
		}

	}

	@Override
	public boolean isServerPushEnabled() {
		return socket != null
				&& (socket.status() == STATUS.OPEN || socket.status() == STATUS.REOPENED);
	}

	@Override
	protected void openWebSocketConnection() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Request pageRequest = setupWebSocket();
				openWebSocket(pageRequest);

			}
		}, "WebSocketConnectionThread").start();

	}

}
