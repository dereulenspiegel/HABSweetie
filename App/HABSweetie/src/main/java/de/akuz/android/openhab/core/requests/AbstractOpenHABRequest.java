package de.akuz.android.openhab.core.requests;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Header;
import retrofit.client.OkClient;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import de.akuz.android.openhab.core.JacksonConverter;
import de.akuz.android.openhab.core.OpenHABAuthManager;
import de.akuz.android.openhab.core.OpenHABRestInterface;
import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;

public abstract class AbstractOpenHABRequest<RESULT extends AbstractOpenHABObject>
		extends RetrofitSpiceRequest<RESULT> {

	private final static String TAG = AbstractOpenHABRequest.class
			.getSimpleName();

	protected String baseUrl;

	protected Class<RESULT> resultClass;

	public AbstractOpenHABRequest(Class<RESULT> clazz, String baseUrl) {
		super(clazz);
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
				.setRequestHeaders(headers).setClient(new OkClient())
				.setConverter(new JacksonConverter()).setDebug(true).build()
				.create(OpenHABRestInterface.class);
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
			// TODO Auto-generated method stub
			return headerList;
		}

	}

}
