package de.akuz.android.openhab.ui;

import javax.inject.Inject;

import org.apache.http.client.HttpResponseException;

import roboguice.util.temp.Strings;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.core.requests.SitemapsRequest;
import de.akuz.android.openhab.ui.ChooseSitemapDialogFragment.SelectSitemapListener;
import de.akuz.android.openhab.util.HABSweetiePreferences;
import de.duenndns.ssl.InteractionReceiver;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PageActivity extends BaseActivity implements SelectSitemapListener {

	private final static String TAG = PageActivity.class.getSimpleName();

	private ViewPager pager;
	private OpenHABPagePagerAdapter pagerAdapter;

	private String baseUrl;

	private String selectedSitemapUrl;

	private PageActivityStateFragment stateFragment;

	@Inject
	HABSweetiePreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "PageActivity has been created");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		Log.d(TAG, "Registering receiver for SSL Decision");
		sslInteractionReceiver = InteractionReceiver.registerReceiver(this);
		setContentView(R.layout.page_activity);
		pager = findView(R.id.pager);
		pagerAdapter = new OpenHABPagePagerAdapter(this,
				getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
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
		baseUrl = prefs.getBaseUrl();
		Log.d(TAG, "Using base url " + baseUrl);
		// Trying to load a previous state from a configuration change;
		stateFragment = (PageActivityStateFragment) getSupportFragmentManager()
				.findFragmentByTag(PageActivityStateFragment.TAG);
		// If we have fragments to restore restore them, but only if the config
		// hasn't changed
		if (isAppConfigured() && !hasBaseUrlChanged() && stateFragment != null
				&& stateFragment.getAvailablePageFragments() != null) {
			Log.d(TAG, "Restoring previous state after config change");
			restorePreviousStateAfterConfigurationChange();
		} else if (hasBaseUrlChanged() && isAppConfigured()) {
			Log.d(TAG, "Base url has changed");
			loadAvailableSitemaps();
		} else if (isAppConfigured()) {
			String pageUrl = prefs.getDefaultSitemapUrl();
			if (!Strings.isEmpty(pageUrl)) {
				loadSubPage(pageUrl);
			} else {
				loadAvailableSitemaps();
			}
		} else {
			makeCrouton(R.string.please_configure_this_app, Style.ALERT);
		}
		stateFragment = new PageActivityStateFragment();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(
				PageActivityStateFragment.TAG);
		if (oldFragment != null) {
			ft.remove(oldFragment);
		}
		ft.add(stateFragment, PageActivityStateFragment.TAG);
		ft.commit();
	}

	private boolean isAppConfigured() {
		return prefs.getBaseUrl() != null;
	}

	private boolean hasBaseUrlChanged() {
		String defaultSitemapUrl = prefs.getDefaultSitemapUrl();
		String currentBaseUrl = prefs.getBaseUrl();
		if (defaultSitemapUrl == null) {
			return true;
		}
		return !defaultSitemapUrl.startsWith(currentBaseUrl);
	}

	private void restorePreviousStateAfterConfigurationChange() {
		Log.d(TAG, "We have "
				+ stateFragment.getAvailablePageFragments().size()
				+ " Fragments saved");
		pagerAdapter = new OpenHABPagePagerAdapter(this,
				getSupportFragmentManager(),
				stateFragment.getAvailablePageFragments());
		pagerAdapter.setFragmentCache(stateFragment.getFragmentCache());
		pager.setAdapter(pagerAdapter);
		pager.setCurrentItem(stateFragment.getCurrentViewPagerPage());
		pager.invalidate();
	}

	@Override
	protected void onStop() {
		stateFragment.setRemovedFragments(pagerAdapter.getRemovedFragments());
		stateFragment.setAvailablePageFragments(pagerAdapter.getFragmentList());
		stateFragment.setCurrentViewPagerPage(pager.getCurrentItem());
		stateFragment.setFragmentCache(pagerAdapter.getFragmentCache());
		super.onStop();
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
							prefs.setDefaultSitemapUrl(result.getSitemap().get(
									0).homepage.link);
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
			pagerAdapter.goOnePageUp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void handleException(Throwable t) {
		loadingIndicatorFalse();
		// FIXME determine correct Retrofit exceptions
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

	@Override
	public void onBackPressed() {
		if (pager.getCurrentItem() > 0) {
			pagerAdapter.goOnePageUp();
		} else {
			super.onBackPressed();
		}
	}
}
