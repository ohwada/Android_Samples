/**
 *  Snackbar sample
 *  2018-05-01 K.OHWADA
 */
package jp.ohwada.android.snackbarsample;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *  class MainActivity
 */
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "snackbar";
    	private final static String TAG_SUB = "MainActivity";

    	private final static String SNACKBAR_TEXT = "Hello";

    	private final static String SNACKBAR_LABEL_ACTION = "action";

    private LinearLayout mSnackbarView;

    private TextView mTextView1;


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSnackbarView = (LinearLayout) findViewById(R.id.LinearLayout_main);

        mTextView1 = (TextView) findViewById(R.id.TextView_1);

        Button btnSnackbar1 = (Button) findViewById(R.id.Button_snackbar_1);
        btnSnackbar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbarSimple();
            }
        }); // btnSnackbar1

        Button btnSnackbar2 = (Button) findViewById(R.id.Button_snackbar_2);
        btnSnackbar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbarAction();
            }
        }); // btnSnackbar2

    } // onCreate

/**
 * showSnackbarSimple
 */
private void showSnackbarSimple() {

                final Snackbar snackbar = Snackbar.make(mSnackbarView, SNACKBAR_TEXT, Snackbar.LENGTH_LONG);
                snackbar.show();

} // showSnackbarSimple

/**
 * showSnackbarAction
 */
private void showSnackbarAction() {

                final Snackbar snackbar = Snackbar.make(mSnackbarView, SNACKBAR_TEXT, Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction(SNACKBAR_LABEL_ACTION, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        procSnackbaClick();
                        snackbar.dismiss();
                    }
                });
                snackbar.show();

} // showSnackbarAction


/**
 * procSnackbaClick
 */
private void procSnackbaClick() {
        log_d("procSnackbaClick");
        mTextView1.setText("snackbar action Clicked");
} // procSnackbaClick

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
