package de.akuz.android.openhab.core;

import java.io.IOException;

import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Event;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.util.Log;
import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;

public class BasicJacksonDecoder<T extends AbstractOpenHABObject> implements
		Decoder<String, T> {

	private final static String TAG = BasicJacksonDecoder.class.getSimpleName();

	private ObjectMapper mapper = new ObjectMapper();

	private Class<T> resultClass;

	private String baseUrl;

	public BasicJacksonDecoder(String baseUrl, Class<T> resultClass) {
		this.resultClass = resultClass;
		this.baseUrl = baseUrl;
	}

	@Override
	public T decode(Event e, String s) {
		Log.d(TAG, "Decoding received message: " + s);
		if (e == Event.MESSAGE) {
			try {
				T result = mapper.readValue(s, resultClass);
				result.setBaseUrl(baseUrl);
				return result;
			} catch (JsonParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
}
