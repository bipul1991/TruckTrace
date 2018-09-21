package com.example.bipul.truckapp.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtlities
{
	private static NetworkInfo networkInfo;
	public static final String noNetworkConnectionMessage = "Please Check Internet Connection";

	public static boolean isConnected(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		try
		{
			networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// test for connection for WIFI
		if (networkInfo != null
		        && networkInfo.isAvailable()
		        && networkInfo.isConnected()) { return true; }

		networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// test for connection for Mobile
		if (networkInfo != null
		        && networkInfo.isAvailable()
		        && networkInfo.isConnected()) { return true; }

		return false;
	}
}
