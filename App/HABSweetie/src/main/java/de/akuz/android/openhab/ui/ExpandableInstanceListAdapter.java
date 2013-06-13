package de.akuz.android.openhab.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.akuz.android.openhab.core.objects.Sitemap;
import de.akuz.android.openhab.core.objects.SitemapsResult;
import de.akuz.android.openhab.core.requests.SitemapsRequest;
import de.akuz.android.openhab.settings.OpenHABConnectionSettings;
import de.akuz.android.openhab.settings.OpenHABInstance;
import de.akuz.android.openhab.ui.views.InstanceListSitemapView;
import de.akuz.android.openhab.ui.views.InstanceListTopView;
import de.akuz.android.openhab.ui.views.LoadingSitemapsView;
import de.akuz.android.openhab.ui.views.OpenHABInstanceUtil;
import de.akuz.android.openhab.util.HABSweetiePreferences;

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

	private DataSetObserver observer;

	private Context ctx;

	public ExpandableInstanceListAdapter(Context ctx,
			List<OpenHABInstance> instances) {
		this.instances = instances;
		this.ctx = ctx;
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
		if (sitemaps == null || sitemaps.size() == 0) {
			if (convertView != null
					&& convertView instanceof LoadingSitemapsView) {
				return convertView;
			} else if (convertView == null) {
				return new LoadingSitemapsView(ctx);
			} else {
				Log.w(TAG,
						"Shoudl return LoadingSitemapView, but convertView is of type "
								+ convertView.getClass().getName());
				return new LoadingSitemapsView(ctx);
			}
		}
		InstanceListSitemapView view = null;
		if (convertView != null && convertView instanceof InstanceListSitemapView) {
			view = (InstanceListSitemapView) convertView;
		} else {
			view = new InstanceListSitemapView(ctx);
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
			view = new InstanceListTopView(ctx);
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

		SitemapsRequest request = new SitemapsRequest(setting);
		spiceManager.execute(request, setting.getBaseUrl(),
				DurationInMillis.ALWAYS_EXPIRED,
				new RequestListener<SitemapsResult>() {

					@Override
					public void onRequestFailure(SpiceException spiceException) {
						// TODO show error message

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
