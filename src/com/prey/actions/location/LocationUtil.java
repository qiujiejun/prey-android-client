package com.prey.actions.location;


import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.actions.HttpDataService;
import com.prey.exceptions.PreyException;
import com.prey.services.LocationService;

public class LocationUtil {
	
	public static HttpDataService dataLocation(Context ctx) {
		HttpDataService data = new HttpDataService("location");
		try {
			data.setList(true);
			PreyConfig.getPreyConfig(ctx).setMissing(true);
			HashMap<String, String> parametersMap = new HashMap<String, String>();
			PreyLocation lastLocation = getPreyLocation(ctx);
			parametersMap.put("lat", Double.toString(lastLocation.getLat()));
			parametersMap.put("lng", Double.toString(lastLocation.getLng()));
			parametersMap.put("accuracy", Float.toString(lastLocation.getAccuracy()));
			data.addDataListAll(parametersMap);
			PreyConfig.getPreyConfig(ctx).setMissing(false);
		} catch (Exception e) {
			PreyLogger.e("Error causa:" + e.getMessage() , e);
		}
		return data;
	}
	
	public static PreyLocation getPreyLocation(Context ctx) throws PreyException{
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
        if (ConnectionResult.SUCCESS == resultCode) {
        	return getPreyLocationPlayService(ctx);
        }else{
        	return getPreyLocationAppService(ctx);
        }
	}
	
	public static PreyLocation getPreyLocationPlayService(Context ctx) throws PreyException{
		
		PreyGooglePlayServiceLocation play=new PreyGooglePlayServiceLocation();
		play.init(ctx);
		try{
			play.startPeriodicUpdates();
		}catch(Exception e){
			
		}
		Location currentLocation = play.getLastLocation(ctx);
        while (currentLocation==null) {
        	try {Thread.sleep(5000);} catch (InterruptedException e) {}
        	currentLocation = play.getLastLocation(ctx);
        }
        try{
        	play.stopPeriodicUpdates();
        }catch(Exception e){
			
		}
        PreyLocation preyLocation= new  PreyLocation(currentLocation) ;
         
        return preyLocation;
	}
	public static PreyLocation getPreyLocationAppService(Context ctx) throws PreyException{
		Intent intent = new Intent(ctx, LocationService.class);
		ctx.startService(intent);
		boolean validLocation = false;
		PreyLocation lastLocation=null;
		int i = 0;
		while (!validLocation) {
			lastLocation = PreyLocationManager.getInstance(ctx).getLastLocation();
			if (lastLocation.isValid()) {
				validLocation = true;
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw new PreyException("Thread was intrrupted. Finishing Location NotifierAction", e);
				}
				if (i > 2) {
					return null;
				}
				i++;
			}
		}
		ctx.stopService(intent);
		return lastLocation;
	} 
}
