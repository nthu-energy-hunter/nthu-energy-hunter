package com.example.hellojni;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import nthu.cs.EnHunter.Logger.Uploder.LogUploader;
import nthu.cs.EnHunter.perProcess.ProcessCrowler;
import nthu.cs.EnHunter.phoneState.NetworkStateReceiver;
import nthu.cs.EnHunter.phoneState.PhoneStatusListener;
import nthu.cs.EnHunter.phoneState.SensorReader;
import nthu.cs.EnHunter.Context.*;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import android.os.Process.*;

import com.android.internal.util.*;

public class SamplingService extends Service {



	private Handler handler = new Handler();

	private ActivityManager am;
	private List<ActivityManager.RunningTaskInfo> taskInfo;

	private double delta;
	private double startTime;
	private double endTime;
	private double lastTime;

	private double prevRecvBytes;
	private double prevTransBytes;
	private double recvBytes;
	private double transBytes;
	private double uplinkRate;
	private double downlinkRate;

	private double edgeRecbBytes;
	private double edgeTransBytes;
	private double prevEdgeRecvBytes;
	private double prevEdgeTransBytes;
	private double edgeUplinkRate;
	private double edgeDownlikRate;

	public static final int PROC_SPACE_TERM = (int) ' ';
	public static final int PROC_TAB_TERM = (int) '\t';
	public static final int PROC_COMBINE = 0x100;
	public static final int PROC_OUT_LONG = 0x2000;

	public static String WIFI_DEV_NAME;
	public static String EDGE_DEV_NAME;
	private boolean have_wifi = false;
	private boolean have_edge = false;

	public enum DEV {
		NEXUS, GOLDFISH
	}

	public static String SEPARATOR =" ";
	//public static DEV CURRENT_DEV = DEV.NEXUS;
	public static DEV CURRENT_DEV = DEV.GOLDFISH;

	BufferedWriter sdCardWriter;

	
	private final long[] mSystemCpuData = new long[7];

	final long[] sysCpu = mSystemCpuData;

	String buffer[];
	int bufferIndex = 0;
	final static int flushThreashold = 100; 


	private long[] previousCPUutil;
	public static SamplingService instance;
	String test="";
	
	LogUploader logger;
	
	public static SamplingService getInstance() {
		if(instance ==null)
			instance =new SamplingService();
		return instance;
	}
	
	public SamplingService() {
		
		
	
		
		Log.d("MTK", "SamplingService ctor");

		if (CURRENT_DEV == DEV.NEXUS) {
			WIFI_DEV_NAME = "eth0:";
			EDGE_DEV_NAME = "rmnet0:";
		} else {
			WIFI_DEV_NAME = "eth0:";
			EDGE_DEV_NAME = "none";
		}

		previousCPUutil  = new long[7];
		initLogger();

		initNetStat();
		//SM = (SensorManager) getSystemService(SENSOR_SERVICE);
	//	initSensorManager();

		initNetRecv();
		instance = this;
		
		String batteryLevel = "no data";
		
	
		
		
	}
	
	
	
	NetworkStateReceiver netRecv;

	public void initNetRecv() {
		//ConnectivityManager connectivityManager = HelloJni.getInstance().connectivityManager;
		
		
		try {
			
			NetworkInfo activeNetInfo = AppContext.getInstance().connectivityManager
			.getActiveNetworkInfo();
		
			if (activeNetInfo != null)
				this.netStat = activeNetInfo.getState().toString() + " "
						+ activeNetInfo.getTypeName();
			else
				this.netStat = "no connection avail.";
		
		
		} catch( Exception e) {
			
			
		}	
				
	}

	public void initSensorManager() {
		
		SensorReader.getInstance().register();
		
	}
	
    void createNoti() {
    	int icon = R.drawable.icon;
    	String tickerText = "EnHunter";
    	long when = System.currentTimeMillis();
    	Notification noti = new Notification(icon, tickerText, when);
    	
    	String expandText = "EnHunter is hunting!";
    	String expandTitle = "EnHunter...";
    	//Intent intent  = new Intent(this,ListViewActivity.classcom.example.hellojni.ListViewActivity);
    	Intent intent  = new Intent("com.example.hellojni.ListViewActivity");
        
    	PendingIntent launchIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
    	noti.setLatestEventInfo(getApplicationContext(), expandTitle, expandText, launchIntent);
    
    	String ns = Context.NOTIFICATION_SERVICE;

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.notify(1, noti);
    }

	public void initLogger() {
		try {
			
		//	 HelloJni.getInstance().createNoti();
			
			Log.d("MTK","Build Info : "+ Build.MANUFACTURER + " "
					+Build.MODEL);
			String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";


			
			buffer = new String[flushThreashold];
			
			String [] source = readCpuInfo().split("cpu")[1].split("[\\s]+|\\t");

			
			for(int i=0 ; i<7;i++) {
				this.previousCPUutil[i] = Long.parseLong(source[i+1]);
				
			}
			
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				File file = new File(root, CONFIG.logFile);
				FileWriter writer = new FileWriter(file);
				sdCardWriter = new BufferedWriter(writer);
				/*
				 * out.write("Hello world"); out.close();
				 */
			}
		} catch (IOException e) {
			Log.d("MTK", "Could not write file " + e.getMessage());
		}

	}

	public void initNetStat() {
		Log.d("MTK", readWifiStatus());
		String src = readWifiStatus();

		if (src.contains(WIFI_DEV_NAME))
			this.have_wifi = true;
		if (src.contains(EDGE_DEV_NAME))
			this.have_edge = true;

		String[] wifiSource = null, edgeSource = null;
		if (have_wifi) {
			wifiSource = readWifiStatus().split(WIFI_DEV_NAME)[1]
					.split("\\t|\\n")[0].split("[\\s]+|\\t");
			prevRecvBytes = Double.parseDouble(wifiSource[1]);
			prevTransBytes = Double.parseDouble(wifiSource[9]);
		}
		if (have_edge) {
			edgeSource = readWifiStatus().split(EDGE_DEV_NAME)[1]
					.split("\\t|\\n")[0].split("[\\s]+|\\t");
			prevEdgeRecvBytes = Double.parseDouble(edgeSource[1]);
			prevEdgeTransBytes = Double.parseDouble(edgeSource[9]);

		}

		Log.d("MTK", "Detailed net states");
		DetailedState[] netStates = android.net.NetworkInfo.DetailedState
				.values();
		for (int i = 0; i < netStates.length; i++)
			Log.d("MTK", netStates[i].name());

		Log.d("MTK", android.net.wifi.WifiInfo.LINK_SPEED_UNITS);
		// Log.d("MTK",android.os.Debug.);
		Log.d("MTK",
				"total trans bytes  = "
						+ android.net.TrafficStats.getTotalTxBytes());
		Log.d("MTK",
				"total mobile trans bytes  = "
						+ android.net.TrafficStats.getMobileTxBytes());

	}

	public String getForegroundTask() {
		this.am = (ActivityManager) SamplingService.this
		.getSystemService(ACTIVITY_SERVICE);
		taskInfo = am.getRunningTasks(1);
		// Log.d("MTK","name="+taskInfo.get(0).topActivity.getClassName()+
		// " pid="+android.os.Process.myPid());
		// Log.d("MTK","description"+taskInfo.get(0).description);

		return taskInfo.get(0).topActivity.getClassName();
		/*
		 * Log.d("MTK","name="+task.topActivity.getClassName());
		 * Log.d("MTK","id="+task.id);
		 * Log.d("MTK","description"+task.description);
		 */

	}

	private String parseDiskData() {
		String[] source = readDiskStatus().split("mmcblk0");
		Log.d("MTK", "disk data len = " + source.length);
		for (int i = 1; i < source.length; i++)
			Log.d("MTK", "i=" + i + " :" + source[i]);
		return null;

	}
	
	private void logSample(String log) {
		try {
			//ProcessCrowler.getInstance().getPerProcessUsage ();
			logger.eventPush(log);
			sdCardWriter.write(log);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void logError(String err) {
		logSample(err);
	}
	
	String powerState="uninit";

	int batteryLevel = 0;
	public String netStat="uninit"; // NetworkStateReciver will update this variable
	private Runnable hardwareSampling = new Runnable() {
		public void run() {

			handler.postDelayed(this, CONFIG.sampleRate*5);
			try {
				String log = null;
				//  #bg ,fg , cpu , phoneState ,battertEvent, BatteryLeft, netStat , netTraffoc,
				//  
				int bg  = ProcessCrowler.getInstance().getCurrentBackorundProcessNum();
				String fgApp = getForegroundTask();
				String load = parseCpuData();
				String phoneState = PhoneStatusListener.getInstance().getPhoneStatus();
				
				String powerstate = powerState;
					//HelloJni.getInstance().getPowerState();
				int batterylevel = batteryLevel;
					//HelloJni.getInstance().getBatteryLevel();
			
				String netState = netStat;
				long txBytes = android.net.TrafficStats.getTotalTxBytes();
				long rxBytes = android.net.TrafficStats.getTotalRxBytes();
				Calendar now = Calendar.getInstance();
				
				int hour = now.get(Calendar.HOUR_OF_DAY);
				int min = now.get(Calendar.MINUTE);
				int sec = now.get(Calendar.SECOND);
				
				
				log = hour+":"+min+":"+sec+" "+bg+" "+fgApp+ " "+load+ " "+phoneState+ " "+powerState+" "
				+batteryLevel+" "+netState+ " "+txBytes+" "+rxBytes+"\n";
				
				logSample(log);
				
				Log.d("Hunter",log);
						
				
				
			
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logError(e.getMessage());
			} 
	
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void registBatteryEvent() {
		try {
			BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					
					String str = intent.getAction();
					
					if(str.equals(Intent.ACTION_BATTERY_CHANGED)){
						Log.d("MTK","@@@@@@@@@@@@@@@");
						Log.d("MTK",str+" "+ intent.getExtras());
						powerState = "PLUGGED";
					}
					if (str.equals(Intent.ACTION_POWER_DISCONNECTED)){
						Log.d("MTK","@@@@@@@@@@@@@@@");

						Context c = getApplicationContext();
						CharSequence text = "ACTION_POWER_DISCONNECTED";
						int duration = Toast.LENGTH_SHORT;
						 powerState = "UNPLUGGED";
						Toast toast = Toast.makeText(c, text, duration);
						toast.show();
					}
					if (str.equals(Intent.ACTION_POWER_CONNECTED)) {
						Log.d("MTK","@A__________A@@@@@@@@@@@@@");
						Context c = getApplicationContext();
						CharSequence text = "ACTION_POWER_CONNECTED";
						int duration = Toast.LENGTH_SHORT;
						powerState = "PLUGGED";
						Toast toast = Toast.makeText(c, text, duration);
						toast.show();
					}
					else {
					
						
						int rawLevel = intent.getIntExtra("level", -1);
						int scale = intent.getIntExtra("scale",-1);
						//int level = -1;
						if( rawLevel >=0 && scale >0) {
							batteryLevel = (rawLevel*100) / scale;
						}
						//Log.d("MTK"," battery now "+ level);
					}
				}
				
			};
			
		Intent batteryIntent = registerReceiver(
				new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						// TODO Auto-generated method stub
						powerState = intent.getExtras().toString();
					}
					
				}
				
				,new IntentFilter(Intent.ACTION_POWER_CONNECTED));
			if(batteryIntent != null){
				powerState = batteryIntent.getExtras().toString();
				//Log.d("MTK","battey level = "+					String.valueOf(batteryIntent.getExtras()));
			}
	//		sendStickyBroadcast(batteryIntent);
			
			
			IntentFilter batteryLevelFilter = new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED);
			batteryLevelFilter.addAction(Intent.ACTION_POWER_CONNECTED);
			batteryLevelFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
			batteryLevelFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
			
			registerReceiver(batteryLevelReceiver,batteryLevelFilter);
			
		}catch(Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("MTK", "SamplingService onStart");
//		AppContext c = (AppContext) this.getApplication();
		AppContext c  = AppContext.getInstance();
		//AppContext c =(AppContext)getApplicationContext(); 
		Log.d("MTK",c.toString());
		logger = c.getLogger();
		startForeground(0, null);	
		registBatteryEvent();
		handler.postDelayed(hardwareSampling, CONFIG.sampleRate);
		super.onStart(intent, startId);
	}


	
	@Override
	public void onDestroy() {
		handler.removeCallbacks(hardwareSampling);
		if(SensorReader.getInstance()!=null)
			SensorReader.getInstance().unregister();
		Log.d("MTK", "profiler on destroy!");
		try {
			sdCardWriter.close();
		} catch (IOException e) {
			logError(e.getMessage());
			Log.d("MTK", "Could not write file " + e.getMessage());
		}

		super.onDestroy();
	}
	
	
	
	public String parseCpuData() {
		String [] source=null;
		try {
		source = readCpuInfo().split("cpu")[1].split("[\\s]+|\\t");
		
		}catch(Exception e) {
			logError(e.getMessage());
		}
		long totalCpuTime = 0;
		long temp , idle;
		idle = Long.parseLong(source[4]) - previousCPUutil[3];
		
		for(int i=0 ; i<7;i++) {
			temp  = Long.parseLong(source[i+1]);
			totalCpuTime += (temp - this.previousCPUutil[i]);
			this.previousCPUutil[i] = temp;
			
		}
		
		return Double.toString(((double)totalCpuTime - (double)idle)/(double)totalCpuTime);

	}

	public native String readBatteryCapacity();

	public native String readBatteryVolatage();

	public native String readDiskStatus();

	public native String readCpuInfo();

	public native String readWifiStatus();

	public native String readCpuFreq();

	public native String readScreenLevel();

	public String readPhoneCallStatus() {
		return null;

	}

	
	
	
	
	

	

	
	/*
	 * this is used to load the 'hello-jni' library on application startup. The
	 * library has already been unpacked into
	 * /data/data/com.example.HelloJni/lib/libhello-jni.so at installation time
	 * by the package manager.
	 */
	static {
		System.loadLibrary("hello-jni");
	}

	/*
	     private double getAverageDataCost() {
        final long WIFI_BPS = 1000000; // TODO: Extract average bit rates from system 
        final long MOBILE_BPS = 200000; // TODO: Extract average bit rates from system
        final double WIFI_POWER = mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ACTIVE)
                / 3600;
        final double MOBILE_POWER = mPowerProfile.getAveragePower(PowerProfile.POWER_RADIO_ACTIVE)
                / 3600;
        final long mobileData = mStats.getMobileTcpBytesReceived(mStatsType) +
                mStats.getMobileTcpBytesSent(mStatsType);
        final long wifiData = mStats.getTotalTcpBytesReceived(mStatsType) +
                mStats.getTotalTcpBytesSent(mStatsType) - mobileData;
        final long radioDataUptimeMs = mStats.getRadioDataUptime() / 1000;
        final long mobileBps = radioDataUptimeMs != 0
                ? mobileData * 8 * 1000 / radioDataUptimeMs
                : MOBILE_BPS;

        double mobileCostPerByte = MOBILE_POWER / (mobileBps / 8);
        double wifiCostPerByte = WIFI_POWER / (WIFI_BPS / 8);
        if (wifiData + mobileData != 0) {
            return (mobileCostPerByte * mobileData + wifiCostPerByte * wifiData)
                    / (mobileData + wifiData);
        } else {
            return 0;
        }
    }
	 *
	 *
	 */
}
