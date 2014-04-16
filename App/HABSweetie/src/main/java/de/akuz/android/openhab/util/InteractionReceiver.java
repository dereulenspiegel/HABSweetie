package de.akuz.android.openhab.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.security.cert.X509Certificate;

import de.akuz.android.openhab.ui.SSLDecisionDialogFragment;
import de.duenndns.ssl.MemorizingTrustManager;

public class InteractionReceiver extends BroadcastReceiver {

	private FragmentActivity activity;

	public InteractionReceiver(FragmentActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("InteractionReceiver", "Received intent for decision");
		String app = intent
				.getStringExtra(MemorizingTrustManager.DECISION_INTENT_APP);
		X509Certificate cert = (X509Certificate) intent
				.getSerializableExtra(MemorizingTrustManager.DECISION_INTENT_CERT);
		int decisionId = intent.getIntExtra(
				MemorizingTrustManager.DECISION_INTENT_ID, 1);
		SSLDecisionDialogFragment dialog = SSLDecisionDialogFragment.build(app,
				decisionId, cert);
		dialog.show(activity.getSupportFragmentManager(), "SSLDecision");
		this.abortBroadcast();
	}

	public static InteractionReceiver registerReceiver(FragmentActivity activity) {
		InteractionReceiver receiver = new InteractionReceiver(activity);

		IntentFilter filter = new IntentFilter(
				MemorizingTrustManager.DECISION_INTENT_REQUEST);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
		Log.d("InteractionReceiver",
				"Registered with action " + filter.getAction(0));
		activity.registerReceiver(receiver, filter);
		return receiver;
	}

	public void unregister() {
		Log.d("InteractionReceiver", "Unregistering receiver");
		activity.unregisterReceiver(this);
	}

}
