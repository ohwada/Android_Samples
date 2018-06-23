/**
 * Service Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.servicesample2;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
	 * class MainActivity
	 */ 
public class MainActivity extends AppCompatActivity implements ServiceConnection{

// debug
    private final static boolean D = true;
	private final static String TAG = "Service";
	private final static String TAG_SUB = "MainActivity";


	private Messenger mMessenger;

	private Messenger mReplyMessanger;

    private boolean isBind = false;



/**
 * onCreate
 */ 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnConnect = (Button)findViewById(R.id.Button_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        }); // btnConnect

		Button btnDisconnect = (Button)findViewById(R.id.Button_disconnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        }); // btnDisconnect


		Button btnSend1 = (Button)findViewById(R.id.Button_send1);
        btnSend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send1();
            }
        }); // btnSend1

		Button btnSend2 = (Button)findViewById(R.id.Button_send2);
        btnSend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send2();
            }
        }); // btnSend2

		Button btnSend3 = (Button)findViewById(R.id.Button_send3);
        btnSend3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send3();
            }
        }); // btnSend3

	} // onCreate


 	/**
	 * onDestroy
	 */ 
  @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
        stopLocalService();
    } // onDestroy


/**
 * onServiceConnected
 */ 
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		log_d("onServiceConnected");
		toast_short("onServiceConnected");
        isBind = true;
		mMessenger = new Messenger(service);
		mReplyMessanger = new Messenger( new ReplyHandler() );
	} // onServiceConnected

/**
 *  onServiceDisconnected
 */ 
	@Override
	public void onServiceDisconnected(ComponentName name) {
		log_d("onServiceDisconnected");
		toast_short("onServiceDisconnected");
        isBind = false;
		mMessenger = null;
		mReplyMessanger = null;
	} //  onServiceDisconnected


	/**
	 * connect
	 */
private void connect() {
    Intent intent = new Intent(this, LocalService.class);
		bindService( intent, this, Context.BIND_AUTO_CREATE );
	} // connect


	/**
	 * disconnect
	 */
private void disconnect() {
        try {
            if(isBind) {
		        unbindService(this);
            }
		} catch (Exception e) {
			e.printStackTrace();
        }
		mMessenger = null;
		mReplyMessanger = null;
	} // disconnect


 	/**
	 * stopLocalService
	 */ 
private void stopLocalService() {
    Intent intent = new Intent(this, LocalService.class);
        stopService(intent);
} // stopTimerService


	/**
	 * send1
	 * send Srting
	 */
private void send1() {
	log_d("send1");
    Message msg = Message.obtain(null, LocalService.WHAT_STRING, "hoge");
    sendMsg(msg);
} // send1


	/**
	 * send2
	 * send Bundle
	 */
private void send2() {
	log_d("send2");
			Bundle bundle = new Bundle();
			//arg.putSerializable("testData", data);

			bundle.putString( LocalService.BUNDLE_KEY_STRING, "foo");
			bundle.putInt( LocalService.BUNDLE_KEY_INT, 123);
			bundle.putFloat( LocalService.BUNDLE_KEY_FLOAT, 3.14f);

			Message msg = Message.obtain(null, LocalService.WHAT_BUNDLE);
			//msg.setData(arg);
			msg.setData(bundle);
            sendMsg(msg);

	} // send2


	/**
	 * send3
	 * send and replay
	 */
private void send3() {
	log_d("send3");
    Message msg = Message.obtain(null, LocalService.WHAT_REPLY, "piyo");

    // call back
	msg.replyTo = mReplyMessanger;
    sendMsg(msg);
} // send3


	/**
	 * sendMsg
	 */
private void sendMsg(Message msg) {
	log_d("sendMsg");
        boolean is_error = false;
		try {
			mMessenger.send(msg);
		} catch (RemoteException e) {
            is_error = true;
			e.printStackTrace();
		}
        if (is_error) {
			toast_short("send failed");
        } else {
		    toast_short("sended");
        }
} // sendMsg


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


 	/**
	 * == class ReplyHandler ==
	 */ 
 class ReplyHandler extends Handler {


 	/**
	 * constractor
	 */ 
		public ReplyHandler() {
			// nothing to do
		}

 	/**
	 * handleMessage
	 */ 
		@Override
		public void handleMessage(Message msg) {
			log_d("handleMessage:" + msg.what );
			switch(msg.what) {
				case LocalService.WHAT_REPLY_OK:
					String m = (String)msg.obj;
					log_d( m );
					toast_short( m );
					break;
			}
		} // handleMessage

	} // class ReplyHandler

}// class MainActivity
