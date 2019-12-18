/** 
 *  OpenGL eS2.0 Sample
 *  draw Rectangle with Image Texture
 *  2019-10-01 K.OHWADA
 */
package jp.ohwada.android.opengl6;



import android.app.Activity;
import android.os.Bundle;


/**
 *  class MainActivity
 */
public class MainActivity extends Activity {
	
	
	private SampleGLSurfaceView mGLSurfaceView;
	

/** 
 *  onCreate
 *  Called when the activity is first created
 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
	    mGLSurfaceView = (SampleGLSurfaceView)findViewById(R.id.surfaceView);

    }
    

/** 
 *  onResume
 */
    @Override
    public void onResume() {
    	super.onResume();
    	mGLSurfaceView.onResume();
    }
 

/** 
 *  onPause
 */   
    @Override
    public void onPause() {
    	super.onPause();
    	mGLSurfaceView.onPause();
    }


} // class MainActivity