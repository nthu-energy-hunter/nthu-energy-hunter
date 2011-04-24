package nthu.cs.EnHunter.perProcess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;
import android.util.SparseArray;
import android.content.*;


public class ProcessCrowler {
	private static ProcessCrowler instance;


	  static class UidToDetail {
	        String name;
	        String packageName;
	        Drawable icon;
	    }
	
	private static final String TAG = "MTK";
	private static final String BATTERY_STATS_IMPL_CLASS = "com.android.internal.os.BatteryStatsImpl";
	private static final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
	private static final String M_BATTERY_INFO_CLASS = "com.android.internal.app.IBatteryStats";
	private static final String BATTERY_STATS_CLASS = "android.os.BatteryStats";
	private static final String UID_CLASS = BATTERY_STATS_CLASS + "$Uid";
	private static final String PROC_CLASS = UID_CLASS + "$Proc";
	private static final String SENSOR_CLASS = UID_CLASS + "$Sensor";
	private static final String BATTER_STATS_TIMER_CLASS = BATTERY_STATS_CLASS + "$Timer";

	
	

	private Object mBatteryInfo_;
	private Object mStats_;
	private Object mPowerProfile_;
	private int mStatsType_ = BatteryStatsConstants.STATS_TOTAL;
	//private int mStatsType_ = BatteryStatsConstants.STATS_UNPLUGGED;
	private Activity parent_;
	int userAppCount = 0;
	
	List<String> currentPkg = new ArrayList<String>();
	////
	
	public ProcessCrowler () {
		
		try {
			mBatteryInfo_ = Class.forName("com.android.internal.app.IBatteryStats$Stub").getDeclaredMethod("asInterface", 
				IBinder.class).invoke(null, Class.forName("android.os.ServiceManager")
						.getMethod("getService", String.class).invoke(null, "batteryinfo"));
		
			load(); 
			instance = this;
		}catch(Exception e) {
			
		}
	}
	
	public static ProcessCrowler getInstance() {
		
		if (instance == null) {
			instance = new ProcessCrowler();
			Log.d("MTK","new a crowler instance");
		}
		Log.d("MTK","get crowler Instance");
		return instance;
	}
	
	private void load(){
		try{
			/*
			 *  return com.android.os.BattertStatsImpl object... it contains lots of things
			 *  transform the object into byte array for IPC
			 * */
			byte[] data = (byte[])Class.forName(M_BATTERY_INFO_CLASS).getMethod("getStatistics", null)
			.invoke(mBatteryInfo_, null);
			
			Parcel parcel = Parcel.obtain();
			parcel.unmarshall(data, 0, data.length);
			parcel.setDataPosition(0);
			/*
			 * Non-reflection call looks like
			 * mStats = com.android.internal.os.BatteryStatsImpl.CREATOR.createFromParcel(parcel);
			 */
			mStats_ = Class.forName(BATTERY_STATS_IMPL_CLASS)
			.getField("CREATOR").getType().getMethod("createFromParcel", Parcel.class)
			.invoke(
					Class.forName(BATTERY_STATS_IMPL_CLASS).getField("CREATOR").get(null), parcel);

		}catch(InvocationTargetException e){
			Log.e("BatteryTester", "Exception: " + e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	public List<String> getCurrentPkgList() {
		return currentPkg;
	}
	public int getCurrentBackorundProcessNum() {
		try {
			
			mBatteryInfo_ = Class.forName("com.android.internal.app.IBatteryStats$Stub").getDeclaredMethod("asInterface", 
					IBinder.class).invoke(null, Class.forName("android.os.ServiceManager")
							.getMethod("getService", String.class).invoke(null, "batteryinfo"));
			
			load(); 
				
			userAppCount = 0;
			currentPkg.clear();
		//	load();
			SparseArray uidStats = (SparseArray)Class.forName(BATTERY_STATS_IMPL_CLASS).getMethod("getUidStats", null).invoke(mStats_, null);
			final int which = mStatsType_;
			final int NU = uidStats.size();
			Log.d("Hunter","crowler -> size = "+NU);
			for (int iu = 0; iu < NU; iu++){
				
				Object u = uidStats.valueAt(iu);
			
				Map<String, Object> processStats = 
					(Map)Class.forName(UID_CLASS).getMethod("getProcessStats", (Class[])null).invoke(u, null);
				
				int uid;
				uid = uidStats.keyAt(iu);
				
				if(uid<10000) // we do not care about system process
					continue;
			
				if(processStats.size() >0) // to investigate the user app only
				{
					//Log.d("Hunter","dsdsd");
					userAppCount++;
					
					for (Map.Entry<String, Object> ent: processStats.entrySet()){ 
						
						currentPkg.add(ent.getKey());	
					}
					
				}
			}
			//Log.d("MTK","#bg = "+ userAppCount);
			//return userAppCount;
			return userAppCount;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return userAppCount;
	}
	public void getPerProcessUsage () {
		try {
			mBatteryInfo_ = Class.forName("com.android.internal.app.IBatteryStats$Stub").getDeclaredMethod("asInterface", 
					IBinder.class).invoke(null, Class.forName("android.os.ServiceManager")
							.getMethod("getService", String.class).invoke(null, "batteryinfo"));
			load(); // obtains the mstat object (containing the info of BatteryStatsImpl)
		
			// the uidStats array instance contains BatteryStatsImpl.Uid objects 
			// (BatteryStatsImpl.Uid is an inner class in BatteryStatsImpl)
			SparseArray uidStats = (SparseArray)Class.forName(BATTERY_STATS_IMPL_CLASS).getMethod("getUidStats", null).invoke(mStats_, null);
			final int which = mStatsType_;
			final int NU = uidStats.size();
		
			
			int userAppCount = 0;
			
			long totalUserTime = 0;
			long totalSysTime = 0;
			long totalFgTime = 0;
			
			int uid;
			
			// Process CPU time
			for (int iu = 0; iu < NU; iu++){
				
				Object u = uidStats.valueAt(iu);
			
				Map<String, Object> processStats = 
					(Map)Class.forName(UID_CLASS).getMethod("getProcessStats", (Class[])null).invoke(u, null);
				
				uid = uidStats.keyAt(iu);
				
				if(uid<10000) // we do not care about system process
					continue;
				
				totalUserTime = 0;
				totalSysTime = 0;
				totalFgTime = 0;
				
				String mainAppName =null;
				
				if(processStats.size() >0) // to investigate the user app only
				{
					userAppCount++;
					
					for (Map.Entry<String, Object> ent: processStats.entrySet()){
				
						Object ps = ent.getValue();
						
						// Returns the total time (in 1/100 sec) spent executing in user code.
						final long userTime = (Long)Class.forName(PROC_CLASS).getMethod("getUserTime", java.lang.Integer.TYPE).invoke(ps, which);
						// Returns the total time (in 1/100 sec) spent executing in system code.
						final long systemTime = (Long)Class.forName(PROC_CLASS).getMethod("getSystemTime", java.lang.Integer.TYPE).invoke(ps, which);
						// Returns the cpu time spent in microseconds while the process was in the foreground.
						final long foregroundTime = (Long)Class.forName(PROC_CLASS).getMethod("getForegroundTime", java.lang.Integer.TYPE).invoke(ps, which);
						mainAppName = ent.getKey();
					//	Log.d("MTK",ent.getKey()+ " uTime = "+ userTime + " sTime = "+ systemTime);
						totalUserTime += userTime;
						totalSysTime += systemTime;
						totalFgTime += foregroundTime;
					} // end for
				} // end if
				
				// Calculate network usage
				double edge_recvTcp = (Long)Class.forName(UID_CLASS).getMethod("getTcpBytesReceived", java.lang.Integer.TYPE).invoke(u, mStatsType_);
				double edge_transTcp = (Long)Class.forName(UID_CLASS).getMethod("getTcpBytesSent",java.lang.Integer.TYPE).invoke(u, mStatsType_);
				double recvTcp = android.net.TrafficStats.getUidRxBytes(uid);
				double transTcp = android.net.TrafficStats.getUidTxBytes(uid);
				
			/*	Log.d("-PowerHunter-","#"+userAppCount+" uid:"+uid+" app:"+mainAppName +" sysTime="+totalUserTime + " userTime="+totalSysTime +
						" fgTime="+totalFgTime + " transTcp="+edge_transTcp+" recvTcp="+edge_recvTcp 
						+" wifi_Trans="+recvTcp+ " wifi_Recv="+transTcp);
				*/
				//Object m = Class.forName(BATTERY_STATS_IMPL_CLASS+"$Uid").cast(u);
				
			//	if(mainAppName.contentEquals("com.facebook.katana")) {
				if(mainAppName!=null&&mainAppName.contains("facebook")){
				Log.d("MTK",mainAppName+" "+
						Class.forName(BATTERY_STATS_IMPL_CLASS+"$Uid").getMethod("computeCurrentTcpBytesReceived").invoke(u, null)+
						" / "+Class.forName(BATTERY_STATS_IMPL_CLASS+"$Uid").getMethod("computeCurrentTcpBytesSent").invoke(u, null)
				+" "+recvTcp +" / "+ transTcp		+ " " + edge_recvTcp + " / "+ edge_transTcp
				
				);
				}
				//}
				/*for(int i=0 ; i<m.length;i++) {
					if(m[i].toString().contains("Tcp")){
						Log.d("MTK",m[i].toString());
					}
				}*/
			//	Log.d("-PowerHunter-","#"+userAppCount+" uid:"+uid+" app:"+mainAppName+" "+
						//(Long)Class.forName(UID_CLASS).getMethod("computeCurrentTcpBytesReceived", java.lang.Long.TYPE).invoke(u,null)
						//+(Long)Class.forName(UID_CLASS).getMethods());
						//getMethod("computeCurrentTcpBytesSent").invoke(u));
			} // end for
			
			
		}catch(Exception e) {
			e.printStackTrace();
			Log.d("MTK_ERRROR",e.getMessage());
		}	
		
	}
	
	private class BatteryStatsConstants{
		private static final boolean LOCAL_LOGV = false;

		/**
		 * A constant indicating a partial wake lock timer.
		 */
		public static final int WAKE_TYPE_PARTIAL = 0;

		/**
		 * A constant indicating a full wake lock timer.
		 */
		public static final int WAKE_TYPE_FULL = 1;

		/**
		 * A constant indicating a window wake lock timer.
		 */
		public static final int WAKE_TYPE_WINDOW = 2;

		/**
		 * A constant indicating a sensor timer.
		 * 
		 * {@hide}
		 */
		public static final int SENSOR = 3;

		/**
		 * A constant indicating a a wifi turn on timer
		 *
		 * {@hide}
		 */
		public static final int WIFI_TURNED_ON = 4;

		/**
		 * A constant indicating a full wifi lock timer
		 *
		 * {@hide}
		 */
		public static final int FULL_WIFI_LOCK = 5;

		/**
		 * A constant indicating a scan wifi lock timer
		 *
		 * {@hide}
		 */
		public static final int SCAN_WIFI_LOCK = 6;

		/**
		 * A constant indicating a wifi multicast timer
		 *
		 * {@hide}
		 */
		public static final int WIFI_MULTICAST_ENABLED = 7;

		/**
		 * A constant indicating an audio turn on timer
		 *
		 * {@hide}
		 */
		public static final int AUDIO_TURNED_ON = 7;

		/**
		 * A constant indicating a video turn on timer
		 *
		 * {@hide}
		 */
		public static final int VIDEO_TURNED_ON = 8;

		/**
		 * Include all of the data in the stats, including previously saved data.
		 */
		public static final int STATS_TOTAL = 0;

		/**
		 * Include only the last run in the stats.
		 */
		public static final int STATS_LAST = 1;

		/**
		 * Include only the current run in the stats.
		 */
		public static final int STATS_CURRENT = 2;

		/**
		 * Include only the run since the last time the device was unplugged in the stats.
		 */
		public static final int STATS_UNPLUGGED = 3;

	}
	
}
