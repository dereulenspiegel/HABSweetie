package de.akuz.android.openhab.core;

import org.atmosphere.wasync.Function;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class AbstractFunction<DATA> implements Function<DATA> {

	private final static String TAG = AbstractFunction.class.getSimpleName();

	private Handler mainHandler;

	public AbstractFunction() {
		mainHandler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				onMessage((DATA) msg.obj);
			}
		};
	}

	@Override
	public void on(DATA t) {
		Log.d(TAG, "Received message in connection thread: ");
		Message mes = mainHandler.obtainMessage();
		mes.obj = t;
		mainHandler.sendMessage(mes);
	}

	public abstract void onMessage(DATA t);

}
