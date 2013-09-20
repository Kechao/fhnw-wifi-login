package nu.mine.masterfix.autologin;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class LoginService extends IntentService {

	public static final String TAG = "LoginService";
	
	private static final String FHNW_LOGIN_URL = "https://mpp.ict.fhnw.ch/login";
	private static final String FORM_LOGIN = "login";
	private static final String FORM_PASSWORD = "password";
	private static final String FORM_PROVIDER = "provider";
	
	private Context context;
	private Handler handler;
	
	public LoginService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Service started!");
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		String login = prefs.getString(PreferencesActivity.KEY_LOGIN, "");
        String password = prefs.getString(PreferencesActivity.KEY_PASSWORD, "");
        String provider = prefs.getString(PreferencesActivity.KEY_PROVIDER, "");
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(FHNW_LOGIN_URL);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
		pairs.add(new BasicNameValuePair(FORM_LOGIN, login));
		pairs.add(new BasicNameValuePair(FORM_PASSWORD, password));
		pairs.add(new BasicNameValuePair(FORM_PROVIDER, provider));	
		
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = client.execute(post);
			String msg;
			if (response.getStatusLine().getStatusCode() == 200) {
				if (response.getFirstHeader("Age") != null) {
					msg = context.getString(R.string.login_successful);
				} else {
					msg = context.getString(R.string.login_auth_failed);
				}
			} else {
				msg = context.getString(R.string.login_failed);
			}
			Log.v(TAG, msg);
			handler.post(new DisplayToast(msg));
		} catch (Exception e) {
			Log.e(TAG, "Error during Login HTTP post!");
		}
		
		Log.d(TAG, "Service stopped!");
	}
	
	private class DisplayToast implements Runnable {
		
		private String text;
		
		public DisplayToast(String text) {
			this.text = text;
		}

		@Override
		public void run() {
			int duration;
			if (text.length() < 25) {
				duration = Toast.LENGTH_SHORT;
			} else {
				duration = Toast.LENGTH_LONG;
			}
			Toast.makeText(context, text, duration).show();
		}

	}

}
