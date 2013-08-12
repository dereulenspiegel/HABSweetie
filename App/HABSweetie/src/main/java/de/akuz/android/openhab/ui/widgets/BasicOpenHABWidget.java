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
import de.akuz.android.openhab.util.ImageLoadHelper;
import de.akuz.android.openhab.util.Strings;

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
	ImageLoadHelper imageLoader;

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
		if (Strings.isEmpty(widget.getImageUrl())) {
			widgetImage.setVisibility(View.GONE);
		} else {
			widgetImage.setVisibility(View.VISIBLE);
		}
		if (Strings.isEmpty(widget.getLabel())) {
			widgetText.setVisibility(View.GONE);
		} else {
			widgetText.setText(widget.getLabel());
			widgetText.setVisibility(View.VISIBLE);
		}

		if (!Strings.isEmpty(widget.getImageUrl())) {
			imageLoader.displayImage(widget.getImageUrl(), widgetImage);
		}
	}

	public void hideHeader(boolean hide) {
		if (hide) {
			widgetImage.setVisibility(View.GONE);
			widgetText.setVisibility(View.GONE);
			// widgetBase.setVisibility(View.GONE);
		} else {
			widgetImage.setVisibility(View.VISIBLE);
			widgetText.setVisibility(View.VISIBLE);
			// widgetBase.setVisibility(View.VISIBLE);
		}
	}

}
