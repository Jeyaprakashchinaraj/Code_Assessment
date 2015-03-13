package com.enterprise.android.canadainfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.woozzu.android.widget.RefreshableListView;
import com.woozzu.android.widget.RefreshableListView.OnRefreshListener;

public class ContentsActivity extends ActionBarActivity {

	private RefreshableListView mListView = null;
	private ArrayList<ContentItems> mlistItems = null;
	private TextView mStatusView = null;
	private MenuItem mRefershMenuitem = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contents);

		new JsonDownloader().execute();
		mStatusView = (TextView) findViewById(R.id.status);
		mListView = (RefreshableListView) findViewById(R.id.listview);

		// Callback to refresh the list
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(RefreshableListView listView) {
				// TODO Auto-generated method stub
				new JsonDownloader().execute();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contents, menu);
		// to enable menu animation
		mRefershMenuitem = menu.findItem(R.id.action_refersh);
		if (mRefershMenuitem != null) {
			mRefershMenuitem.setActionView(R.layout.refersh_action_layout);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_refersh:
			if (mRefershMenuitem != null) {
				mRefershMenuitem.setActionView(R.layout.refersh_action_layout);
			}
			new JsonDownloader().execute();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class ContentItems {
		String title = null;
		String describtion = null;
		String imagHrf = null;
	}

	class JsonDownloader extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			if (!isNetworkAvailable()) {
				return null;
			}
			return getContents();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {

				parseResponse(result);

			} else {

				mStatusView.setText(R.string.unabletodownload);
				Log.d("json file", "invalid..." + mRefershMenuitem);
			}
			if (mRefershMenuitem != null) {
				mRefershMenuitem.setActionView(null);
			} else {

			}
			super.onPostExecute(result);
		}

		private void parseResponse(String serverresponse) {
			mlistItems = new ArrayList<>();
			try {

				JSONObject response = new JSONObject(serverresponse);

				String title = response.getString(getResources().getString(
						R.string.tag_title));

				if (title != null) {
					setTitle(title);
				}
				JSONArray rows = response.getJSONArray(getResources()
						.getString(R.string.tag_rows));

				for (int i = 0; i < rows.length(); i++) {

					JSONObject obj = rows.getJSONObject(i);
					ContentItems item = new ContentItems();
					item.title = obj.getString(getResources().getString(
							R.string.tag_title));
					item.describtion = obj.getString(getResources().getString(
							R.string.tag_desc));
					item.imagHrf = obj.getString(getResources().getString(
							R.string.tag_url));

					if (item.title != null && !item.title.equals("null")
							&& item.describtion != null
							&& !item.describtion.equals("null")) {
						mlistItems.add(item);
					}
				}

				mListView.setAdapter(new ContentAdaptor(ContentsActivity.this,
						mlistItems));
				mStatusView.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				mListView.completeRefreshing();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private String getContents() {

			InputStream is = null;
			HttpURLConnection conn = null;

			try {
				try {

					URL url = new URL(getResources()
							.getString(R.string.jsonUrl));

					conn = (HttpURLConnection) url.openConnection();
					/* time in milliseconds */
					conn.setReadTimeout(10000);
					conn.setConnectTimeout(15000);
					conn.setRequestMethod("GET");

					conn.setRequestProperty("Content-Type",
							"application/json;charset=utf-8");
					conn.setRequestProperty("X-Requested-With",
							"XMLHttpRequest");

					conn.connect();

					is = conn.getInputStream();
					return convertStreamToString(is);

				} finally {

					// is.close();
					// conn.disconnect();
				}

			} catch (UnknownHostException e1) {
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
			return null;
		}

		private String convertStreamToString(InputStream inputStream)
				throws IOException {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = "";
			String result = "";
			while ((line = bufferedReader.readLine()) != null)
				result += line;

			inputStream.close();
			return result;

		}
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwInfo = cm.getActiveNetworkInfo();
		return nwInfo != null && nwInfo.isConnectedOrConnecting();
	}

}
