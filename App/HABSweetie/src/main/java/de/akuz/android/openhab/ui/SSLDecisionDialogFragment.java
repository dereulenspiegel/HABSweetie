package de.akuz.android.openhab.ui;

import java.security.cert.X509Certificate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.akuz.android.openhab.R;
import de.duenndns.ssl.MTMDecision;
import de.duenndns.ssl.MemorizingTrustManager;

public class SSLDecisionDialogFragment extends SherlockDialogFragment implements
		OnClickListener {

	private final static String TAG = SSLDecisionDialogFragment.class
			.getSimpleName();

	private String app;
	private int decisionId;

	private TextView textViewDN;
	private TextView textViewMD5;
	private TextView textViewSHA1;
	private TextView textViewSignedBy;

	private X509Certificate chain;

	public static SSLDecisionDialogFragment build(String app, int decisionId,
			X509Certificate chain) {
		SSLDecisionDialogFragment fragment = new SSLDecisionDialogFragment();
		fragment.setCertificateChain(chain);
		Bundle args = new Bundle();
		args.putString(MemorizingTrustManager.DECISION_INTENT_APP, app);
		args.putInt(MemorizingTrustManager.DECISION_INTENT_ID, decisionId);
		fragment.setArguments(args);
		return fragment;
	}

	public void setCertificateChain(X509Certificate chain) {
		this.chain = chain;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		app = args.getString(MemorizingTrustManager.DECISION_INTENT_APP);
		decisionId = args.getInt(MemorizingTrustManager.DECISION_INTENT_ID);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View rootView = getActivity().getLayoutInflater().inflate(
				R.layout.ssl_dialog, null, false);

		X509Certificate firstCert = chain;

		textViewDN = (TextView) rootView.findViewById(R.id.textViewDN);
		textViewMD5 = (TextView) rootView.findViewById(R.id.textViewMD5);
		textViewSHA1 = (TextView) rootView.findViewById(R.id.textViewSHA1);
		textViewSignedBy = (TextView) rootView
				.findViewById(R.id.textViewSignedBy);

		textViewDN.setText(firstCert.getSubjectDN().toString());
		textViewMD5.setText(MemorizingTrustManager.certHash(firstCert, "MD5"));
		textViewSHA1.setText(MemorizingTrustManager
				.certHash(firstCert, "SHA-1"));
		textViewSignedBy.setText(firstCert.getIssuerDN().toString());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(rootView);
		builder.setTitle(R.string.dialog_title);
		builder.setNegativeButton(R.string.mtm_decision_abort, this);
		builder.setNeutralButton(R.string.mtm_decision_once, this);
		builder.setPositiveButton(R.string.mtm_decision_always, this);
		builder.setCancelable(false);

		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int buttonId) {
		switch (buttonId) {
		case DialogInterface.BUTTON_POSITIVE:
			acceptAlways();
			break;
		case DialogInterface.BUTTON_NEUTRAL:
			acceptOnce();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			deny();
			break;
		}

	}

	private void acceptOnce() {
		sendDecision(MTMDecision.DECISION_ONCE);
	}

	private void acceptAlways() {
		sendDecision(MTMDecision.DECISION_ALWAYS);
	}

	private void deny() {
		sendDecision(MTMDecision.DECISION_ABORT);
	}

	void sendDecision(int decision) {
		Log.d(TAG, "Sending decision to " + app + ": " + decision);
		Intent i = new Intent(MemorizingTrustManager.DECISION_INTENT_RESPONSE);
		i.putExtra(MemorizingTrustManager.DECISION_INTENT_ID, decisionId);
		i.putExtra(MemorizingTrustManager.DECISION_INTENT_CHOICE, decision);
		i.setPackage(getActivity().getPackageName());
		getActivity().sendBroadcast(i);
		dismiss();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		sendDecision(MTMDecision.DECISION_ABORT);
		super.onDismiss(dialog);
	}

}
