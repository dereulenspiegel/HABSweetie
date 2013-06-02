package de.akuz.android.openhab.core.requests;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Header;
import retrofit.client.OkClient;
import android.util.Log;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.squareup.okhttp.OkHttpClient;

import de.akuz.android.openhab.core.JacksonConverter;
import de.akuz.android.openhab.core.OpenHABAuthManager;
import de.akuz.android.openhab.core.OpenHABOkAuthenticator;
import de.akuz.android.openhab.core.OpenHABRestInterface;
import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;

public abstract class AbstractOpenHABRequest<RESULT extends AbstractOpenHABObject>
		extends RetrofitSpiceRequest<RESULT> {

	private final static String TAG = AbstractOpenHABRequest.class
			.getSimpleName();

	protected String baseUrl;

	protected Class<RESULT> resultClass;

	protected static OkClient okClient;

	public AbstractOpenHABRequest(Class<RESULT> clazz, String baseUrl) {
		super(clazz);
		Log.d(TAG, "Using base url " + baseUrl);
		this.baseUrl = baseUrl;
		resultClass = clazz;
	}

	public abstract void setParameters(String... params);

	@Override
	public RESULT loadDataFromNetwork() throws Exception {
		RESULT result = executeRequest();
		result.setBaseUrl(baseUrl);
		return result;
	}

	protected OpenHABRestInterface getRestAdapter() {
		OpenHABRequestHeaders headers = null;
		if (OpenHABAuthManager.hasCredentials()) {
			headers = new OpenHABRequestHeaders(
					OpenHABAuthManager.getEncodedCredentials());
		} else {
			headers = new OpenHABRequestHeaders();
		}

		return getRestAdapterBuilder().setServer(baseUrl)
				.setRequestHeaders(headers).setClient(getOkClient())
				.setConverter(new JacksonConverter()).setDebug(true).build()
				.create(OpenHABRestInterface.class);
	}

	protected OkClient getOkClient() {
		if (okClient != null) {
			return okClient;
		}
		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.setFollowProtocolRedirects(true);
		okHttpClient.setAuthenticator(new OpenHABOkAuthenticator());
		OkClient client = new OkClient(okHttpClient);
		okClient = client;
		return client;
	}

	protected abstract RESULT executeRequest() throws Exception;

	protected static class OpenHABRequestHeaders implements
			retrofit.RequestHeaders {

		private List<Header> headerList = new ArrayList<Header>(3);

		public OpenHABRequestHeaders(String authorization) {
			this();
			headerList.add(new Header("Authorization", authorization));
		}

		public OpenHABRequestHeaders() {
			headerList.add(new Header("Accept", "application/xml"));
		}

		@Override
		public List<Header> get() {
			return headerList;
		}

	}

}
