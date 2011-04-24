package nthu.cs.EnHunter.Context;

import nthu.cs.EnHunter.Logger.Uploder.LogUploader;
import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;


public class AppContext extends Application{

	private static AppContext instance; // single instance of application context
	private static final String TAG = "EnHunter - AppContext";
	private Context activityContext;
	LogUploader logger;
	
	// Global data 
	public SensorManager sensorManager ;
	public ConnectivityManager connectivityManager;
	public WifiManager wifiManager ;
	
	public void setServiceInstances(SensorManager sm, ConnectivityManager cm, WifiManager wm) {
		this.sensorManager = sm;
		this.connectivityManager = cm;
		this.wifiManager = wm;
	}
	
	public static AppContext getInstance () {
		return instance;
	}
	
	public void setContextFromActivity(Context c) {
		this.activityContext = c; 
	}
	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		Log.d(TAG, "onCreate");
		logger = new LogUploader(activityContext);
		AppContext.instance = this;	
	}
	
	
	public LogUploader getLogger() {
		return logger;
	}
	
}
