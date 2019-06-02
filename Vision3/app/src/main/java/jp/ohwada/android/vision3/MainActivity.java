/**
 * Vision Sample
 * Barcode Detection
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision3;


import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that reads barcodes.
 * original : https://github.com/googlesamples/android-vision/tree/master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Vision";
    private final static String TAG_SUB = "MainActivity";

    // constants used to pass extra data in the intent
    private final static String EXTRA_KEY_AUTO_FOCUS = BarcodeCaptureActivity.EXTRA_KEY_AUTO_FOCUS;
    private final static String EXTRA_KEY_USE_FLASH = BarcodeCaptureActivity.EXTRA_KEY_USE_FLASH;
    private static final String EXTRA_KEY_BARCODE =     BarcodeCaptureActivity.EXTRA_KEY_BARCODE;

    private static final int RC_BARCODE_CAPTURE = 9001;

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton mButtonAutoFocus;
    private CompoundButton mButtonUseFlash;
    private TextView mTextViewStatusMessage;
    private TextView mTextViewBarcodeValue;

/**
  * onCreate
  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewStatusMessage = (TextView)findViewById(R.id.tv_status_message);
        mTextViewBarcodeValue = (TextView)findViewById(R.id.tv_barcode_value);

        mButtonAutoFocus = (CompoundButton) findViewById(R.id.cb_auto_focus);
        mButtonUseFlash = (CompoundButton) findViewById(R.id.cb_use_flash);

        Button btnRead = (Button) findViewById(R.id.button_read_barcode);
            btnRead .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startBarcodeCaptureActivity();
                }
            }); // btnRead 

    } // onCreate


/**
  * Called when an activity you launched exits
  */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(EXTRA_KEY_BARCODE);
                    mTextViewStatusMessage.setText(R.string.barcode_success);
                    mTextViewBarcodeValue.setText(barcode.displayValue);
                    log_d("Barcode read: " + barcode.displayValue);
                } else {
                    mTextViewStatusMessage.setText(R.string.barcode_failure);
                    log_d( "No barcode captured, intent data is null");
                } // if data
            } else {
                mTextViewStatusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            } //  if resultCode
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        } //  if requestCode
    } // onActivityResult


 /**
  * launch barcode activity
  */
private void startBarcodeCaptureActivity() {
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(EXTRA_KEY_AUTO_FOCUS, mButtonAutoFocus.isChecked());
            intent.putExtra(EXTRA_KEY_USE_FLASH, mButtonUseFlash.isChecked());
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
} // startBarcodeCaptureActivity


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class MainActivity
