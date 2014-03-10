package com.prey.actions.picture;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;

import com.prey.PreyLogger;
import com.prey.actions.HttpDataService;
import com.prey.actions.camera.CameraAction;
import com.prey.activities.SimpleCameraActivity;
import com.prey.net.http.EntityFile;

public class PictureUtil {

	public static HttpDataService getPicture(Context ctx) {
		HttpDataService data = null;
		
		try {
			

			
			byte[] frontPicture=getPicture(ctx,"front");

			
 
			
			Thread.sleep(4000);
 
			byte[] backPicture=getPicture(ctx,"back");;
			 
			data = new HttpDataService(CameraAction.DATA_ID);
			data.setList(true);
			
 
				if (frontPicture != null) {
					PreyLogger.i("dataImagen data length=" + frontPicture.length);
					InputStream file = new ByteArrayInputStream(frontPicture);
					EntityFile entityFile = new EntityFile();
					entityFile.setFile(file);
					entityFile.setMimeType("image/png");
					entityFile.setName("picture.jpg");
					entityFile.setType("picture");

					
					
					data.addEntityFile(entityFile);
				} else {
					PreyLogger.i("dataImagen front null");
				}			 
			 
			if (backPicture != null) {
				PreyLogger.i("dataImagen data length=" + backPicture.length);
				InputStream file = new ByteArrayInputStream(backPicture);
				EntityFile entityFile = new EntityFile();
				entityFile.setFile(file);
				entityFile.setMimeType("image/png");
				entityFile.setName("screenshot.jpg");
				entityFile.setType("screenshot");

				 
				data.addEntityFile(entityFile);
			} else {
				PreyLogger.i("dataImagen back null");
			} 

		} catch (Exception e) {
			PreyLogger.e("Error causa:" + e.getMessage() + e.getMessage(), e);
		}
		return data;
	}
	
	private static byte[] getPicture(Context ctx,String focus){
		AudioManager mgr = null;
		SimpleCameraActivity.dataImagen=null;
		int streamType = AudioManager.STREAM_SYSTEM;
		SimpleCameraActivity.activity = null;
		Intent intent = new Intent(ctx, SimpleCameraActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("focus", focus);
		ctx.startActivity(intent);
		

		int i = 0;

		mgr = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		mgr.setStreamSolo(streamType, true);
		mgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		mgr.setStreamMute(streamType, true);

		while (SimpleCameraActivity.activity == null && i < 20) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			PreyLogger.i("esperando antes take picture[" + i + "]");
			i++;
		}
		if (SimpleCameraActivity.activity != null) {
			PreyLogger.i("takePicture activity no nulo");
			SimpleCameraActivity.activity.takePicture();
		} else {
			PreyLogger.i("takePicture activity nulo");
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		mgr.setStreamSolo(streamType, false);
		mgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		mgr.setStreamMute(streamType, false);

		try {
			i = 0;
			while (SimpleCameraActivity.activity != null && SimpleCameraActivity.dataImagen == null && i < 20) {
				Thread.sleep(1000);
				i++;
				PreyLogger.i("falta imagen[" + i + "]");
			}
		} catch (InterruptedException e) {
			PreyLogger.i("Error, causa:" + e.getMessage());
		}
		if (SimpleCameraActivity.activity != null) {
			SimpleCameraActivity.activity.finish();
		}
		return SimpleCameraActivity.dataImagen;
	}

	
	
	private static Bitmap joinImages(byte[] first, byte[] second)
	{
	    Bitmap bmp1, bmp2;
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    bmp1 = BitmapFactory.decodeByteArray(first, 0, first.length,options);
	    bmp2 = BitmapFactory.decodeByteArray(second, 0, second.length,options);
	    if (bmp1 == null || bmp2 == null)
	        return bmp1;
	    int height = bmp1.getHeight();
	    if (height < bmp2.getHeight())
	        height = bmp2.getHeight();

	    Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth() + bmp2.getWidth(), height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bmOverlay);
	    canvas.drawBitmap(bmp1, 0, 0, null);
	    canvas.drawBitmap(bmp2, bmp1.getWidth(), 0, null);
	    return bmOverlay;
	}
	
	private static byte[] bitmapToByteArray(Bitmap bm) {
        // Create the buffer with the correct size
        int iBytes = bm.getWidth() * bm.getHeight() * 4;
        ByteBuffer buffer = ByteBuffer.allocate(iBytes);

        // Log.e("DBG", buffer.remaining()+""); -- Returns a correct number based on dimensions
        // Copy to buffer and then into byte array
        bm.copyPixelsToBuffer(buffer);
        // Log.e("DBG", buffer.remaining()+""); -- Returns 0
        return buffer.array();
    }

}