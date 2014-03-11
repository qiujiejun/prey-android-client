package com.prey.activities;

 

import android.annotation.SuppressLint;
import android.app.Activity;
 

import android.content.res.Configuration;
import android.hardware.Camera;

import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
 
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.prey.PreyLogger;
import com.prey.R;

public class SimpleCameraActivity extends Activity implements SurfaceHolder.Callback {

	public static SimpleCameraActivity activity = null;
	public static Camera camera;

	public static SurfaceHolder mHolder;
	public static byte[] dataImagen = null;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_camera);
		
		Bundle extras=getIntent().getExtras();
		String focus=extras.getString("focus");
		PreyLogger.i("focus:"+focus);
		
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		try {
			int numberOfCameras = Camera.getNumberOfCameras();
			if (numberOfCameras == 1) {
				camera = Camera.open();
				PreyLogger.i("open camera()");
			} else {
				if ("front".equals(focus)){
					camera = Camera.open(0);
					PreyLogger.i("open camera(0)");
				} else {
					camera = Camera.open(1);
					PreyLogger.i("open camera(1)");
				}
			}

		} catch (Exception e) {
			PreyLogger.e(" camera error:"+e.getMessage(),e);
		}
		if (camera==null){
			try {
				camera = Camera.open(0); 
			} catch (Exception e) {
			}
		}
		activity = this;
	}

	@SuppressLint("NewApi")
	public void takePicture(String focus) {
		try {
			if (camera != null) {
				Camera.Parameters parameters = camera.getParameters();
			 	
				if("front".equals(focus)){
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						parameters.set("orientation", "portrait");
						parameters.set("rotation", 90);
					}
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						parameters.set("orientation", "landscape");
						parameters.set("rotation", 180);
					}  
				}else{
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
						parameters.set("orientation", "portrait");
						parameters.set("rotation", 270);
					}
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						parameters.set("orientation", "landscape");
						parameters.set("rotation",  0);
					} 
				}
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				
				parameters.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
			   // parameters.setSceneMode(Parameters.SCENE_MODE_AUTO);
			    //parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
				parameters.set("iso", 400); 
			    parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
/*
			    int rotation = getWindowManager().getDefaultDisplay().getRotation();
			    int degrees = 0;
			    switch (rotation) {
			         case Surface.ROTATION_0: degrees = 0; break;
			         case Surface.ROTATION_90: degrees = 90; break;
			         case Surface.ROTATION_180: degrees = 180; break;
			         case Surface.ROTATION_270: degrees = 270; break;
			     }
			    Camera.CameraInfo info = new Camera.CameraInfo();
			     int result;
			     if ("front".equals(focus)) {
			         result = (info.orientation + degrees) % 360;
			         result = (360 - result) % 360;  // compensate the mirror
			     } else {  // back-facing
			         result = (info.orientation - degrees + 360) % 360;
			     }
			     camera.setDisplayOrientation(result);
			     parameters.setRotation(result);
			     */
				camera.setParameters(parameters);
			}
		} catch (Exception e) {
		}	
		try {	
			if(camera!=null){
				camera.startPreview();
			//	camera.autoFocus(  autoFocusCallback); 
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				PreyLogger.i("open takePicture()");
			}
		} catch (Exception e) {
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	
	AutoFocusCallback autoFocusCallback=new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
		}
    };
				
				
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			dataImagen = data;
		}
	};

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		PreyLogger.i("camera setPreviewDisplay()");
		mHolder = holder;
		try {
			if (camera != null)
				camera.setPreviewDisplay(mHolder);
		} catch (Exception e) {
			PreyLogger.e("Error PreviewDisplay:" + e.getMessage(), e);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		PreyLogger.i("camera surfaceDestroyed()");
		if (camera != null) {
			try{
				camera.stopPreview();
			}catch(Exception e){
			}
			try{
				camera.release();
			}catch(Exception e){
			}
			camera = null;
		}
	}

}
