package de.akuz.android.openhab.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.objects.Page;

public class OpenHABPagePagerAdapter extends FragmentStatePagerAdapter
		implements OnPageChangeListener {

	private final static String TAG = OpenHABPagePagerAdapter.class
			.getSimpleName();

	private Map<String, PageFragment> fragmentCache = new WeakHashMap<String, PageFragment>();

	private Context mContext;

	private float pageWidth;

	private List<PageFragment> fragmentList = new ArrayList<PageFragment>();

	private Set<PageFragment> changedOrRemovedFragments = new HashSet<PageFragment>();

	public OpenHABPagePagerAdapter(Context ctx, FragmentManager fm) {
		super(fm);
		mContext = ctx;
		int pageCount = ctx.getResources().getInteger(R.integer.page_count);
		Log.d(TAG, "Working with a page count of " + pageCount);
		pageWidth = 1.0f / pageCount;
	}

	@Override
	public Fragment getItem(int position) {
		PageFragment fragment = fragmentList.get(position);
		return fragment;
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public float getPageWidth(int position) {
		if (fragmentList.size() > 1) {
			return pageWidth;
		}
		// return 1.0f;
		return pageWidth;
	}

	public void initializeWithFirstPage(String pageUrl) {
		if (fragmentList.size() > 0) {
			Log.w(TAG, "Reinitializing PageAdapter");
		}
		PageFragment fragment = getPageFragment(pageUrl);
		fragmentList.clear();
		fragmentList.add(fragment);
		notifyDataSetChanged();
	}

	private PageFragment getPageFragment(String pageUrl) {
		PageFragment fragment = null;
		if (fragmentCache.containsKey(pageUrl)) {
			fragment = fragmentCache.get(pageUrl);
		} else {
			fragment = PageFragment.build(pageUrl, this);
			fragmentCache.put(pageUrl, fragment);
		}
		return fragment;
	}

	@Override
	public int getItemPosition(Object object) {
		if (changedOrRemovedFragments.contains(object)) {
			changedOrRemovedFragments.remove(object);
			return POSITION_NONE;
		}
		return POSITION_UNCHANGED;
	}

	public int showPage(Page page) {
		Log.d(TAG, "Showing page " + page.getLink());
		int position = fragmentList.size();
		if (position == 0) {
			Log.d(TAG, "Got nothing in pager adapter, adding first fragment");
			PageFragment fragment = getPageFragment(page.getLink());
			fragmentList.add(fragment);
			return 0;
		}

		for (PageFragment p : fragmentList) {
			Page pp = p.getPage();
			if (pp != null && pp.hasSubPage(page.getLink())) {
				int index = fragmentList.indexOf(p);
				position = index + 1;
				Log.d(TAG, "Found parent at " + index);
				break;
			} else if (pp == null) {
				int index = fragmentList.indexOf(p);
				Log.w(TAG, "Found PageFragment without Page object at index "
						+ index);
			}
		}
		if (position < fragmentList.size()) {
			int size = fragmentList.size();
			for (int i = position; i < size; i++) {
				Log.d(TAG, "Removing fragment on position " + i);
				changedOrRemovedFragments.add(fragmentList.get(i));
				fragmentList.remove(i);
			}
		}
		Log.d(TAG, "Inserting new page at position " + position);
		fragmentList.add(position, getPageFragment(page.getLink()));
		notifyDataSetChanged();
		return position;
	}

	public int goOnePageDown(Page page) {
		PageFragment fragment = getPageFragment(page.getLink());
		int position = fragmentList.size();
		for (PageFragment p : fragmentList) {
			if (p.getPage() != null && p.getPage().hasSubPage(page.getLink())) {
				position = fragmentList.indexOf(p) + 1;
			}
		}
		int size = fragmentList.size();
		for (int i = position + 1; i < size; i++) {
			fragmentList.remove(i);
		}
		fragmentList.add(position, fragment);
		notifyDataSetChanged();
		return position;
	}

	public void goOnePageUp() {
		if (fragmentList.size() == 1
				&& fragmentList.get(0).getPage().getParent() != null) {
			PageFragment child = fragmentList.get(0);
			PageFragment parent = getPageFragment(child.getPage().getParent()
					.getLink());
			fragmentList.add(0, parent);
		} else if (fragmentList.size() > 1) {
			fragmentList.remove(fragmentList.size() - 1);
			notifyDataSetChanged();
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		Log.d(TAG, "onPageSelected: " + position);
		if (position < fragmentList.size() - 1) {
			goOnePageUp();
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

	@Override
	public CharSequence getPageTitle(int position) {
		return fragmentList.get(position).getPage().getTitle();
	}

}
