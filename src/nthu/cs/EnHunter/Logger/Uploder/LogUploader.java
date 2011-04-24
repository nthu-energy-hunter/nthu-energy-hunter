package nthu.cs.EnHunter.Logger.Uploder;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nthu.cs.EnHunter.Logger.Network.HTTPRequestHelper;
import nthu.cs.EnHunter.Logger.Uploder.State.*;

import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

public class LogUploader {
	
	private ILoggerState state;
	private ILoggerState persistAllState;
	private ILoggerState persistAndSendState;
	private ILoggerState bulkSendState;
	
	private Context mContext;
	private Handler mHandler;
	private Timer mTimer;
	
	
	private String mDevId;
	
	private static final String TAG = "Enhunter - LogUploader";
	
	private static final int FLUSH_RATE = 60 * 1000;
	private static final int BULK_UPLOAD_LIMIT = 50;
	
	private static final String TARGET_URL = "http://energy-hunter-nthu.appspot.com";
	private static final String ACTION = TARGET_URL + "/index";
	
	private JSONArray logContainer;
	
	
	
	public LogUploader(Context context) {
		
		this.bulkSendState = new bulkSend();
		this.persistAllState = new persistAllState();
		this.persistAndSendState = new persistThenSend();
		
		mDevId = getDeviceId();
		mContext = context;
		logContainer = new JSONArray();
		
		// process the result get from HTTPRequestHandler
		mHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg){
				if (msg.getData().containsKey(HTTPRequestHelper.RESPONSE)) {

					boolean success = msg.getData().getBoolean(HTTPRequestHelper.RESPONSE_SUCCESS);
					String bundleResult = msg.getData().getString(HTTPRequestHelper.RESPONSE);
					// if the response is success , remove the corresbonding log data from local storage
					if( Global.DEBUG)
						Log.d(TAG, success + bundleResult);
				}
			}
		};
		
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				flush();
			}


		}, 0, FLUSH_RATE);
		
	}

	/**
	 * Force the collected logs to be sent to EnHunter Server. 
	 */
	private void flush() {
		// TODO Auto-generated method stub
		sendLogRequest(ACTION);
		this.logContainer = new JSONArray();
	}
	
	/**
	 * Return the unique device Id of the phone
	 * @return A String containing the device unique identifier
	 */
	private String getDeviceId() {
		String product = Build.PRODUCT;
		//String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
		String androidId = "test";
		if (product == null || androidId == null) {
			return null;
		} else {
			return product + "_" + androidId;
		}
	}
	/**
	 * Upload the log
	 * @param url	the EnHunter server's URL
	 * @param data	log
	 * */
	private void sendLogRequest(final String url) {
		
		if(Global.DEBUG) {
			Log.d(TAG, "sendLogRequest url = "+ url);
			Log.d(TAG, "sendLogRequest container len = "+logContainer.length());
		}
		
		try {
			for(int i=0 ; i<logContainer.length() ;i++) {
				
				final String logData = logContainer.getString(i);
				final ResponseHandler<String> responseHandler =
					HTTPRequestHelper.getResponseHandlerInstance(mHandler);
				
				new Thread() {
					@Override
					public void run() {
						Map<String, String> params = new HashMap<String, String>();
						params.put("data", logData);
						
						HTTPRequestHelper helper = new HTTPRequestHelper(responseHandler);
						helper.doPost(url, null, null, params);
					}
				}.start();
			}
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	private int getNumEvent() {
		return logContainer.length();
	}
	
	/**
	 * Set to New State 
	 * */
	public void setLoggerState() {
		
	}
	
	/**
	 * push a log and to see if it is the time to execute network transfer
	 * @param log	String to log
	 * */
	public void eventPush(String log) {
		
		state.addEvent();
		
		/*
		if(Global.DEBUG)
			Log.d(TAG,"eventPush log =" + log);
		
		JSONObject dataObj = new JSONObject();
		try {
			dataObj.put("text", log);
			dataObj.put("id", Long.toString(Global.id++));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			if(Global.DEBUG)
				Log.d(TAG,"eventPush err = " +e.getMessage() );
			e.printStackTrace();
		}
		addEvent(dataObj);
		
		// do flush on every log if in TEST_MODE
		if(Global.TEST_MODE || getNumEvent() >= BULK_UPLOAD_LIMIT) {
			flush();
		}
		*/
		
	}
	/**
	 * addEvent (log) to container and wait for upload , 
	 * use "synchronized" to ensure add one element to container at a time
	 * */
	private synchronized void addEvent(JSONObject dataObj) {
		if (Global.DEBUG)
			Log.d(TAG,"addEvent");
		this.logContainer.put(dataObj);
	}
	
}
