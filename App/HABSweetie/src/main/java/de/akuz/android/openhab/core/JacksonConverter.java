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

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonConverter implements Converter {

	private final static String TAG = JacksonConverter.class.getSimpleName();

	@Inject
	ObjectMapper mapper;

	@Override
	public Object fromBody(TypedInput body, Type type)
			throws ConversionException {

		try {
			return mapper.readValue(body.in(), type.getClass());
		} catch (Exception e) {
			Log.e(TAG, "Error parsing body", e);
			throw new ConversionException(e);
		}
	}

	@Override
	public TypedOutput toBody(Object object) {
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
	
	public void setObjectMapper(ObjectMapper mapper){
		this.mapper = mapper;
	}

}
