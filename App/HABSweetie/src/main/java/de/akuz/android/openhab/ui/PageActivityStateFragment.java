package de.akuz.android.openhab.ui;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PageActivityStateFragment extends Fragment {

	public final static String TAG = PageActivityStateFragment.class
			.getSimpleName();

	private List<PageFragment> availablePageFragments;

	private int currentViewPagerPage;

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

}
