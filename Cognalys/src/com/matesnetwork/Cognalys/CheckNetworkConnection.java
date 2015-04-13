package com.matesnetwork.Cognalys;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetworkConnection {

	// test urls

	public String hosturl = "http://test.foodiesbuddy.com/mobile/pizza3/getdetails.php?";
	public String thumbimagehosturl = "http://test.foodiesbuddy.com/upload/images/thumbs/thumb_";
	public String normalimagehosturl = "http://test.foodiesbuddy.com/";

	
	public static boolean isConnectionAvailable(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()
					&& netInfo.isConnectedOrConnecting()
					&& netInfo.isAvailable()) {
				return true;
			}
		}
		return false;
	}
}
