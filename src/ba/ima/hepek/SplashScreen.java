package ba.ima.hepek;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Splash screen activity
 */
public class SplashScreen extends Activity {

	private boolean runOnce = false;

	private Intent mainActivity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash_screen);

		new SoundThread(this, R.raw.junuz).start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("SPLASH", "SPLASH onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Analytics
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("SPLASH", "SPLASH onResume()");
		mainActivity = new Intent(SplashScreen.this, MainActivity.class);

		if (!runOnce) {
			runOnce = true;
			int timeToWaitSplash = 4000;
			Handler handler = new Handler();
			// run a thread and wait few seconds to show main screen
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {

					// make sure we close the splash screen so the user won't
					// come
					// back when it presses back key

					finish();
					// start the home screen

					SplashScreen.this.startActivity(mainActivity);

				}

			}, timeToWaitSplash); // time in milliseconds (1 second = 1000
									// milliseconds) until
			// the run() method will be called

			// Next time on resume skip this...
		} else {
			SplashScreen.this.startActivity(mainActivity);
		}

	}

	/**
	 * Implemets SoundThread for playing audio.
	 * 
	 * @author amer
	 * 
	 */

	public class SoundThread extends Thread {

		private int soundResource;
		private Context context;

		public SoundThread(final Context context, final int soundResource) {
			this.soundResource = soundResource;
			this.context = context;
		}

		@Override
		public void run() {
			try {
				sleep(2000);

				MediaPlayer mp = MediaPlayer.create(context, soundResource);
				mp.start();
			} catch (InterruptedException e) {
				Log.d("SPLASH", "Error playing sound");
				e.printStackTrace();
			}

		}
	}

}