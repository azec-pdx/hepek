package ba.ima.hepek;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	private Button hepekButton;
	FlashThread flashThread = null;
	MediaPlayer mp;
	Camera mCamera;
	public static SurfaceView preview;
	public static SurfaceHolder mHolder;
	int dot = 200; // Length of a Morse Code "dot" in milliseconds
	int dash = 500; // Length of a Morse Code "dash" in milliseconds
	int short_gap = 200; // Length of Gap Between dots/dashes
	int medium_gap = 500; // Length of Gap Between Letters
	int long_gap = 1000; // Length of Gap Between Words
	long[] pattern = { 0, // Start immediately
			dot, short_gap, dot, short_gap, dot, // s
			medium_gap, dash, short_gap, dash, short_gap, dash, // o
			medium_gap, dot, short_gap, dot, short_gap, dot, // s
			long_gap };

	private boolean hasFlash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init hardware
		hasFlash = getApplicationContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		Log.d("FLASH", "IMA flash?: " + hasFlash);

		preview = (SurfaceView) findViewById(R.id.camSurface);
		mHolder = preview.getHolder();
		mHolder.addCallback(this);

		this.hepekButton = (Button) this.findViewById(R.id.hepekBtn);
		this.hepekButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Play sound
				playSound(R.raw.penzioner);
				// Vibrate
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(pattern, -1);

				// Flash
				if (hasFlash) {
					flashThread = new FlashThread(pattern);
					flashThread.start();
				}

			}

		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("MAIN", "onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			Log.d("FLASH", "Starting camera preview");
			mCamera = Camera.open();
			mCamera.startPreview();
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			Log.e("FLASH", "Error starting camera preview!");
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Manager of Sounds
	 */
	protected void playSound(int uri) {

		if (mp != null) {
			if (!mp.isPlaying()) {
				mp = MediaPlayer.create(this, uri);
			}
		} else {
			mp = MediaPlayer.create(this, uri);
		}
		if (!mp.isPlaying()) {
			mp.start();
		}
	}

	/**
	 * Used for Flash effect pattern execution.
	 * 
	 */
	public class FlashThread extends Thread {

		long[] pattern;

		public FlashThread(long[] pattern) {
			this.pattern = pattern;
		}

		@Override
		public void run() {

			Parameters params = null;
			// Simulate a slow network
			try {

				params = mCamera.getParameters();
				params.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(params);

				Log.d("FLASH", "Begin flashing...");

				boolean on = true;
				for (long l : pattern) {
					if (on) {
						Log.d("FLASH", "ON for: " + l);
						params.setFlashMode(Parameters.FLASH_MODE_TORCH);
						mCamera.setParameters(params);

						on = false;
					} else {
						Log.d("FLASH", "OFF for:" + l);
						params.setFlashMode(Parameters.FLASH_MODE_OFF);
						mCamera.setParameters(params);
						on = true;
					}
					sleep(l);
				}
				// Don't stop camera preview in thread - it should remain active

			} catch (InterruptedException e) {

				Log.e("FLASH", "Failed flashing:" + e.getMessage());
				e.printStackTrace();
			} finally {

				try {
					params.setFlashMode(Parameters.FLASH_MODE_OFF);
					mCamera.setParameters(params);
				} catch (Exception e) {
					Log.e("FLASH", "Failed stopping flash!");
				}

			}

		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mHolder = holder;
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

		// mCamera.stopPreview();
		mHolder = null;
	}
}
