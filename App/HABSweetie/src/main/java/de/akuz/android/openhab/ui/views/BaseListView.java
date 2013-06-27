package de.akuz.android.openhab.ui.views;

import de.akuz.android.openhab.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public abstract class BaseListView<O> extends LinearLayout {

	private View rootView;

	protected O object;

	public BaseListView(Context context) {
		super(context);
		setBackgroundResource(R.color.transparent);
		buildUi();
		addView(rootView);
	}

	protected void setLayout(int id) {
		rootView = LayoutInflater.from(getContext()).inflate(id, this, false);
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T findView(int id) {
		return (T) rootView.findViewById(id);
	}

	public void updateObject(O object) {
		this.object = object;
		objectUpdated(object);
	}

	protected abstract void buildUi();

	protected abstract void objectUpdated(O object);

	public O getObject() {
		return object;
	}

}
