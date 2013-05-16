package de.akuz.android.openhab.core;

import android.app.Application;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.googlehttpclient.json.Jackson2ObjectPersisterFactory;

public class OpenHABRestService extends GoogleHttpClientSpiceService {

	@Override
	public CacheManager createCacheManager(Application application) {
		CacheManager cacheManager = new CacheManager();
		Jackson2ObjectPersisterFactory factory = new Jackson2ObjectPersisterFactory(application);

		cacheManager.addPersister(factory);
		return cacheManager;
	}

	@Override
	public HttpRequestFactory createRequestFactory() {
		NetHttpTransport transport = new NetHttpTransport();
		HttpRequestFactory factory = transport.createRequestFactory();
		return factory;
	}
}
