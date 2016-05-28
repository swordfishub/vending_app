package com.example.etxeberria_vending.track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm_Receiver extends BroadcastReceiver {

    private final String TAG = "Alarm_Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
    	try {   
    		Log.e(TAG, "onReceive - StartService:");
    		// start the service
            Intent tracking = new Intent(context, Mi_Servicio.class);
            context.startService(tracking);
		} catch (Exception e) {
			Log.e(TAG, "onReceive:"+e.getMessage());
		}
        
    }
}
