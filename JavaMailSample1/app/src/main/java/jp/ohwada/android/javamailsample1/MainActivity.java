/**
 *  Send Mail Sample
 *  2018-05-01 K.OHWADA
 */





package jp.ohwada.android.javamailsample1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



/**
     * class MainActivity
     */ 
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Mail";
    	private final static String TAG_SUB = "MainActivity";

        private final static String LF = "\n";

        // set your account
            	private final static String GMAIL_USER = "user@gmail.com";
            	private final static String GMAIL_PASSWORD = "password";

                private final static String MAIL_TO = "user@example.com";

                private final static String SUBJECT = "TEST1";

                private final static String BODY = "test from android with jsvamail";


    private SendMail mSendMail;

    private TextView  mTextView1;

    /**
     * == onCreate ==
     */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 mTextView1 = (TextView) findViewById(R.id.TextView_1);

        Button btnSend = (Button) findViewById(R.id.Button_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        }); // btnSend

        mSendMail = new SendMail();
        mSendMail.setGmailUser(GMAIL_USER);
        mSendMail.setGmailPassword(GMAIL_PASSWORD);
        mSendMail.setCallback( new SendMail.Callback() {

            @Override
            public void onFinish() {
                log_d( "onFinish");
                toast_short("sended");
            }

            @Override
            public void onError(Exception e) {
                log_d( "onError");
                e.printStackTrace();
                toast_short("send failed");
            }

        }); // mSendMail setCallback

    showText();

    } // onCreate


 	/**
	 * showText
	 */ 
private void showText() {
    String text = MAIL_TO + LF;
     text += SUBJECT + LF;
    text += BODY + LF;
    mTextView1.setText(text);
} // showText


 	/**
	 * sendMail
	 */ 
private void sendMail() {
    mSendMail.sendAsync(MAIL_TO, SUBJECT, BODY); 
} // sendMail


/**
 * toast_short
 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class MainActivity
