/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;



import java.util.Timer;
import java.util.TimerTask;

import nthu.cs.EnHunter.Context.AppContext;
import nthu.cs.EnHunter.phoneState.SensorReader;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class HelloJni extends Activity
{
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

			
			
	}




	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		
		
	}


	private Button startButton;
	private Button stopButton;
	private static HelloJni instance;
	//SensorReader sensorListenr = null;	
	public SensorManager SM ;
	public ConnectivityManager connectivityManager;
	public WifiManager wifiManager ;
    /** Called when the activity is first created. */
	
	static int notificationRef = 1;
	/*String powerState="uninit";
	int batteryLevel = 0;
	*/
	
	public HelloJni() {
		
		Log.d("HelloJni","ctor!");
	}
	
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
      
        
      
        setContentView(R.layout.main);
        
        AppContext.getInstance().setContextFromActivity(this);
        
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        startButton.setOnClickListener(startClickListener);
        stopButton.setOnClickListener(stopClickListener);
        Log.d("HelloJni","onCreate!");
        instance = this;
        
       AppContext.getInstance().setServiceInstances((SensorManager) getSystemService(SENSOR_SERVICE),
    		   (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE),
    		   (WifiManager)getSystemService(Context.WIFI_SERVICE));
        /*
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        */

    }

    /*
    private Runnable r = new Runnable () {
    
    	public void run() {
    		
    		Log.d( "my_activity","sensor x-axis = " + SensorReader.getInstance().ax);
    		h.postDelayed(r, 1000);
    	}
    	
    };
    private Handler h = new Handler();
*/    
    
    /*
    public static HelloJni getInstance() {
    	if(instance == null)
    		instance = new HelloJni();
    	return instance;
    	
    }*/

    
    private Button.OnClickListener startClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
        	
            Intent intent = new Intent(HelloJni.this, SamplingService.class);
            
            //SamplingService.class.
            
            
           startService(intent);
           createNoti();
          
           
      /*   	h.removeCallbacks(r);
        	h.postDelayed(r, 3000);
        */
        }
    };

    void createNoti() {
    	int icon = R.drawable.icon;
    	String tickerText = "EnHunter";
    	long when = System.currentTimeMillis();
    	Notification noti = new Notification(icon, tickerText, when);
    	
    	String expandText = "EnHunter is hunting!";
    	String expandTitle = "EnHunter...";
    	//Intent intent  = new Intent(this,ListViewActivity.classcom.example.hellojni.ListViewActivity);
    	Intent intent  = new Intent("com.example.hellojni.tabView");
        
    	PendingIntent launchIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
    	noti.setLatestEventInfo(getApplicationContext(), expandTitle, expandText, launchIntent);
    
    	String ns = Context.NOTIFICATION_SERVICE;

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.notify(1, noti);
    }
    
    private Button.OnClickListener stopClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            //°±¤îªA°È
            Intent intent = new Intent(HelloJni.this, SamplingService.class);
            stopService(intent);
        }
    };
    
}