/**
 *  JavaMail Sample
 *  get mail from Gmail IMAP Server
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.javamailsample2;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.*;



 	/**
	 * class GmailImap
	 *  reference : http://d.hatena.ne.jp/jbking/20080608/p1
	 */ 
public class GmailImap {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Mail";
    	private final static String TAG_SUB = "GmailImap";

    	private final static String LF = "\n";

    	private final static int MAX_MESSAGE = 10;

		    	private final static String IMAP_HOST = "imap.gmail.com";

		    	private final static int IMAP_PORT = 993;

				    	private final static String IMAP_FOLDER = "INBOX";

					    	private final static String  IMAP__STORE = "imaps";

					    	private final static boolean SESSION_DEBUG = true;

            	private String mGmailUser;
            	private String mGmailPassword;


 // callback 
    private Callback mCallback; 


   /*
     * callback interface
     */    
    public interface Callback {
        public void onFinish(List<MailMessage> list);
        public void onError(Exception e);
    } // interface


/**
     * constractor
     */ 
public GmailImap() {
    // dummy
} // GmailImap


    /*
     * setCallback
     */ 
    public void setCallback( Callback callback ) {
        log_d("setCallback");
        mCallback = callback;
    } // setCallback


/**
     * setGmailUser
     */ 
public void setGmailUser(String user)  {
     mGmailUser = user;
} // setGmailUser


/**
     * setGmailPassword
     */ 
public void setGmailPassword(String password)  {
     mGmailPassword = password;
} // setGmailPassword



	/**
	 * getMailAsync
	 */ 
public void getMailAsync( Callback callback )  {


        mCallback = callback;

        new MailTask().execute();

} // getMailAsync



	/**
	 * getMail
	 * reference : https://docs.oracle.com/javaee/6/api/javax/mail/Message.html
	 */
	public List<MailMessage> getMail() throws Exception {

        List<MailMessage> list = new ArrayList<MailMessage>();
        MailMessage mail_message;

		Properties props = System.getProperties();
		Session session = Session.getInstance(props, null);
		session.setDebug(SESSION_DEBUG);

		Store store = session.getStore(IMAP__STORE);
		store.connect(IMAP_HOST, IMAP_PORT, mGmailUser, mGmailPassword);


		Folder folder = store.getFolder(IMAP_FOLDER);
		folder.open(Folder.READ_ONLY);
        Folder[]  folder_arr = folder.list();
            if ((folder_arr == null )||(folder_arr.length == 0)) {
			    log_d("CAN NOT folder.list");
            }

		Message[] msg_arr = folder.getMessages();
            if ((msg_arr == null )||( msg_arr.length==0 )) {
			    log_d("CAN NOT getMessages");
            }

    int len = msg_arr.length;
    if (len >MAX_MESSAGE) {
        len = MAX_MESSAGE;
    }

			for(int i=0; i<len; i++) {
                mail_message = new MailMessage( msg_arr[i] );
                list.add(mail_message);
			}

            if (folder != null) {
			    folder.close();
            }

            if (store != null) {
			    store.close();
            }


		return list;
	} // getMail


    /**
     * notifyFinish
     */
    private void notifyFinish(List<MailMessage> list) {
                    log_d("notifyFinish");
           if ( mCallback != null ) {
            mCallback.onFinish(list);
        } 
}	// notifyFinish

    /**
     * notifyError
     */
    private void notifyError(Exception e ) {
                    log_d("notifyError");
           if ( mCallback != null ) {
            mCallback.onError(e);
        } 
}	// notifyError


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d



/**
 * class MailTask
 */
public class MailTask extends
        AsyncTask<Void, Void, List<MailMessage>> {

         private Exception mException;

/**
 * constractor
 */
public MailTask() {
    super();
  } // MailTask


/**
 * == onPreExecute == 
 */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                log_d("onPreExecute");
            }  // onPreExecute

/**
 * == doInBackground == 
 */
            @Override
            protected List<MailMessage> doInBackground(Void... params) {

                log_d("doInBackground");
                try {
                    return getMail();
                } catch (Exception e) {
                    mException = e;
                    e.printStackTrace();
                    return null;
                }

            } // doInBackground

/**
 * == onPostExecute == 
 */
            @Override
            protected void onPostExecute(List<MailMessage> response) {

                super.onPostExecute(response);
                log_d("onPostExecute");

                if (response != null) {
                    notifyFinish( response );
                } else {
                    notifyError( mException );
                }

            } // onPostExecute

    } // class MailTask


} // class class GmailImap

