package de.akuz.android.openhab.core;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;

import android.os.AsyncTask;
import de.akuz.android.openhab.core.objects.Page;

public class LoadPageTask extends AsyncTask<String, Void, Page> {
	
	private AsyncHttpClient client;

	@Override
	protected Page doInBackground(String... params) {
		return null;
	}

}
