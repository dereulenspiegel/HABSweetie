package de.akuz.android.openhab.core;

import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import android.app.Application;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.retrofit.RetrofitObjectPersisterFactory;
import com.octo.android.robospice.retrofit.RetrofitSpiceService;

import de.akuz.android.openhab.AndroidModule;

public class OpenHABRestService extends RetrofitSpiceService {

	private JacksonConverter converter;

	@Override
	public CacheManager createCacheManager(Application application) {
		CacheManager cacheManager = new CacheManager();
		converter = new JacksonConverter();
		// FIXME Bad hack because I don't know how to inject here
		converter
				.setObjectMapper(new AndroidModule(null).provideObjectMapper());
		RetrofitObjectPersisterFactory factory = new RetrofitObjectPersisterFactory(
				application, converter);

		cacheManager.addPersister(factory);
		return cacheManager;
	}

	@Override
	public Builder createRestAdapterBuilder() {
		return new RestAdapter.Builder();
	}
}
