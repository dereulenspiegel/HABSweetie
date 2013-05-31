package de.akuz.android.openhab.core;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

import javax.inject.Inject;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import android.util.Log;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class JacksonConverter implements Converter {

	private final static String TAG = JacksonConverter.class.getSimpleName();

	@Inject
	ObjectMapper mapper;

	@Override
	public Object fromBody(TypedInput body, Type type)
			throws ConversionException {
		checkAndCreateMapper();
		try {
			if (type instanceof Class<?>) {

				return mapper.readValue(body.in(), (Class<?>) type);
			} else {
				throw new IllegalArgumentException("type must be of type class");
			}
		} catch (Exception e) {
			Log.e(TAG, "Error parsing body", e);
			throw new ConversionException(e);
		}
	}

	private void checkAndCreateMapper() {
		if (mapper == null) {
			XmlFactory f = new XmlFactory(new InputFactoryImpl(),
					new OutputFactoryImpl());
			JacksonXmlModule module = new JacksonXmlModule();
			module.setDefaultUseWrapper(false);
			mapper = new XmlMapper(f, module);
			mapper.configure(
					DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			mapper.configure(
					DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
					true);
			mapper.configure(
					DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING,
					true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);
		}
	}

	@Override
	public TypedOutput toBody(Object object) {
		checkAndCreateMapper();
		try {
			return new XmlTypedOutput(mapper.writeValueAsBytes(object));
		} catch (Exception e) {
			Log.e(TAG, "Cant't serialize object of class " + object.getClass(),
					e);
		}
		return null;
	}

	public static class XmlTypedOutput implements TypedOutput {

		private byte[] data;

		public XmlTypedOutput(byte[] data) {
			this.data = data;
		}

		@Override
		public String fileName() {
			return null;
		}

		@Override
		public String mimeType() {
			return "application/xml; charset=UTF-8";
		}

		@Override
		public long length() {
			return data.length;
		}

		@Override
		public void writeTo(OutputStream out) throws IOException {
			out.write(data);
		}

	}

	public void setObjectMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

}
