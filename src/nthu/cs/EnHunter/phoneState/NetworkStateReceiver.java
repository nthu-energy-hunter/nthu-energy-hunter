package nthu.cs.EnHunter.phoneState;

import java.util.List;

import nthu.cs.EnHunter.Context.AppContext;

import com.example.hellojni.HelloJni;
import com.example.hellojni.SamplingService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkStateReceiver extends BroadcastReceiver {
	
	ConnectivityManager connectivityManager;

	NetworkInfo activeNetInfo;

	
	public String activeNet = "";
	
	public NetworkStateReceiver() {		
		Log.d("-NetworkStateReceiver-","ctor!");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("-NetworkStateReceiver-","onReceive");
		
		connectivityManager = AppContext.getInstance().connectivityManager;
		
		/*
		connectivityManager = HelloJni.getInstance().connectivityManager;
		WifiManager wm = HelloJni.getInstance().wifiManager;
		 */
		
		
		/*	wm.startScan();
		List<ScanResult> r = wm.getScanResults();
		if(r!=null) {
			for(int i = 0 ; i<r.size();i++)
			{
				Log.d("MTK","#"+i+ " "+r.get(i).level);
			
			}
			activeNetInfo = connectivityManager.getActiveNetworkInfo();
		}*/
		activeNetInfo = connectivityManager.getActiveNetworkInfo();
		
		if (activeNetInfo != null)
			activeNet = activeNetInfo.getState().toString() + " "+ activeNetInfo.getTypeName();
		else
			activeNet = "NO_NETWORKED";

		SamplingService.getInstance().netStat = activeNet;

	}
}
