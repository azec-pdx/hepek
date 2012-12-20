package ba.ima.hepek;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ba.ima.hepek.effects.SOSFlash;
import ba.ima.hepek.effects.SOSVibrate;
import ba.ima.hepek.utils.SoundThread;

/**
 * Main application activity with all UI
 * 
 * @author ZeKoU - amerzec@gmail.com
 * @author Gondzo - gondzo@gmail.com
 * @author NarDev - valajbeg@gmail.com
 */

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	/* Used as logging ID */
	private static final String ACTIVITY = MainActivity.class.getSimpleName();

	/* UI elements */
	private Button hepekButton;

	private Button ledWhite;

	private Button ledBlue;

	private Button ledGreen;

	private Button ledRed;

	// FlashThread flashThread = null;
	private Camera mCamera;
	public static SurfaceView preview;
	public static SurfaceHolder mHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		init();

	}

	/* Load UI components */
	private void init() {

		preview = (SurfaceView) findViewById(R.id.camSurface);
		mHolder = preview.getHolder();
		mHolder.addCallback(this);

		this.hepekButton = (Button) this.findViewById(R.id.hepekBtn);
		this.hepekButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Play sound
				Log.d(ACTIVITY, "Hepek sound effect invoked...");
				new SoundThread(MainActivity.this, R.raw.hepek_sound_effect,
						MainActivity.this).start();

				// Vibrate
				Log.d(ACTIVITY, "Hepek vibrate effect invoked...");
				new SOSVibrate().execute(new Activity[] { MainActivity.this });

				// Flash SOS
				Log.d(ACTIVITY, "Hepek flash effect invoked...");
				new SOSFlash(mCamera).execute(getApplicationContext());

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
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// Not interested in this because we hide cam preview surface anyways.
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mHolder = holder;
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// Not interested in this because we hide cam preview surface anyways.
	}
}
