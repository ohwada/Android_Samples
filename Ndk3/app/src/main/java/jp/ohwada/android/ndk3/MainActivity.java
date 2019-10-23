/**
  * NDK Sample
  * 2019-08-01 K.OHWADA
  */
package jp.ohwada.android.ndk3;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;


/** 
 *  class MainActivity
 * original : https://github.com/android/ndk-samples/tree/master/hello-jniCallback
 */
public class MainActivity extends Activity {

    int hour = 0;
    int minute = 0;
    int second = 0;
    TextView tickView;


/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tickView = (TextView) findViewById(R.id.tickView);
        tickView.setTextColor(Color.BLUE);
        tickView.setTextSize(20);

    }


/** 
 *  onResume
 */
    @Override
    public void onResume() {
        super.onResume();
        hour = minute = second = 0;
        TextView tv = (TextView)findViewById(R.id.hellojniMsg);
        tv.setText(stringFromJNI());
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(18);
        startTicks();
    }


/** 
 *  onPause
 */
    @Override
    public void onPause () {
        super.onPause();
        StopTicks();
    }


    /*
     * A function calling from JNI to update current timer
     */
    //@Keep
    private void updateTimer() {
        ++second;
        if(second >= 60) {
            ++minute;
            second -= 60;
            if(minute >= 60) {
                ++hour;
                minute -= 60;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String ticks = "" + MainActivity.this.hour + ":" +
                        MainActivity.this.minute + ":" +
                        MainActivity.this.second;
                MainActivity.this.tickView.setText(ticks);
            }
        }); // runOnUiThread
    }


    static {
        System.loadLibrary("hello-jnicallback");
    }
    public native  String stringFromJNI();
    public native void startTicks();
    public native void StopTicks();
}
