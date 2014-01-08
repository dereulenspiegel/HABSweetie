package de.akuz.android.openhab.ui;

import javax.inject.Named;
import javax.inject.Singleton;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
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

@Module(injects = { OpenHABWidgetFactory.class, WidgetListAdapter.class,
		PageFragment.class, ChartWidget.class, ColorpickerWidget.class,
		FrameWidget.class, ImageWidget.class, SelectionWidget.class,
		SetpointWidget.class, SliderWidget.class, SwitchWidget.class,
		TextWidget.class, VideoWidget.class, WebviewWidget.class }, complete = false, library = true)
public class UiModule {

	private BaseActivity activity;

	public UiModule(BaseActivity activity) {
		this.activity = activity;
	}

	@Provides
	public FragmentManager provideFragmentManager() {
		return activity.getSupportFragmentManager();
	}

	@Provides
	@Named("ui")
	@Singleton
	public ObjectGraph provideObjectGraph() {
		return activity.getObjectGraph();
	}

	@Provides
	@Singleton
	public OpenHABWidgetFactory provideWidgetFactory(
			@Named("ui") ObjectGraph objectGraph) {
		return new OpenHABWidgetFactory(objectGraph);
	}

	@Provides
	@Singleton
	@Named(value = "activity")
	public Context provideActivityContext() {
		return activity;
	}

}
