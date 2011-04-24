package nthu.cs.EnHunter.phoneState;

import nthu.cs.EnHunter.Context.AppContext;

import com.example.hellojni.HelloJni;

import android.content.Context;
import android.hardware.*;

import android.util.Log;

@SuppressWarnings("deprecation")
public class SensorReader implements SensorEventListener{

	private static SensorReader instance = null;
	
	private SensorManager sm = null;
	public float ax,ay,az;
	private float ox,oy,oz;
	
	public SensorReader() {
		//sm = HelloJni.getInstance().SM;
		sm = AppContext.getInstance().sensorManager;
	}
	
	
	
	public static SensorReader getInstance() {
		if(instance==null) {
			instance = new SensorReader();
			Log.d("MTK","init new SensorReader instance!");
		}
		
		return instance;
	}

	public void register() {
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sm.SENSOR_DELAY_NORMAL);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),sm.SENSOR_DELAY_NORMAL);
	}
	
	public void unregister() {
		sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION));
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		switch(event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
					this.ax = event.values[0];
					this.ay = event.values[1];
					this.az = event.values[2];
					Log.d("-EnHunter-Sensor-", ax+" "+ay+" "+az);
			case Sensor.TYPE_ORIENTATION:
					this.ox = event.values[0];
					this.oy = event.values[1];
					this.oz = event.values[2];
					Log.d("-EnHunter-Sensor-", ox+" "+oy+" "+oz);
			break;
		}
		
	}
}
