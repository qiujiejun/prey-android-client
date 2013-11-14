package com.prey.receivers;

import java.util.HashMap;

import com.prey.PreyLogger;
import com.prey.actions.location.LocationUtil;
import com.prey.actions.location.PreyLocation;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.BatteryManager;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class BatteryLevelReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		int scale = -1;
		int level = -1;
		int voltage = -1;
		int temp = -1;
		level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
		voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
		PreyLogger.d("BatteryLevelReceiver level is " + level + "/" + scale + ", temp is " + temp + ", voltage is " + voltage);
		try{
			PreyLocation lastLocation=LocationUtil.getPreyLocation(ctx);
			HashMap<String, String> parameters = new HashMap<String, String>(); 
			parameters.put("lat", Double.toString(lastLocation.getLat()));
			parameters.put("lng", Double.toString(lastLocation.getLng()));
			parameters.put("acc", Float.toString(lastLocation.getAccuracy()));
			parameters.put("alt", Double.toString(lastLocation.getAltitude()));
			PreyLogger.d("lat:" + Double.toString(lastLocation.getLat()));
			PreyLogger.d("lng:" + Double.toString(lastLocation.getLng()));
			PreyLogger.d("acc:" + Float.toString(lastLocation.getAccuracy()));
			PreyLogger.d("alt:" + Double.toString(lastLocation.getAltitude()));
		}catch(Exception e){
			PreyLogger.e("Error, causa:"+e.getMessage(), e);
		}
	}

}
