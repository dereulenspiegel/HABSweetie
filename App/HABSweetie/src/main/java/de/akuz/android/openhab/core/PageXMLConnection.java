package de.akuz.android.openhab.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.atmosphere.wasync.Client;
import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.Request;
import org.atmosphere.wasync.Request.METHOD;
import org.atmosphere.wasync.Request.TRANSPORT;
import org.atmosphere.wasync.RequestBuilder;
import org.atmosphere.wasync.Socket;
import org.atmosphere.wasync.impl.AtmosphereClient;

import android.os.Handler;
import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import dagger.ObjectGraph;
import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.core.objects.Widgets;
import de.akuz.android.openhab.core.requests.ItemCommandRequest;
import de.akuz.android.openhab.core.requests.ItemRequest;
import de.akuz.android.openhab.core.requests.PageRequest;
import de.akuz.android.openhab.ui.widgets.ItemUpdateListener;
import de.akuz.android.openhab.util.HABSweetiePreferences;
import de.akuz.android.openhab.util.UrlUtils;

public class PageXMLConnection implements PageConnectionInterface,
		RequestListener<Page> {

	private final static String TAG = PageXMLConnection.class.getSimpleName();

	private List<PageUpdateListener> listeners = new LinkedList<PageUpdateListener>();

	@Inject
	SpiceManager spiceManager;

	@Inject
	HABSweetiePreferences prefs;

	@Inject
	ObjectGraph objectGraph;

	private String baseUrl;
	private String pageId;
	private String sitemapId;

	private Handler uiHandler;

	private Socket socket;

	private boolean wssConnectionEnabled;

	private UUID atmosphereId;

	@Inject
	public PageXMLConnection() {
		uiHandler = new Handler();
		atmosphereId = UUID.randomUUID();
	}

	@Override
	public void loadCompletePage() {
		spiceManager.execute(new PageRequest(baseUrl, sitemapId, pageId),
				pageId, DurationInMillis.ALWAYS_EXPIRED, this);

	}

	@Override
	public void registerUpdateListener(PageUpdateListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}

	}

	@Override
	public void open(String baseUrl, String sitemapId, String pageId) {
		this.baseUrl = baseUrl;
		this.pageId = pageId;
		this.sitemapId = sitemapId;
		new Thread(new Runnable() {

			@Override
			public void run() {
				Request pageRequest = setupWebSocket();
				openWebSocket(pageRequest);

			}
		}, "WebSocketConnectionThread").start();
	}

	private void openWebSocket(Request request) {
		try {
			socket.open(request);
		} catch (IOException e) {
			wssConnectionEnabled = false;
			notifyListenersOfException(e);
		}
	}

	private Request setupWebSocket() {
		Client client = ClientFactory.getDefault().newClient(
				AtmosphereClient.class);

		RequestBuilder builder = client.newRequestBuilder();
		if (OpenHABAuthManager.hasCredentials()) {
			builder.header("Authorization",
					"Basic " + OpenHABAuthManager.getEncodedCredentials());
		}
		if (prefs.useWebSockets()) {
			builder.transport(TRANSPORT.WEBSOCKET);
		}
		for (Decoder<?, ?> d : getDecoders()) {
			builder.decoder(d);
		}
		String pageUrl = UrlUtils.concat(UrlUtils.concat(baseUrl, sitemapId),
				pageId);
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
				.transport(TRANSPORT.LONG_POLLING) //
				.transport(TRANSPORT.STREAMING) //
				.build(); //

		socket = client.create();
		socket.on(new WidgetsFunction());
		socket.on(new WidgetFunction());
		socket.on(new PageFunction());
		socket.on(new ExceptionFunction());
		socket.on(Function.MESSAGE.error.name(), new ExceptionFunction());
		socket.on(new VoidFunction());
		socket.on(new PlainStringFunction());
		socket.on(Function.MESSAGE.open.name(), new Function<Object>() {

			@Override
			public void on(Object t) {
				wssConnectionEnabled = true;
				String type = "NULL";
				if (t != null) {
					type = t.getClass().getName();
				}
				Log.i(TAG, "WSS connection has been openend: " + type);
			}
		});
		socket.on(Function.MESSAGE.headers.name(), new Function<String>() {

			@Override
			public void on(String t) {
				Log.d(TAG, "Received headers");
			}

		});
		socket.on(Function.MESSAGE.close.name(), new Function<Object>() {

			@Override
			public void on(Object t) {
				Log.d(TAG, "WebSocket closed");
				wssConnectionEnabled = false;
			}
		});
		return pageRequest;
	}

	protected List<Decoder<?, ?>> getDecoders() {
		ArrayList<Decoder<?, ?>> decoderList = new ArrayList<Decoder<?, ?>>(3);
		Decoder<?, ?> pageDecoder = new BasicJackson2XmlDecoder<Page>(baseUrl,
				Page.class);
		objectGraph.inject(pageDecoder);
		Decoder<?, ?> widgetDecoder = new BasicJackson2XmlDecoder<Widget>(
				baseUrl, Widget.class);
		objectGraph.inject(widgetDecoder);
		decoderList.add(widgetDecoder);
		Decoder<?, ?> widgetsDecoder = new BasicJackson2XmlDecoder<Widgets>(
				baseUrl, Widgets.class);
		objectGraph.inject(widgetsDecoder);
		decoderList.add(widgetsDecoder);
		return decoderList;

	}

	@Override
	public void close() {
		socket.close();

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
			Log.e(TAG, "Got error from WSS", t);
			notifyListenersOfException(t);
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

	private class VoidFunction implements Function<Void> {

		@Override
		public void on(Void t) {
			Log.d(TAG, "Received empty message");

		}

	}

	private class PlainStringFunction implements Function<String> {

		@Override
		public void on(String t) {
			Log.d(TAG, "Received String " + t);

		}

	}

	@Override
	public void unregisterUpdateListener(PageUpdateListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}

	}

	@Override
	public void sendCommand(final Item item, String command,
			final ItemUpdateListener listener) {
		spiceManager.execute(new ItemCommandRequest(item, command),
				new RequestListener<AbstractOpenHABObject>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						notifyListenersOfException(spiceException.getCause());

					}

					@Override
					public void onRequestSuccess(AbstractOpenHABObject result) {
						if (!isServerPushEnabled() && listener != null) {
							pollItem(item, listener);
						}

					}
				});

	}

	private void pollItem(Item item, final ItemUpdateListener listener) {
		spiceManager.execute(new ItemRequest(item, baseUrl),
				new RequestListener<Item>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						notifyListenersOfException(spiceException.getCause());

					}

					@Override
					public void onRequestSuccess(Item result) {
						listener.itemUpdateReceived(result);

					}
				});
	}

	@Override
	public void onRequestFailure(SpiceException spiceException) {
		Throwable cause = spiceException.getCause();
		notifyListenersOfException(cause);

	}

	@Override
	public void onRequestSuccess(Page result) {
		notifyListenersOfPageUpdate(result);

	}

	private void notifyListenersOfWidgetUpdate(final Widget widget) {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (final PageUpdateListener l : listeners) {
					l.widgetUpdateReceived(widget);

				}
			}
		});
	}

	private void notifyListenersOfPageUpdate(final Page page) {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (final PageUpdateListener l : listeners) {
					l.pageUpdateReceived(page);

				}
			}
		});
	}

	private void notifyListenersOfException(final Throwable t) {
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				for (final PageUpdateListener l : listeners) {
					l.exceptionOccured(t);

				}
			}
		});
	}

	@Override
	public boolean isServerPushEnabled() {
		return wssConnectionEnabled;
	}

}
