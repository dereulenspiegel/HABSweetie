package de.akuz.android.openhab.ui;

import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.google.api.client.http.HttpResponseException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.core.requests.SitemapsRequest;
import de.akuz.android.openhab.ui.ChooseSitemapDialogFragment.SelectSitemapListener;
import de.duenndns.ssl.InteractionReceiver;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PageActivity extends BaseActivity implements SelectSitemapListener {

	private final static String TAG = PageActivity.class.getSimpleName();

	private ViewPager pager;
	private OpenHABPagePagerAdapter pagerAdapter;

	private String baseUrl;

	private String selectedSitemapUrl;

	private PageActivityStateFragment stateFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "PageActivity has been created");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		Log.d(TAG, "Registering receiver for SSL Decision");
		sslInteractionReceiver = InteractionReceiver.registerReceiver(this);
		setContentView(R.layout.page_activity);
		pager = findView(R.id.pager);
		stateFragment = (PageActivityStateFragment) getSupportFragmentManager()
				.findFragmentByTag(PageActivityStateFragment.TAG);
		if (stateFragment == null) {
			pagerAdapter = new OpenHABPagePagerAdapter(this,
					getSupportFragmentManager());
			pager.setAdapter(pagerAdapter);
			stateFragment = new PageActivityStateFragment();
		} else {
			pagerAdapter = new OpenHABPagePagerAdapter(this,
					getSupportFragmentManager(),
					stateFragment.getAvailablePageFragments());
			pager.setAdapter(pagerAdapter);
			pager.setCurrentItem(stateFragment.getCurrentViewPagerPage());
		}
		pager.setOnPageChangeListener(pagerAdapter);
		pager.setOffscreenPageLimit(0);

		getActionBar().setTitle("HABSweetie");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Log.d(TAG, "onCreate of PageActivity complete");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Resuming PageActivity");
		baseUrl = getPreferenceStringValue(R.string.pref_url_key);
		Log.d(TAG, "Using base url " + baseUrl);
		String pageUrl = getPreferenceStringValue(R.string.pref_default_sitemap_url_key);
		if (pageUrl != null && baseUrl != null && !pageUrl.startsWith(baseUrl)) {
			pageUrl = null;
		}
		if ((pageUrl != null && baseUrl != null) || selectedSitemapUrl != null) {
			loadSubPage(pageUrl);
		} else if (baseUrl != null) {
			loadAvailableSitemaps();
		} else {
			makeCrouton(R.string.please_configure_this_app, Style.ALERT);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stateFragment.setAvailablePageFragments(pagerAdapter.getFragmentList());
		stateFragment.setCurrentViewPagerPage(pager.getCurrentItem());
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

	public void loadSubPage(Page page) {
		Log.d(TAG, "Loading page: " + page.getLink());
		int position = pagerAdapter.showPage(page);
		pager.setCurrentItem(position, true);
	}

	public void loadSubPage(String pageUrl) {
		pagerAdapter.initializeWithFirstPage(pageUrl);
		pager.setCurrentItem(0, true);
	}

	@Override
	public void sitemapSelected(Sitemap selectedSitemap) {
		selectedSitemapUrl = selectedSitemap.link;
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
