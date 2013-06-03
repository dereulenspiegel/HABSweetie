package de.akuz.android.openhab.core;

import javax.inject.Inject;

import android.app.Application;

import com.google.api.client.http.HttpRequestFactory;
import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.googlehttpclient.json.Jackson2ObjectPersisterFactory;

import dagger.ObjectGraph;
import de.akuz.android.openhab.BootstrapApplication;
import de.akuz.android.openhab.core.http.OkHttpTransport;

public class OpenHABRestService extends GoogleHttpClientSpiceService {

	@Inject
	ObjectGraph objectGraph;

	@Override
	public CacheManager createCacheManager(Application application) {
		((BootstrapApplication) application).getObjectGraph().inject(this);
		CacheManager cacheManager = new CacheManager();
		Jackson2ObjectPersisterFactory factory = new Jackson2ObjectPersisterFactory(
				application);

		cacheManager.addPersister(factory);
		return cacheManager;
	}

	@Override
	public HttpRequestFactory createRequestFactory() {
		OkHttpTransport.Builder okTransportBuilder = new OkHttpTransport.Builder();
		OkHttpTransport transport = okTransportBuilder.build();
		objectGraph.inject(transport);
		HttpRequestFactory factory = transport.createRequestFactory();
		return factory;
	}
}
