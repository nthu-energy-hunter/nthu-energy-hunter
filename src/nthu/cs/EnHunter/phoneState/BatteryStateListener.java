package nthu.cs.EnHunter.phoneState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BatteryStateListener extends BroadcastReceiver {
	private static BatteryStateListener instance;
	
	
	
	public static BatteryStateListener getInstance() {
		if(instance == null)
			instance = new BatteryStateListener();
		return instance;
	}
	
	private BatteryStateListener() {
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int rawLevel = intent.getIntExtra("level", -1);
		int scale = intent.getIntExtra("scale",-1);
		int level = -1;
		if( rawLevel >=0 && scale >0) {
			level = (rawLevel*100) / scale;
		}
		Log.d("MTK"," battery now "+ level);
	}
	
}
