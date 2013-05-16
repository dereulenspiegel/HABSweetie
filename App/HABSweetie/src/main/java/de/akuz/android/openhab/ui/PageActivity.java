package de.akuz.android.openhab.ui;

import java.util.Map;
import java.util.WeakHashMap;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.api.client.http.HttpResponseException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.core.requests.SitemapsRequest;
import de.akuz.android.openhab.ui.ChooseSitemapDialogFragment.SelectSitemapListener;
import de.duenndns.ssl.InteractionReceiver;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PageActivity extends BaseActivity implements SelectSitemapListener {

	private final static String TAG = PageActivity.class.getSimpleName();

	private LinearLayout pageFragmentContainerRight;
	private LinearLayout pageFragmentContainerLeft;

	private Fragment currentFragment;
	private Fragment leftFragment;
	private Fragment rightFragment;

	private String baseUrl;

	private String selectedSitemapUrl;

	private Map<String, PageFragment> fragmentCache = new WeakHashMap<String, PageFragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "PageActivity has been created");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		Log.d(TAG, "Registering receiver for SSL Decision");
		sslInteractionReceiver = InteractionReceiver.registerReceiver(this);
		setContentView(R.layout.page_activity);
		pageFragmentContainerLeft = findView(R.id.pageFragmentContainerLeft);
		pageFragmentContainerRight = findView(R.id.pageFragmentContainerRight);

		getActionBar().setTitle("HABSweetie");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Log.d(TAG, "onCreate of PageActivity complete");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Resuming PageActivity");
		currentFragment = getFragmentManager().findFragmentById(
				R.id.pageFragmentContainerLeft);
		if (currentFragment == null) {
			baseUrl = getPreferenceStringValue(R.string.pref_url_key);
			Log.d(TAG, "Using base url " + baseUrl);
			String pageUrl = getPreferenceStringValue(R.string.pref_default_sitemap_url_key);
			if (pageUrl != null && baseUrl != null
					&& !pageUrl.startsWith(baseUrl)) {
				pageUrl = null;
			}
			if ((pageUrl != null && baseUrl != null)
					|| selectedSitemapUrl != null) {
				loadSubPage(pageUrl);
			} else if (baseUrl != null) {
				loadAvailableSitemaps();
			} else {
				makeCrouton(R.string.please_configure_this_app, Style.ALERT);
			}
		}
	}

	public void loadingIndicatorTrue() {
		setProgressBarIndeterminateVisibility(Boolean.TRUE);
	}

	public void loadingIndicatorFalse() {
		setProgressBarIndeterminateVisibility(Boolean.FALSE);
	}

	private void loadAvailableSitemaps() {
		loadingIndicatorTrue();
		spiceManager.execute(new SitemapsRequest(baseUrl), baseUrl,
				DurationInMillis.NEVER, new RequestListener<SitemapsResult>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						Exception cause = (Exception) spiceException.getCause();
						loadingIndicatorFalse();
						handleException(cause);
					}

					@Override
					public void onRequestSuccess(SitemapsResult result) {
						loadingIndicatorFalse();
						if (result.getSitemap() != null
								&& result.getSitemap().size() == 1) {
							Editor edit = PreferenceManager
									.getDefaultSharedPreferences(
											PageActivity.this).edit();
							edit.putString(
									getString(R.string.pref_default_sitemap_url_key),
									result.getSitemap().get(0).homepage.link);
							edit.commit();
							loadSubPage(result.getSitemap().get(0).homepage.link);
						} else if (result.getSitemap() != null
								&& result.getSitemap().size() > 1) {
							ChooseSitemapDialogFragment fragment = ChooseSitemapDialogFragment
									.build(result.getSitemap(),
											PageActivity.this);
							fragment.show(getFragmentManager(), "chooseSitemap");
						} else {
							makeCrouton(R.string.error_no_sitemaps_found,
									Style.ALERT);
						}

					}
				});
	}

	public void loadSubPage(String pageUrl) {
		Log.d(TAG, "Loading page: " + pageUrl);
		selectedSitemapUrl = pageUrl;
		loadingIndicatorTrue();
		if (isSinglePaneMode()) {
			loadSubPageSinglePaneMode(pageUrl);
		} else {
			loadSubPageMultiPaneMode(pageUrl);
		}
	}

	public void loadParentPage(String pageUrl) {
		if (isSinglePaneMode()) {
			loadSubPageSinglePaneMode(pageUrl);
		} else {
			loadParentPageMultiPane(pageUrl);
		}
	}

	public void loadParentPageMultiPane(String pageUrl) {
		if (rightFragment == null) {
			return;
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		PageFragment parentPage = getFragmentForUrl(pageUrl);
		rightFragment = leftFragment;
		ft.replace(R.id.pageFragmentContainerLeft, parentPage);
		leftFragment = parentPage;
		ft.replace(R.id.pageFragmentContainerRight, rightFragment);
		ft.commit();
	}

	public boolean isSinglePaneMode() {
		return pageFragmentContainerRight == null
				|| pageFragmentContainerRight.getVisibility() == View.GONE;
	}

	private void loadSubPageMultiPaneMode(String pageUrl) {
		Log.d(TAG, "Loading sub page in multi pane mode");
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		PageFragment subPage = getFragmentForUrl(pageUrl);
		if (leftFragment != null) {
			ft.remove(leftFragment);
		}
		if (rightFragment != null) {
			ft.remove(rightFragment);
		}
		if (leftFragment == null) {
			leftFragment = subPage;
			pageFragmentContainerRight.setVisibility(View.GONE);
			ft.add(R.id.pageFragmentContainerLeft, subPage);
		} else if (rightFragment == null) {
			pageFragmentContainerRight.setVisibility(View.VISIBLE);
			rightFragment = subPage;
			ft.addToBackStack(null);
			ft.add(R.id.pageFragmentContainerRight, subPage);
		} else {
			ft.remove(rightFragment);
			ft.replace(R.id.pageFragmentContainerLeft, rightFragment);
			ft.replace(R.id.pageFragmentContainerLeft, subPage);
			ft.addToBackStack(null);
			rightFragment = leftFragment;
			leftFragment = subPage;
		}
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		ft.commitAllowingStateLoss();
	}

	private void loadSubPageSinglePaneMode(String pageUrl) {
		Log.d(TAG, "Loading page in single pane mode" + pageUrl);
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		PageFragment pageFragment = getFragmentForUrl(pageUrl);
		trans.replace(R.id.pageFragmentContainerLeft, pageFragment);
		if (currentFragment != null) {
			trans.addToBackStack(null);
		}
		currentFragment = pageFragment;
		trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		trans.commitAllowingStateLoss();
	}

	@Override
	public void sitemapSelected(Sitemap selectedSitemap) {
		loadSubPage(selectedSitemap.homepage.link);

	}

	@Override
	public void canceled() {
		makeCrouton(R.string.warning_sitemap_needed, Style.INFO);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getFragmentManager().popBackStack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private PageFragment getFragmentForUrl(String pageUrl) {
		if (fragmentCache.containsKey(pageUrl)) {
			PageFragment fragment = fragmentCache.get(pageUrl);
			if (fragment != null) {
				return fragment;
			}
		}
		PageFragment fragment = PageFragment.build(pageUrl);
		fragmentCache.put(pageUrl, fragment);
		return fragment;
	}

	private void handleException(Throwable t) {
		loadingIndicatorFalse();
		if (t instanceof HttpResponseException) {
			HttpResponseException e = (HttpResponseException) t;
			if (e.getStatusCode() == 401) {
				makeCrouton(R.string.error_auth_failed, Style.ALERT);
				return;
			}
		}
		makeCrouton(R.string.error_generic, Style.ALERT, t.getMessage());
	}

	public void updateTitleAndIcon(String title, Drawable icon) {
		if (title != null) {
			getActionBar().setTitle(title);
		} else {
			getActionBar().setTitle(R.string.app_name);
		}
		if (icon != null) {
			getActionBar().setIcon(icon);
		} else {
			getActionBar().setIcon(R.drawable.ic_launcher);
		}
	}
}
