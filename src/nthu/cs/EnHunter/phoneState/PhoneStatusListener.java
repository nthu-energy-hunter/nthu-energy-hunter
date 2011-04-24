package nthu.cs.EnHunter.phoneState;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneStatusListener extends PhoneStateListener{
	
	private String currentState = "idle";
	private static PhoneStatusListener instance = null;
	
	public static PhoneStatusListener getInstance() {
		if(instance == null)
			instance = new PhoneStatusListener();
		return instance;
	}
	
	public String getPhoneStatus() {
		return this.currentState;
	}
	
	@Override
	public void onCallStateChanged(int state, String phoneNumber) {
		
		switch(state) {
			case TelephonyManager.CALL_STATE_IDLE:
				this.currentState = "idle";
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				this.currentState = "offhook";
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				this.currentState = "rining";
			default:
				break;
		}
	}
	
	
}
