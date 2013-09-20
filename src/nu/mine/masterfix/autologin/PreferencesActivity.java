package nu.mine.masterfix.autologin;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String TAG = "Preferences";
	public static final String KEY_ACTIVE = "active";
	public static final String KEY_LOGIN = "login";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_PROVIDER = "provider";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		PreferenceManager.getDefaultSharedPreferences(this)
			.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		Log.d(TAG, "Key \""+key+"\" changed!");
		
		if (key.equals(KEY_ACTIVE)) {
			int state;
			if (sp.getBoolean(key, false)) {
				state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
			} else {
				state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
			}
			ComponentName name = new ComponentName(this, StateChangeReceiver.class);
			getPackageManager().setComponentEnabledSetting(name, state, PackageManager.DONT_KILL_APP);
		}
		
	}

}
