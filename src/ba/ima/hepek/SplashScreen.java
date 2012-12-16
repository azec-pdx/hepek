package ba.ima.hepek;

import ba.ima.hepek.R;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
 
/**
 * Splash screen activity
 */
public class SplashScreen extends Activity {
 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.splash_screen);
 
        Handler handler = new Handler();
 
        // run a thread after 2 seconds to start the home screen
        handler.postDelayed(new Runnable() {
 
            @Override
            public void run() {
 
                // make sure we close the splash screen so the user won't come back when it presses back key
 
                finish();
                // start the home screen
                
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(intent);
 
            }
 
        }, 4000); // time in milliseconds (1 second = 1000 milliseconds) until the run() method will be called
        new SoundThread().start();
    }
    
    
public class SoundThread extends Thread {
    	
    	
        @Override
        public void run() {
        	try {
				sleep(2000);
			
	        	MediaPlayer mp = MediaPlayer.create(SplashScreen.this, R.raw.junuz);
	        	mp.start();
        	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
      }
    }
 
}