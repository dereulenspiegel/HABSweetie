package de.akuz.android.openhab.ui;

import java.text.MessageFormat;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dagger.ObjectGraph;
import de.akuz.android.openhab.R;
import de.akuz.android.openhab.core.CommunicationModule;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public abstract class BaseFragment extends Fragment {

	private LayoutInflater inflater;
	private View rootView;
	private ViewGroup container;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
		((BaseActivity) getActivity()).inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		buildUi();
		return rootView;
	}

	protected void setView(int resId) {
		rootView = inflater.inflate(resId, container, false);
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T findView(int resId) {
		return (T) rootView.findViewById(resId);
	}

	protected abstract void buildUi();

	public void handleException(Throwable t) {
		Log.e("HABSweetie", "D'oh", t);
		makeCrouton(R.string.error_generic, Style.ALERT, t.getMessage());
	}

	protected String getPreferenceStringValue(int resId) {
		return PreferenceManager.getDefaultSharedPreferences(getActivity())
				.getString(getString(resId), null);
	}

	protected void makeCrouton(int resId, Style style, Object... params) {
		if (isAdded()) {
			String message = getString(resId);
			if (params != null && params.length > 0) {
				message = MessageFormat.format(message, params);
			}
			Crouton.makeText(this.getActivity(), message, style).show();
		}
	}

	public void inject(Object o) {
		((BaseActivity) getActivity()).inject(o);
	}

}
