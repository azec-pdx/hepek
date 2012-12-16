package ba.ima.hepek;



import ba.ima.hepek.R;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button hepekButton; 
	FlashThread flashThread=null;
	MediaPlayer mp;
	int dot = 200;      // Length of a Morse Code "dot" in milliseconds
	int dash = 500;     // Length of a Morse Code "dash" in milliseconds
	int short_gap = 200;    // Length of Gap Between dots/dashes
	int medium_gap = 500;   // Length of Gap Between Letters
	int long_gap = 1000;    // Length of Gap Between Words
	long[] pattern = {
	    0,  // Start immediately
	    dot, short_gap, dot, short_gap, dot,    // s
	    medium_gap,
	    dash, short_gap, dash, short_gap, dash, // o
	    medium_gap,
	    dot, short_gap, dot, short_gap, dot,    // s
	    long_gap
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.hepekButton = (Button)this.findViewById(R.id.hepekBtn);
        this.hepekButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				playSound(R.raw.penzioner);
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(pattern, -1);
				
				boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                System.out.println("imal flash"+hasFlash);
				if (hasFlash){
					if (flashThread==null){
						flashThread = new FlashThread(pattern);
						flashThread.start();
					}
                }
				
			}
        	
        });
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

        
    	if (mp!=null){
    		if (!mp.isPlaying()){
    			mp = MediaPlayer.create(this, uri);
    		}
    	}else{
    		mp = MediaPlayer.create(this, uri);
    	}
        if (!mp.isPlaying()){
        	mp.start();
        }
    }

    
    
    public class FlashThread extends Thread {
    	
    	long[] pattern;
    	public FlashThread(long[] pattern){
    		this.pattern=pattern;
    	}
        @Override
        public void run() {
        	Camera cam=null;
            // Simulate a slow network
            try {
            	
            	cam = Camera.open();     
            	Parameters p = cam.getParameters();
            	p.setFlashMode(Parameters.FLASH_MODE_TORCH);
            	if (Build.MANUFACTURER.equalsIgnoreCase("samsung")){
            		p.setFlashMode(Parameters.FLASH_MODE_ON);
            	}
            	cam.setParameters(p);
            	System.out.println("pocinjem flash pattern");
            	cam.startPreview();
            	if (Build.MANUFACTURER.equalsIgnoreCase("samsung")){
            		cam.autoFocus(new AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                        }
                    });
            	}
            	boolean on=true;
            	for (long l:pattern){
            		if (on){
            			System.out.println("on"+l);
            			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
            			if (Build.MANUFACTURER.equalsIgnoreCase("samsung")){
                    		p.setFlashMode(Parameters.FLASH_MODE_ON);
                    	}
                    	cam.setParameters(p);
            			
            			on=false;
            		}else{
            			System.out.println("off"+l);
            			p.setFlashMode(Parameters.FLASH_MODE_OFF);
            			cam.setParameters(p);
            			on=true;
            		}
            		sleep(l);
            	}
              
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
             finally {
            	 System.out.println("finaly");
            	try{
            		cam.stopPreview();
            		cam.release();
            	}
            	catch(Exception e){
            		System.out.println("camstop failed");
            	}

             }
        
      }
    }
}
 