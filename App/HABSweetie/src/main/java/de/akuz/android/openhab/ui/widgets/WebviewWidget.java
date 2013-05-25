package de.akuz.android.openhab.ui.widgets;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Widget;

public class WebviewWidget extends BasicOpenHABWidget {

	private WebView webView;

	public WebviewWidget(Context context, Widget widget) {
		super(context, widget);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void buildUi() {
		setView(R.layout.webview_widget);
		super.buildUi();
		webView = findView(R.id.webView);
		webView.setWebViewClient(new OpenHABWebViewClient());
	}

	@Override
	public void updateItem(Item item) {
		// Ignore
	}

	@Override
	protected void widgetUpdated(Widget widget) {
		super.widgetUpdated(widget);
		webView.loadUrl(widget.getFullUrl());
	}

	private class OpenHABWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
