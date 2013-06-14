package de.akuz.android.openhab.core;

import android.app.Application;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.googlehttpclient.json.Jackson2ObjectPersisterFactory;

import dagger.ObjectGraph;
import de.akuz.android.openhab.BootstrapApplication;
import de.akuz.android.openhab.core.http.OkHttpTransport;

public class OpenHABRestService extends GoogleHttpClientSpiceService {

	private final static boolean useOkHttp = false;

	private ObjectGraph objectGraph;

	@Override
	public CacheManager createCacheManager(Application application) {
		objectGraph = ((BootstrapApplication) application).getObjectGraph();
		CacheManager cacheManager = new CacheManager();
		Jackson2ObjectPersisterFactory factory = new Jackson2ObjectPersisterFactory(
				application);

		cacheManager.addPersister(factory);
		return cacheManager;
	}

	@Override
	public HttpRequestFactory createRequestFactory() {
		if (useOkHttp) {
			return createOkHttpRequestFactory();
		} else {
			return createNetHttpRequestFactory();
		}
	}

	private HttpRequestFactory createNetHttpRequestFactory() {
		NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
		NetHttpTransport transport = builder.build();
		return transport.createRequestFactory();
	}

	private HttpRequestFactory createOkHttpRequestFactory() {
		OkHttpTransport.Builder okTransportBuilder = new OkHttpTransport.Builder();
		OkHttpTransport transport = okTransportBuilder.build();
		objectGraph.inject(transport);
		HttpRequestFactory factory = transport.createRequestFactory();
		return factory;
	}
}
