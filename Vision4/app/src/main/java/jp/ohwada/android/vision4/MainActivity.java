/**
 * Vision Sample
 * OCR Reader
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision4;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;

/**
 * Main activity demonstrating how to pass extra parameters 
 * to an activity that recognizes text.
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/ocr-reader
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "MainActivity";

  // Constants used to pass extra data in the intent
    private static final String  EXTRA_KEY_FOCUS = OcrCaptureActivity.EXTRA_KEY_FOCUS;
    private static final String  EXTRA_KEY_FLASH = OcrCaptureActivity.EXTRA_KEY_FLASH;
    private static final String  EXTRA_KEY_TEXT = OcrCaptureActivity.EXTRA_KEY_TEXT;

    private static final int RC_OCR_CAPTURE = 9003;

    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton mButtonFocus;
    private CompoundButton mButtonFlash;
    private TextView mTextViewStatus;
    private TextView mTextViewValue;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewStatus = (TextView)findViewById(R.id.tv_status_message);
        mTextViewValue = (TextView)findViewById(R.id.tv_text_value);

        mButtonFocus = (CompoundButton) findViewById(R.id.cb_auto_focus);
        mButtonFlash = (CompoundButton) findViewById(R.id.cb_use_flash);

            Button btnRead = (Button)findViewById(R.id.button_read_text);
            btnRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startCaptureActivity();
                }
            }); // btnRead

    }


/**
 * launch Ocr capture activity.
  */
private void startCaptureActivity() {
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(EXTRA_KEY_FOCUS, mButtonFocus.isChecked());
            intent.putExtra(EXTRA_KEY_FLASH, mButtonFlash.isChecked());
            startActivityForResult(intent, RC_OCR_CAPTURE);
} // startCaptureActivity


/**
  * Called when an activity you launched exits, giving you the requestCode
  */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(EXTRA_KEY_TEXT);
                    mTextViewStatus.setText(R.string.status_ocr_success);
                    mTextViewValue.setText(text);
                    log_d("Text read: " + text);
                } else {
                    mTextViewStatus.setText(R.string.status_ocr_failure);
                    log_d("No Text captured, intent data is null");
                } // f data
            } else {
                mTextViewStatus.setText(String.format(getString(R.string.status_ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            } // if resultCode
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        } // if requestCode
    } // onActivityResult


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class MainActivity
