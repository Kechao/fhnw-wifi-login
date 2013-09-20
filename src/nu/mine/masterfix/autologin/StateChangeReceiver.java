package nu.mine.masterfix.autologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class StateChangeReceiver extends BroadcastReceiver {

	public static final String TAG = "StateChangeReceiver";
	
	private static final String FHNW_SSID = "fhnw-public";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d(TAG, "AutoLogin Wifi state change!");
		
		NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

			WifiManager wifiman = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiinfo = wifiman.getConnectionInfo();
			
			if (wifiinfo.getSSID().equals(FHNW_SSID)) {
				Log.d(TAG, "FHNW Network detected, try to start LoginService!");
				Intent service = new Intent(context, LoginService.class);
				context.startService(service);
			} else {
				Log.d(TAG, "No FHNW Network detected!");
			}
			
		}

		Log.d(TAG, "StateChangeReceiver ended!");
	}

}
