/**
 * OpenCL Sample
 * 2019-10-01 K.OHWADA
 * original : https://github.com/wf9a5m75/opencl_test
 */
package jp.ohwada.android.opencl2;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

/**
  * class MainActivity
 */
public class MainActivity extends Activity {

    private TextView mTextViewSample;

  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

    public native String  stringFromCL();


/**
  * onCreate
 */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
        mTextViewSample = (TextView) findViewById(R.id.sample_text);
  }


/**
  * onResume
 */
  @Override
  protected void onResume() {
        super.onResume();
        mTextViewSample.setText( stringFromCL() );
        mTextViewSample.setTextColor(Color.BLUE);
        mTextViewSample.setTextSize(20);
    }


} // class MainActivity
