package de.akuz.android.openhab.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Event;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

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

	private String rootNodeName;

	public BasicJackson2XmlDecoder(String baseUrl, Class<T> resultClass,
			String rootNodeName) {
		this.resultClass = resultClass;
		this.baseUrl = baseUrl;
		this.rootNodeName = rootNodeName;
	}

	@Override
	public T decode(Event e, String s) {
		try {
			if (e == Event.MESSAGE && verifyRootNodeName(s, rootNodeName)) {

				Log.d(TAG, resultClass.getSimpleName()
						+ ": Decoding message:\n " + s);
				long receivedAt = System.currentTimeMillis();
				ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes());

				T object = parser.parseAndClose(is, Charset.forName("UTF-8"),
						resultClass);
				object.setReceivedAt(receivedAt);
				object.setBaseUrl(baseUrl);
				return object;
			}

		} catch (XmlPullParserException e1) {
			Log.e(TAG, "Can't parse XML", e1);
		} catch (IOException e1) {
			Log.e(TAG, "Error decoding the message, returning NULL", e1);
		}
		return null;
	}

	protected boolean verifyRootNodeName(String s, String expectedName)
			throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(s));
		while (parser.getEventType() != XmlPullParser.START_TAG) {
			parser.next();
		}
		String rootNodeName = parser.getName();
		if (expectedName.equalsIgnoreCase(rootNodeName)) {
			return true;
		}
		Log.d(TAG, "The root node name " + rootNodeName
				+ " does not match the expected root node name " + expectedName);
		return false;
	}

}
