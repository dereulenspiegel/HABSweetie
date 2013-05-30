package de.akuz.android.openhab;

import javax.inject.Singleton;

import android.content.Context;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nostra13.universalimageloader.core.ImageLoader;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import de.akuz.android.openhab.core.BasicJackson2XmlDecoder;
import de.akuz.android.openhab.core.JacksonConverter;
import de.akuz.android.openhab.ui.WidgetListAdapter;
import de.akuz.android.openhab.ui.widgets.AbstractOpenHABWidget;
import de.akuz.android.openhab.ui.widgets.BasicOpenHABWidget;
import de.akuz.android.openhab.ui.widgets.ChartWidget;
import de.akuz.android.openhab.ui.widgets.ColorpickerWidget;
import de.akuz.android.openhab.ui.widgets.FrameWidget;
import de.akuz.android.openhab.ui.widgets.ImageWidget;
import de.akuz.android.openhab.ui.widgets.OpenHABWidgetFactory;
import de.akuz.android.openhab.ui.widgets.SelectionWidget;
import de.akuz.android.openhab.ui.widgets.SetpointWidget;
import de.akuz.android.openhab.ui.widgets.SliderWidget;
import de.akuz.android.openhab.ui.widgets.SwitchWidget;
import de.akuz.android.openhab.ui.widgets.TextWidget;
import de.akuz.android.openhab.ui.widgets.VideoWidget;
import de.akuz.android.openhab.ui.widgets.WebviewWidget;

@Module(library = true, injects = { BootstrapApplication.class,
		WidgetListAdapter.class, OpenHABWidgetFactory.class, FrameWidget.class,
		TextWidget.class, AbstractOpenHABWidget.class, ImageWidget.class,
		SliderWidget.class, SwitchWidget.class, SelectionWidget.class,
		ColorpickerWidget.class, BasicOpenHABWidget.class,
		SetpointWidget.class, WebviewWidget.class, VideoWidget.class,
		ChartWidget.class, BasicJackson2XmlDecoder.class,
		JacksonConverter.class })
public class AndroidModule {

	private final BootstrapApplication app;

	public AndroidModule(BootstrapApplication app) {
		this.app = app;
	}

	@Provides
	@Singleton
	public Context provideApplicationContext() {
		return app;
	}

	@Provides
	@Singleton
	public ImageLoader provideImageLoader() {
		return ImageLoader.getInstance();
	}

	@Provides
	@Singleton
	public OpenHABWidgetFactory provideWidgetFactory(ObjectGraph objectGraph) {
		return new OpenHABWidgetFactory(objectGraph);
	}

	@Provides
	@Singleton
	public ObjectGraph provideObjectGraph() {
		return app.getObjectGraph();
	}

	@Provides
	@Singleton
	public ObjectMapper provideObjectMapper() {
		XmlFactory f = new XmlFactory(new InputFactoryImpl(),
				new OutputFactoryImpl());
		JacksonXmlModule module = new JacksonXmlModule();
		XmlMapper mapper = new XmlMapper(f, module);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
				true);
		mapper.configure(
				DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		return mapper;
	}

}
