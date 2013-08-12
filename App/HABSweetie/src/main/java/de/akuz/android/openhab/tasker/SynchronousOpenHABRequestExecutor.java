package de.akuz.android.openhab.tasker;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import dagger.ObjectGraph;
import de.akuz.android.openhab.core.OpenHABRestService;

public class SynchronousOpenHABRequestExecutor extends OpenHABRestService {

	public <T> T executeRequest(GoogleHttpClientSpiceRequest<T> request)
			throws SpiceException {
		try {
			request.setHttpRequestFactory(createRequestFactory());
			return request.loadDataFromNetwork();
		} catch (Exception e) {
			throw new SpiceException(e);
		}
	}

	public void setObjectGraph(ObjectGraph objectGraph) {
		this.objectGraph = objectGraph;
	}

}
