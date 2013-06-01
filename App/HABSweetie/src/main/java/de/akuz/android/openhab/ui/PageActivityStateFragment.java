package de.akuz.android.openhab.ui;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PageActivityStateFragment extends Fragment {

	public final static String TAG = PageActivityStateFragment.class
			.getSimpleName();

	private List<PageFragment> availablePageFragments;
	
	private Set<PageFragment> removedFragments;
	
	private Map<String,PageFragment> fragmentCache;

	private int currentViewPagerPage;
	
	private String baseUrl;
	
	private boolean hasState = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public List<PageFragment> getAvailablePageFragments() {
		return availablePageFragments;
	}

	public void setAvailablePageFragments(
			List<PageFragment> availablePageFragments) {
		this.availablePageFragments = availablePageFragments;
	}

	public int getCurrentViewPagerPage() {
		return currentViewPagerPage;
	}

	public void setCurrentViewPagerPage(int currentViewPagerPage) {
		this.currentViewPagerPage = currentViewPagerPage;
	}

	public Set<PageFragment> getRemovedFragments() {
		return removedFragments;
	}

	public void setRemovedFragments(Set<PageFragment> removedFragments) {
		this.removedFragments = removedFragments;
	}

	public Map<String, PageFragment> getFragmentCache() {
		return fragmentCache;
	}

	public void setFragmentCache(Map<String, PageFragment> fragmentCache) {
		this.fragmentCache = fragmentCache;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public boolean isHasState() {
		return hasState;
	}

	public void setHasState(boolean hasState) {
		this.hasState = hasState;
	}

}
