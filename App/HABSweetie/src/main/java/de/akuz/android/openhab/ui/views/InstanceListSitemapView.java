package de.akuz.android.openhab.ui.views;

import android.content.Context;
import android.widget.TextView;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Sitemap;

public class InstanceListSitemapView extends BaseListView<Sitemap> {

	private TextView sitemapNameTextView;

	public InstanceListSitemapView(Context context) {
		super(context);
	}

	@Override
	protected void buildUi() {
		setLayout(R.layout.instance_list_sitemap_view);
		sitemapNameTextView = findView(R.id.sitemapName);
	}

	@Override
	protected void objectUpdated(Sitemap object) {
		sitemapNameTextView.setText(object.name);

	}

}
