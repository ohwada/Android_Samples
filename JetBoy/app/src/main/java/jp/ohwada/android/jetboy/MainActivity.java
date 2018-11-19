/**
 * JetBoy
 * 2018-11-01 K.OHWADA
 */
package jp.ohwada.android.jetboy;

 import jp.ohwada.android.jetboy.JetBoyView.JetBoyThread;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * class MainActivity
 * original : https://github.com/Miserlou/Android-SDK-Samples/tree/master/JetBoy
 */
public class MainActivity extends Activity implements View.OnClickListener {

   	// debug
	private final static String TAG = "JetBoy";
    	private final static String TAG_SUB = "MainActivity";
    private final static boolean DEBUG = true; 

    /** A handle to the thread that's actually running the animation. */
    private JetBoyThread mJetBoyThread;

    /** A handle to the View in which the game is running. */
    private JetBoyView mJetBoyView;

    // the play start button
    private Button mButton;

    // used to hit retry
    private Button mButtonRetry;


    // the window for instructions and such
    private TextView mTextView;

    // game window timer
    private TextView mTimerView;

    /**
     * Required method from parent class
     * 
     * @param savedInstanceState - The previous instance of this app
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // get handles to the JetView from XML and the JET thread.
        mJetBoyView = (JetBoyView)findViewById(R.id.JetBoyView);
        mJetBoyThread = mJetBoyView.getThread();

        // look up the happy shiny button
        mButton = (Button)findViewById(R.id.Button01);
        mButton.setOnClickListener(this);

        mButtonRetry = (Button)findViewById(R.id.Button02);
        mButtonRetry.setOnClickListener(this);


        // set up handles for instruction text and game timer text
        mTextView = (TextView)findViewById(R.id.text);
        mTimerView = (TextView)findViewById(R.id.timer);

        mJetBoyView.setTimerView(mTimerView);

        mJetBoyView.SetButtonView(mButtonRetry);

        mJetBoyView.SetTextView(mTextView);
    }
    

    /**
     * Handles component interaction
     * 
     * @param v The object which has been clicked
     */
    public void onClick(View v) {
        // this is the first screen
        if (mJetBoyThread.getGameState() == JetBoyThread.STATE_START) {
            mButton.setText("PLAY!");
            mTextView.setVisibility(View.VISIBLE);

            mTextView.setText(R.string.helpText);
            mJetBoyThread.setGameState(JetBoyThread.STATE_PLAY);

        }
        // we have entered game play, now we about to start running
        else if (mJetBoyThread.getGameState() == JetBoyThread.STATE_PLAY) {
            mButton.setVisibility(View.INVISIBLE);
            mTextView.setVisibility(View.INVISIBLE);
            mTimerView.setVisibility(View.VISIBLE);
            mJetBoyThread.setGameState(JetBoyThread.STATE_RUNNING);

        }
        // this is a retry button
        else if (mButtonRetry.equals(v)) {

            mTextView.setText(R.string.helpText);

            mButton.setText("PLAY!");
            mButtonRetry.setVisibility(View.INVISIBLE);
            // mButtonRestart.setVisibility(View.INVISIBLE);

            mTextView.setVisibility(View.VISIBLE);
            mButton.setText("PLAY!");
            mButton.setVisibility(View.VISIBLE);

            mJetBoyThread.setGameState(JetBoyThread.STATE_PLAY);

        } else {
            Log.d("JB VIEW", "unknown click " + v.getId());

            Log.d("JB VIEW", "state is  " + mJetBoyThread.mState);

        }
    }


    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

// substitute by BACK key ,since there is no DPAD key in device

        if (mJetBoyThread.getGameState() == JetBoyThread.STATE_RUNNING) {
            return mJetBoyThread.doKeyDown(keyCode, msg);
}

         if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, msg);
        } else {
            return mJetBoyThread.doKeyDown(keyCode, msg);
        }
    } // onKeyDown

    /**
     * Standard override for key-up.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {

// substitute by BACK key ,since there is no DPAD key in device
        if (mJetBoyThread.getGameState() == JetBoyThread.STATE_RUNNING) {
            return mJetBoyThread.doKeyUp(keyCode, msg);
}

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyUp(keyCode, msg);
        } else {
            return mJetBoyThread.doKeyUp(keyCode, msg);
        }
    } // onKeyUp


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (DEBUG) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  class MainActivity
