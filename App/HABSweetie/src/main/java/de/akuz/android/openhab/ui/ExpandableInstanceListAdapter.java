package de.akuz.android.openhab.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.core.requests.SitemapsRequest;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.ui.views.InstanceListSitemapView;
import de.akuz.android.openhab.ui.views.InstanceListTopView;
import de.akuz.android.openhab.ui.views.InstanceSitemapsLoadingFailedView;
import de.akuz.android.openhab.ui.views.LoadingSitemapsView;
import de.akuz.android.openhab.ui.views.OpenHABInstanceUtil;
import de.akuz.android.openhab.util.HABSweetiePreferences;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ExpandableInstanceListAdapter implements ExpandableListAdapter {

	private final static String TAG = ExpandableInstanceListAdapter.class
			.getSimpleName();

	@Inject
	HABSweetiePreferences prefs;

	@Inject
	SpiceManager spiceManager;

	@Inject
	OpenHABInstanceUtil instanceUtil;

	private List<OpenHABInstance> instances;
	private Map<OpenHABInstance, List<Sitemap>> sitemapMap = new HashMap<OpenHABInstance, List<Sitemap>>();
	private Set<OpenHABInstance> loadingFailedInstances = new HashSet<OpenHABInstance>();

	private DataSetObserver observer;

	private BaseActivity activity;

	public ExpandableInstanceListAdapter(BaseActivity ctx,
			List<OpenHABInstance> instances) {
		this.instances = instances;
		this.activity = ctx;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		OpenHABInstance instance = instances.get(groupPosition);
		List<Sitemap> sitemaps = sitemapMap.get(instance);
		if (sitemaps == null || sitemaps.size() == 0) {
			return null;
		}
		return sitemaps.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		OpenHABInstance instance = instances.get(groupPosition);
		List<Sitemap> sitemaps = sitemapMap.get(instance);
		if (loadingFailedInstances.contains(instance)) {
			if (convertView instanceof InstanceSitemapsLoadingFailedView) {
				return convertView;
			} else {
				return new InstanceSitemapsLoadingFailedView(activity);
			}
		} else if (sitemaps == null || sitemaps.size() == 0) {
			if (convertView != null
					&& convertView instanceof LoadingSitemapsView) {
				return convertView;
			} else if (convertView == null) {
				return new LoadingSitemapsView(activity);
			} else {
				Log.w(TAG,
						"Should return LoadingSitemapView, but convertView is of type "
								+ convertView.getClass().getName());
				return new LoadingSitemapsView(activity);
			}
		}
		InstanceListSitemapView view = null;
		if (convertView != null
				&& convertView instanceof InstanceListSitemapView) {
			view = (InstanceListSitemapView) convertView;
		} else {
			view = new InstanceListSitemapView(activity);
		}
		view.updateObject(sitemaps.get(childPosition));
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		OpenHABInstance instance = instances.get(groupPosition);
		List<Sitemap> sitemaps = sitemapMap.get(instance);
		return (sitemaps == null || sitemaps.size() == 0) ? 1 : sitemaps.size();
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return instances.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return instances != null ? instances.size() : 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		OpenHABInstance instance = (OpenHABInstance) getGroup(groupPosition);
		InstanceListTopView view = null;
		if (convertView != null) {
			view = (InstanceListTopView) convertView;
		} else {
			view = new InstanceListTopView(activity);
		}
		view.updateObject(instance);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		OpenHABInstance instance = instances.get(groupPosition);
		List<Sitemap> sitemaps = sitemapMap.get(instance);
		if (sitemaps != null && sitemaps.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return instances != null && instances.size() > 0;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		final OpenHABInstance instance = (OpenHABInstance) getGroup(groupPosition);
		OpenHABConnectionSettings setting = instanceUtil
				.chooseSetting(instance);
		final List<Sitemap> sitemapList = new ArrayList<Sitemap>(5);
		loadingFailedInstances.remove(instance);
		notifyDataSetChanged();
		SitemapsRequest request = new SitemapsRequest(setting);
		spiceManager.execute(request, setting.getBaseUrl(),
				DurationInMillis.ALWAYS_EXPIRED,
				new RequestListener<SitemapsResult>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						loadingFailedInstances.add(instance);
						activity.makeCrouton(R.string.error_loading_sitemaps,
								Style.ALERT, spiceException.getCause()
										.getMessage());
						notifyDataSetChanged();
					}

					@Override
					public void onRequestSuccess(SitemapsResult result) {
						sitemapList.addAll(result.getSitemap());
						sitemapMap.put(instance, sitemapList);
						notifyDataSetChanged();
					}
				});
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		this.observer = observer;

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		observer = null;

	}

	public void reloadInstances() {
		instances = prefs.getAllConfiguredInstances();
		notifyDataSetChanged();
	}

	private void notifyDataSetChanged() {
		observer.onChanged();
	}

}
