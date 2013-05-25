package de.akuz.android.openhab.ui.widgets;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Widget;

public abstract class BasicOpenHABWidget extends AbstractOpenHABWidget {

	protected TextView widgetText;
	protected ImageView widgetImage;
	protected View widgetBase;

	protected final static String decimalPattern = "#,###,##0.0";
	protected final static DecimalFormat df;
	static {
		NumberFormat f = NumberFormat.getInstance(Locale.ENGLISH);
		if (f instanceof DecimalFormat) {
			df = (DecimalFormat) f;
			df.applyPattern(decimalPattern);
		} else {
			df = new DecimalFormat(decimalPattern);
			df.applyPattern(decimalPattern);
		}
	}

	@Inject
	ImageLoader imageLoader;

	public BasicOpenHABWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected void buildUi() {
		widgetText = findView(R.id.widgetText);
		widgetImage = findView(R.id.widgetIcon);
		widgetBase = findView(R.id.widgetBase);
	}

	@Override
	protected void widgetUpdated(Widget widget) {
		widgetText.setText(widget.getLabel());
		widgetText.setVisibility(View.VISIBLE);
		if (widget.getLabel() == null && widget.getIcon() == null) {
			hideHeader(true);
		} else {
			hideHeader(false);
		}
		if (widget.getImageUrl() != null) {
			imageLoader.displayImage(widget.getImageUrl(), widgetImage);
		}
	}

	public void hideHeader(boolean hide) {
		if (hide) {
			widgetImage.setVisibility(View.GONE);
			widgetText.setVisibility(View.GONE);
//			widgetBase.setVisibility(View.GONE);
		} else {
			widgetImage.setVisibility(View.VISIBLE);
			widgetText.setVisibility(View.VISIBLE);
//			widgetBase.setVisibility(View.VISIBLE);
		}
	}

}
