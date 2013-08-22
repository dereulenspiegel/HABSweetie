package de.akuz.android.openhab.core;

import javax.inject.Inject;

import android.app.Application;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.googlehttpclient.json.Jackson2ObjectPersisterFactory;
import com.squareup.okhttp.OkHttpClient;

import dagger.ObjectGraph;
import de.akuz.android.openhab.BootstrapApplication;
import de.akuz.google.api.OkHttpTransport;

public class OpenHABRestService extends GoogleHttpClientSpiceService {

	private final static boolean useOkHttp = true;

	protected ObjectGraph objectGraph;

	@Inject
	OkHttpClient okClient;

	@Override
	public CacheManager createCacheManager(Application application) {
		objectGraph = ((BootstrapApplication) application).getObjectGraph();
		objectGraph.inject(this);
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
		okTransportBuilder.setOkHttpClient(okClient);
		OkHttpTransport transport = okTransportBuilder.build();
		HttpRequestFactory factory = transport.createRequestFactory();
		return factory;
	}
}
