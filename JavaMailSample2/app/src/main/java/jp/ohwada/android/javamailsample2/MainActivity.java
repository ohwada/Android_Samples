/**
 *  JavaMail Sample
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.javamailsample2;


import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.mail.*;



/**
     * class MainActivity
     */ 
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Mail";
    	private final static String TAG_SUB = "MainActivity";

        private final static String LF = "\n";

            	private final static String GMAIL_USER = "iot.ohwada@gmail.com";
            	private final static String GMAIL_PASSWORD = "kenichi3860";


    private GmailImap mGmailImap;

    private TextView  mTextView1;

    /**
     * == onCreate ==
     */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    mTextView1 = (TextView) findViewById(R.id.TextView_1);
    mTextView1.setMovementMethod(ScrollingMovementMethod.getInstance());

        Button btnGet = (Button) findViewById(R.id.Button_get);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getMail();
            }
        }); // btnGet

 
    mGmailImap = new GmailImap();
    mGmailImap.setGmailUser(GMAIL_USER);
    mGmailImap.setGmailPassword(GMAIL_PASSWORD);


    } // onCreate





 	/**
	 * getMail
	 */ 
private void getMail() {

    mGmailImap.getMailAsync( new GmailImap.Callback() {

            @Override
            public void onFinish(List<MailMessage> list) {
                log_d( "onFinish");
                procFinish(list);
                toast_short("mail get");
            }

            @Override
            public void onError(Exception e) {
                log_d( "onError");
                e.printStackTrace();
                toast_short("get mail failed");
            }

        }); // getMailAsync


} // getMail


 	/**
	 * procFinish
	 */ 
private void procFinish(List<MailMessage> list) {
                log_d( "procFinish");

    if ((list == null)||(list.size() == 0)) {
        log_d( "no MailMessage");
        toast_short( "no MailMessage");
        return;
    }


    String msg = "";
    for ( MailMessage mm: list ) {
        msg += mm.getMessage() + LF;
    }
    log_d(msg);

             if (mTextView1 != null ) {
                mTextView1.setText(msg);
            }
    toast_short( "get MailMessage");

 } // procFinish


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
