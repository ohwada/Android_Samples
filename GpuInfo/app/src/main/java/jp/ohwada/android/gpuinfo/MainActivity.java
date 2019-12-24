/**
 * GPU Info
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.gpuinfo;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * class MainActivity
 * reference : https://stackoverflow.com/questions/15804365/is-there-any-way-to-get-gpu-information
 */ 
class MainActivity extends Activity implements GLSurfaceView.Renderer {

    // debug
    private final static String TAG = "GpuInfo";

    private final static String LF = "\n";

    private TextView mTextView;
    private GLSurfaceView mSurfaceView;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mTextView = new TextView(this);
        mSurfaceView = new GLSurfaceView(this);
        linearLayout.addView(mTextView);
        linearLayout.addView(mSurfaceView);
        setContentView(linearLayout);

        mSurfaceView.setRenderer(this);

        mTextView.setTextSize(20);
        mTextView.setTextColor(Color.BLACK);
}


/**
 * onSurfaceCreated
 */ 
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        StringBuilder sb=new StringBuilder();
        sb.append("GL Version: ").append(getGlEsVersion()).append(LF);
        sb.append("Vendor: ").append( gl.glGetString(GL10.GL_VENDOR)).append(LF);
        sb.append("Renderer: ").append(gl.glGetString(GL10.GL_RENDERER)).append(LF);
        String msg = sb.toString();
        showText_onUI(msg);
        log_d(msg);
    }


/**
 * getGlEsVersion
 */ 
private String getGlEsVersion() {
        ActivityManager activityManager =  (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        return configurationInfo.getGlEsVersion();
}


/**
 * showText_onUI
 */ 
private void showText_onUI(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(text);
                mSurfaceView.setVisibility(View.GONE);
            }
        });
}


/**
 * onSurfaceChanged
 */ 
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
            // nop
    }

/**
 * onDrawFrame
 */ 
    @Override
    public void onDrawFrame(GL10 gl) {
            // nop
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	   Log.d( TAG,  msg );
}


} // class MainActivity

