package de.akuz.android.openhab.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Event;

import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.XmlObjectParser;

import de.akuz.android.openhab.core.objects.AbstractOpenHABObject;

public class BasicJackson2XmlDecoder<T extends AbstractOpenHABObject>
		implements Decoder<String, T> {

	private final static String TAG = BasicJackson2XmlDecoder.class
			.getSimpleName();

	private final static XmlObjectParser parser = new XmlObjectParser(
			new XmlNamespaceDictionary().set("", ""));

	private Class<T> resultClass;

	private String baseUrl;

	public BasicJackson2XmlDecoder(String baseUrl, Class<T> resultClass) {
		this.resultClass = resultClass;
		this.baseUrl = baseUrl;
	}

	@Override
	public T decode(Event e, String s) {
		if (e == Event.MESSAGE) {
			// Log.d(TAG, resultClass.getSimpleName() + " Decoding message " +
			// s);
			long receivedAt = System.currentTimeMillis();
			ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes());
			try {
				T object = parser.parseAndClose(is, Charset.forName("UTF-8"),
						resultClass);
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
