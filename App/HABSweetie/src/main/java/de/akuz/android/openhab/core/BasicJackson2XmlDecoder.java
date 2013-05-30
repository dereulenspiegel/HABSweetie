package de.akuz.android.openhab.core;

import java.io.IOException;

import javax.inject.Inject;

import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Transport.EVENT_TYPE;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;

public class BasicJackson2XmlDecoder<T extends AbstractOpenHABObject>
		implements Decoder<String, T> {

	private final static String TAG = BasicJackson2XmlDecoder.class
			.getSimpleName();

	private Class<T> resultClass;

	private String baseUrl;

	@Inject
	ObjectMapper mapper;

	public BasicJackson2XmlDecoder(String baseUrl, Class<T> resultClass) {
		this.resultClass = resultClass;
		this.baseUrl = baseUrl;
	}

	@Override
	public T decode(EVENT_TYPE e, String s) {
		if (EVENT_TYPE.MESSAGE == e) {
			// Log.d(TAG, resultClass.getSimpleName() + " Decoding message " +
			// s);
			try {
				long receivedAt = System.currentTimeMillis();
				T object = mapper.readValue(s, resultClass);
				object.setReceivedAt(receivedAt);
				object.setBaseUrl(baseUrl);
				return object;
			} catch (IOException e1) {
				// Ignore for now. Returning null will indicate a parsing error
				// to wAsync
				// Log.e(TAG, "Error decoding the message", e1);
			}
		}
		return null;
	}

}
