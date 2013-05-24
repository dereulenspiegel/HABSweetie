package de.akuz.android.openhab.ui;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.channels.ClosedChannelException;

import javax.inject.Inject;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.api.client.http.HttpResponseException;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.PageConnectionInterface;
import de.akuz.android.openhab.core.PageUpdateListener;
import de.akuz.android.openhab.core.objects.Item;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Widget;
import de.akuz.android.openhab.ui.widgets.AbstractOpenHABWidget.ItemCommandInterface;
import de.akuz.android.openhab.ui.widgets.ItemUpdateListener;
import de.akuz.android.openhab.util.Utils;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PageFragment extends BaseFragment implements ItemCommandInterface,
		PageUpdateListener, ImageLoadingListener {

	public final static String PAGE_URL_ARG = "pageUrl";

	private String baseUrl;
	private String pageUrl;

	private ListView widgetList;

	private WidgetListAdapter listAdapter;

	private Page page;

	private final static String TAG = PageFragment.class.getSimpleName();

	private PageActivity pageActivity;

	private ProgressDialogFragment progressDialog;

	@Inject
	PageConnectionInterface pageConnection;

	private OpenHABPagePagerAdapter pageAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baseUrl = getPreferenceStringValue(R.string.pref_url_key);
		pageUrl = getArguments().getString(PAGE_URL_ARG);
		pageActivity = (PageActivity) getActivity();
		Log.d(TAG, "PageFragment has been created");

		listAdapter = new WidgetListAdapter(this);
		// ((BaseActivity) getActivity()).inject(pageConnection);
		pageConnection.registerUpdateListener(this);
		pageConnection.open(baseUrl, pageUrl);
		loadCompletePage();

	}

	private void setPageAdapter(OpenHABPagePagerAdapter pagerAdapter) {
		this.pageAdapter = pagerAdapter;
	}

	private void loadCompletePage() {
		progressDialog = ProgressDialogFragment
				.build(getString(R.string.message_loading_page));
		progressDialog.show(getFragmentManager(), "progressDialog");
		pageConnection.loadCompletePage();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Closing web socket");
		pageConnection.close();
		super.onDestroy();
	}

	@Override
	protected void buildUi() {
		Log.d(TAG, "Building PageFragment UI");
		setView(R.layout.page_fragment);
		widgetList = findView(R.id.widgetList);
		widgetList.setAdapter(listAdapter);
		widgetList.setDivider(null);
		widgetList.setDividerHeight(0);

	}

	public static PageFragment build(String pageUrl,
			OpenHABPagePagerAdapter adapter) {
		Bundle args = new Bundle();
		args.putString(PAGE_URL_ARG, pageUrl);
		PageFragment fragment = new PageFragment();
		fragment.setArguments(args);
		fragment.setPageAdapter(adapter);
		return fragment;
	}

	private void updateActionBar() {
		// if (isVisible() && pageActivity != null) {
		// if (page.getIconUrl() != null) {
		// ImageLoader.getInstance().loadImage(page.getIconUrl(), this);
		// } else {
		// pageActivity.updateTitleAndIcon(page.getTitle(), null);
		// }
		// }
	}

	@Override
	public void onResume() {
		pageActivity = (PageActivity) getActivity();
		super.onResume();
	}

	private void updatePage(final Page page) {
		if (page.getWidget() != null) {
			this.page = page;
		} else {
			this.page.setIcon(page.getIcon());
			this.page.setTitle(page.getTitle());
		}
		updateActionBar();
		if (page.getWidget() != null) {
			listAdapter.batchAddOrUpdateWidgets(page.getWidget());
		}

	}

	@Override
	public void sendCommand(Item item, String command,
			final ItemUpdateListener updateListener) {
		Log.d(TAG, "Sending command " + command + " to item " + item.name);
		pageConnection.sendCommand(item, command, updateListener);
	}

	private void handleHttpResponseException(HttpResponseException exception) {
		if (401 == exception.getStatusCode()) {
			// TODO ask user for credentials
			makeCrouton(R.string.error_auth_failed, Style.ALERT);
		} else {
			pageActivity.makeCrouton(R.string.error_generic, Style.ALERT,
					exception.getMessage());
		}
	}

	@Override
	public void loadPage(Page page) {
		if (!pageActivity.isFinishing()) {
			pageActivity.loadSubPage(page);
		}

	}

	@Override
	public boolean serverPushEnabled() {
		return pageConnection.isServerPushEnabled();
	}

	@Override
	public void widgetUpdateReceived(Widget widget) {
		Log.d(TAG, "Received widget update");
		listAdapter.addWidget(widget);

	}

	@Override
	public void pageUpdateReceived(Page page) {
		// Ugly workaround for cases where widget updates are parsed as page
		// updates
		if (progressDialog != null) {
			progressDialog.dismissAllowingStateLoss();
			progressDialog = null;
		}
		if (page.getId() != null) {
			Log.d(TAG, "Received Page update");
			updatePage(page);
			pageActivity.loadingIndicatorFalse();
		} else {
			Log.d(TAG, "Received widget update");
			listAdapter.batchAddOrUpdateWidgets(page.getWidget());
		}

	}

	@Override
	public void exceptionOccured(Throwable t) {
		if (progressDialog != null) {
			progressDialog.dismissAllowingStateLoss();
			progressDialog = null;
		}
		pageActivity.loadingIndicatorFalse();
		if (Utils.hasCause(t, IOException.class)
				&& t.getMessage().equals("Invalid handshake response")) {
			// TODO: For now we ignore, find a way to handle this properly
			return;
		}
		if (Utils.hasCause(t, ClosedChannelException.class)) {
			makeCrouton(R.string.error_generic, Style.ALERT,
					"WebSocketConnection closed");
		} else if (t instanceof HttpResponseException) {
			handleHttpResponseException((HttpResponseException) t);
		} else if (Utils.hasCause(t, java.net.ConnectException.class)) {
			makeCrouton(R.string.error_connect, Style.ALERT);
		} else if (Utils.hasCause(t, SocketTimeoutException.class)) {
			makeCrouton(R.string.error_timeout, Style.ALERT);
		} else {
			handleException(t);
		}
	}

	public Page getPage() {
		return page;
	}

	@Override
	public void showDialog(DialogFragment dialog) {
		dialog.show(getFragmentManager(), null);

	}

	@Override
	public void onLoadingStarted(String imageUri, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadingFailed(String imageUri, View view,
			FailReason failReason) {
		if (isVisible()) {
			pageActivity.updateTitleAndIcon(page.getTitle(), null);
		}

	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		if (isVisible()) {
			BitmapDrawable icon = new BitmapDrawable(getResources(),
					loadedImage);
			pageActivity.updateTitleAndIcon(page.getTitle(), icon);
		}

	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		// TODO Auto-generated method stub

	}

}
