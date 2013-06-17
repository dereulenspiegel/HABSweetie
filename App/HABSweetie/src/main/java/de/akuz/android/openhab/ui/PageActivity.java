package de.akuz.android.openhab.ui;

import javax.inject.Inject;

import roboguice.util.temp.Strings;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.actionbarsherlock.view.MenuItem;
import com.google.api.client.http.HttpResponseException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.OpenHABAuthManager;
import de.akuz.android.openhab.core.objects.Page;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.core.requests.SitemapsRequest;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.settings.wizard.ConnectionWizardActivity;
import de.akuz.android.openhab.ui.ChooseSitemapDialogFragment.SelectSitemapListener;
import de.akuz.android.openhab.util.HABSweetiePreferences;
import de.akuz.android.openhab.util.InteractionReceiver;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PageActivity extends BaseActivity implements
		SelectSitemapListener, OnChildClickListener {

	private final static String TAG = PageActivity.class.getSimpleName();

	private ViewPager pager;
	private OpenHABPagePagerAdapter pagerAdapter;

	private OpenHABInstance currentInstance;

	private PageActivityStateFragment stateFragment;

	@Inject
	ConnectivityManager conManager;

	@Inject
	HABSweetiePreferences prefs;

	private ExpandableListView instanceList;
	private ExpandableInstanceListAdapter instanceListAdapter;

	private DrawerLayout drawerLayout;
	private SherlockActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "PageActivity has been created");
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Registering receiver for SSL Decision");
		sslInteractionReceiver = InteractionReceiver.registerReceiver(this);

		setContentView(R.layout.page_activity);

		drawerLayout = findView(R.id.drawer_layout);
		instanceList = findView(R.id.instanceList);
		pager = findView(R.id.pager);
		pagerAdapter = new OpenHABPagePagerAdapter(this,
				getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(pagerAdapter);
		pager.setOffscreenPageLimit(0);
		instanceListAdapter = new ExpandableInstanceListAdapter(this,
				prefs.getAllConfiguredInstances());
		inject(instanceListAdapter);
		instanceList.setAdapter(instanceListAdapter);
		instanceList.setOnChildClickListener(this);

		Log.d(TAG, "onCreate of PageActivity complete");
		drawerToggle = new SherlockActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name);
		drawerLayout.setDrawerListener(drawerToggle);
		getSupportActionBar().setTitle("HABSweetie");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		drawerToggle.syncState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Resuming PageActivity");
		instanceListAdapter.reloadInstances();
		currentInstance = prefs.getDefaultInstance();
		stateFragment = (PageActivityStateFragment) getSupportFragmentManager()
				.findFragmentByTag(PageActivityStateFragment.TAG);
		// If we have fragments to restore restore them, but only if the config
		// hasn't changed
		if (stateFragment != null && stateFragment.isHasState()
				&& stateFragment.getSavedInstance() != null) {
			currentInstance = stateFragment.getSavedInstance();
		}
		setNewInstance(currentInstance);
		Log.d(TAG, "Creating state fragment");
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

	public OpenHABInstance getCurrentInstance() {
		return currentInstance;
	}

	public void setNewInstance(OpenHABInstance instance) {
		currentInstance = instance;
		if (currentInstance != null) {
			OpenHABAuthManager
					.updateCredentuals(chooseSetting(currentInstance));
		}
		if (isAppConfigured() && !hasBaseUrlChanged() && stateFragment != null
				&& stateFragment.getAvailablePageFragments() != null
				&& stateFragment.getAvailablePageFragments().size() > 0) {
			Log.d(TAG, "Restoring previous state after config change");
			restorePreviousStateAfterConfigurationChange();
		} else if (isAppConfigured() && hasBaseUrlChanged()) {
			loadAvailableSitemaps();
		} else if (isAppConfigured() && currentInstance != null) {
			if (!Strings.isEmpty(currentInstance.getDefaultSitemapId())) {
				String pageUrl = currentInstance
						.getDefaultSitemapUrl(chooseSetting(currentInstance));
				Log.d(TAG, "Loading default sitemap from url " + pageUrl);
				loadSubPage(pageUrl);
			} else {
				loadAvailableSitemaps();
			}
		} else {
			Log.d(TAG, "App is not configured, starting wizard");
			// makeCrouton(R.string.please_configure_this_app, Style.ALERT);
			Intent i = new Intent(this, ConnectionWizardActivity.class);
			startActivity(i);
		}
	}

	public void setNewInstance(OpenHABInstance instance, Sitemap sitemap) {
		currentInstance = instance;
		OpenHABConnectionSettings settings = instance
				.getSettingForCurrentNetwork(conManager);
		OpenHABAuthManager.updateCredentials(settings.getUsername(),
				settings.getPassword());
		loadSubPage(sitemap.homepage.link);
	}

	private boolean isAppConfigured() {
		return currentInstance != null;
	}

	private boolean hasBaseUrlChanged() {
		OpenHABInstance oldInstance = null;
		if (stateFragment != null) {
			oldInstance = stateFragment.getSavedInstance();
		}
		OpenHABInstance defaultInstance = currentInstance;
		if (oldInstance == null && defaultInstance == null) {
			return true;
		}
		if (oldInstance == null && defaultInstance != null) {
			return false;
		}
		String oldBaseUrl = chooseSetting(oldInstance).getBaseUrl();
		String currentBaseUrl = chooseSetting(defaultInstance).getBaseUrl();
		return !oldBaseUrl.equals(currentBaseUrl);
	}

	public OpenHABConnectionSettings chooseSetting(OpenHABInstance instance) {
		return instance.getSettingForCurrentNetwork(conManager);
	}

	private void restorePreviousStateAfterConfigurationChange() {
		Log.d(TAG, "We have "
				+ stateFragment.getAvailablePageFragments().size()
				+ " Fragments saved");
		currentInstance = stateFragment.getSavedInstance();
		OpenHABConnectionSettings settings = currentInstance
				.getSettingForCurrentNetwork(conManager);
		OpenHABAuthManager.updateCredentials(settings.getUsername(),
				settings.getPassword());
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
		stateFragment.setSavedInstance(currentInstance);
		stateFragment.setHasState(true);
		super.onStop();
	}

	public void loadingIndicatorTrue() {
		setProgressBarIndeterminateVisibility(Boolean.TRUE);
	}

	public void loadingIndicatorFalse() {
		setProgressBarIndeterminateVisibility(Boolean.FALSE);
	}

	private String getBaseUrl() {
		return chooseSetting(currentInstance).getBaseUrl();
	}

	private void loadAvailableSitemaps() {
		Log.d(TAG, "Loading all available sitemaps");
		String baseUrl = getBaseUrl();
		final ProgressDialogFragment progressDialog = ProgressDialogFragment
				.build(getString(R.string.message_loading_sitemaps));
		progressDialog.show(getSupportFragmentManager(), "sitemapsProgress");
		spiceManager.execute(new SitemapsRequest(baseUrl), baseUrl,
				DurationInMillis.ALWAYS_EXPIRED,
				new RequestListener<SitemapsResult>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						progressDialog.dismissAllowingStateLoss();
						Exception cause = (Exception) spiceException.getCause();
						handleException(cause);
					}

					@Override
					public void onRequestSuccess(SitemapsResult result) {
						loadingIndicatorFalse();
						progressDialog.dismissAllowingStateLoss();
						if (result.getSitemap() != null
								&& result.getSitemap().size() == 1) {
							currentInstance.setDefaultSitemapIdFromUrl(result
									.getSitemap().get(0));
							prefs.saveInstance(currentInstance);
							Log.d(TAG, "Got only one sitemap, loading it");
							loadSubPage(result.getSitemap().get(0).homepage.link);
						} else if (result.getSitemap() != null
								&& result.getSitemap().size() > 1) {
							Log.d(TAG,
									"Received multiple sitemaps, showing dialog");
							ChooseSitemapDialogFragment fragment = ChooseSitemapDialogFragment
									.build(result.getSitemap());
							inject(fragment);
							fragment.show(getSupportFragmentManager(),
									"chooseSitemap");
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
		pager.setAdapter(pagerAdapter);
		pager.setCurrentItem(0, true);
	}

	@Override
	public void sitemapSelected(Sitemap selectedSitemap, boolean useAsDefault) {
		Log.d(TAG, "Selected sitemap is " + selectedSitemap.name);
		currentInstance.setDefaultSitemapIdFromUrl(selectedSitemap);
		prefs.saveInstance(currentInstance);
		loadSubPage(selectedSitemap.homepage.link);
	}

	@Override
	public void canceled() {
		makeCrouton(R.string.warning_sitemap_needed, Style.INFO);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if (item.getItemId() == android.R.id.home) {
			pagerAdapter.goOnePageUp();
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
			getSupportActionBar().setTitle(title);
		} else {
			getSupportActionBar().setTitle(R.string.app_name);
		}
		if (icon != null) {
			getSupportActionBar().setIcon(icon);
		} else {
			getSupportActionBar().setIcon(R.drawable.ic_launcher);
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

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		OpenHABInstance instance = (OpenHABInstance) instanceListAdapter
				.getGroup(groupPosition);
		Sitemap sitemap = (Sitemap) instanceListAdapter.getChild(groupPosition,
				childPosition);
		setNewInstance(instance, sitemap);
		drawerLayout.closeDrawers();
		return true;
	}
}
