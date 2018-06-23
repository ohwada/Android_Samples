/**
 * Service Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.servicesample2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
	 * class LocalService
	 * send and recieve messages between Activity and Service
	 * reference : https://developer.android.com/guide/components/bound-services?hl=ja
	 */ 
public class LocalService extends Service {

    // debug
    private final static boolean D = true;
	private final static String TAG = "Service";
	private final static String TAG_SUB = "LocalService";
  
    public static final String ACTION = "jp.ohwada.android.servicesample2.LocalService";

	public static final int WHAT_TIMER = 1;
	public static final int WHAT_STRING = 2;
	public static final int WHAT_BUNDLE = 3;
	public static final int WHAT_REPLY = 4;
	public static final int WHAT_REPLY_OK = 5;


    public static final String BUNDLE_KEY_STRING = "key_string";
    public static final String BUNDLE_KEY_INT = "key_int";
    public static final String BUNDLE_KEY_FLOAT = "key_float";

	private final static String LF = "\n";



	private Messenger mMessenger;


 	/**
	 * onBind
	 */ 
	@Override
	public IBinder onBind(Intent i) {
		log_d("onBind");
		return mMessenger.getBinder();
	} // onBind

 	/**
	 * onCreate
	 */ 
	@Override
	public void onCreate() {
		super.onCreate();
		log_d("onCreate");
		mMessenger = new Messenger( new TestHandler() );
	} // onCreate


 	/**
	 * onDestroy
	 */ 
  @Override
  public void onDestroy() {
    super.onDestroy();
    log_d("onDestroy");
  } // onDestroy




 	/**
	 * write into logcat
	 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


 	/**
	 * == TestHandler ==
	 */ 
	class TestHandler extends Handler {


 	/**
	 * constractor
	 */ 
		public TestHandler() {
			// nothing to do
		}

 	/**
	 * handleMessage
	 */ 
		@Override
		public void handleMessage(Message msg) {
			log_d("handleMessage: " + msg.what );
			switch(msg.what) {
				case WHAT_STRING:
					String str = (String)msg.obj;
					log_d("string: " + str );
					break;
				case WHAT_BUNDLE:
					rcv_bundle(msg);
					break;
				case WHAT_REPLY:
					rcv_reply(msg);
					break;
				default:
					log_d( "recieve msg" ); 
					super.handleMessage(msg);
					break;
			}

		} // handleMessage


 	/**
	 * rcv_bundle
	 */ 
private void rcv_bundle(Message msg) {
					Bundle bundle = msg.getData();
			String str = bundle.getString( BUNDLE_KEY_STRING, "");
			int i = bundle.getInt( BUNDLE_KEY_INT, 0);
			float f = bundle.getFloat( BUNDLE_KEY_FLOAT, 0);
			String m = "rcv_bundle:" + LF;
			m += str + LF;
			m += Integer.toString(i) + LF;
			m += Float.toString(f) + LF;
			log_d(m);
} // rcv_bundle


 	/**
	 * rcv_reply
	 */ 
private void rcv_reply(Message msg) {
			String str =  (String)msg.obj;
			log_d( "rcv_reply: " + str );
			Messenger reply = msg.replyTo;
			if(reply != null) {
				try {
					// piyo piyo
					String ret =  str + " " + str;
					reply.send(Message.obtain(null, WHAT_REPLY_OK, ret));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} // if
} // rcv_reply


	} // class TestHandler


} // class LocalService
