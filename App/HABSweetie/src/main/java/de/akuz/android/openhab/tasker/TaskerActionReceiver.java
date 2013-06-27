package de.akuz.android.openhab.tasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TaskerActionReceiver extends BroadcastReceiver {

	private final static String TAG = TaskerActionReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent
				.getAction())) {
			Intent i = new Intent(context, TaskerActionService.class);
			i.putExtras(i.getExtras());
			context.startService(i);
		} else {
			Log.w(TAG, "Received unkown/invalid intent " + intent.getAction());
		}

	}
}
