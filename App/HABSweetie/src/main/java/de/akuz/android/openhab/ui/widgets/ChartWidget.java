package de.akuz.android.openhab.ui.widgets;

import java.util.Random;

import android.content.Context;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.util.UrlUtils;

public class ChartWidget extends ImageWidget {

	private Random random = new Random();

	public ChartWidget(Context context, Widget widget) {
		super(context, widget);
	}

	@Override
	protected String getImageUrl() {
		String rrdUrl = UrlUtils.concat(widget.getBaseUrl(), "rrdchart.png?");
		if (widget.getItem().type.equals("GroupItem")) {
			rrdUrl = rrdUrl + "groups=";
		} else {
			rrdUrl = rrdUrl + "items=";
		}
		rrdUrl = rrdUrl + widget.getItem().name + "&period="
				+ widget.getPeriod() + "&random="
				+ String.valueOf(random.nextInt());
		return rrdUrl;
	}

}
