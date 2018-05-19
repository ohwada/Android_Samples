/**
 * Gmail API Sample
 * send mail with Gmail API
 * 2018-05-01 K.OHWADA
 */


package jp.ohwada.android.gmaillapisample;


 import com.google.android.gms.common.ConnectionResult;
 import com.google.android.gms.common.GoogleApiAvailability;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
 import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
 import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
 import com.google.api.client.util.Base64;
 import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import com.google.api.services.gmail.model.*;

import android.Manifest;
import android.accounts.AccountManager;
 import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
 import android.util.Log;


 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Properties;

import javax.mail.*;
 import javax.mail.Message;
 import javax.mail.internet.InternetAddress;
 import javax.mail.internet.MimeMessage;


/**
 * class GmailSend
 * refarence : https://developers.google.com/gmail/api/guides/sending
 */
public class GmailSend {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "GMail";
    	private final static String TAG_SUB = "GmailSend";


    public static final int STATUS_PRE_EXECUTE = 1;
    public static final int STATUS_CANCELLED = 2;


    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String APPLICATION_NAME = "Gmail API Sample";

    private static final String USER_ID_ME = "me";

// GmailScopes
// https://developers.google.com/gmail/api/auth/scopes
// https://developers.google.com/resources/api-libraries/documentation/gmail/v1/java/latest/com/google/api/services/gmail/GmailScopes.html
    private static final String[] SCOPES = { GmailScopes.GMAIL_SEND };

    
    private GmailRequestTask mGmailRequestTask;

        private SharedPreferences mSharedPreferences;

        private GoogleAccountCredential mCredential;

        private com.google.api.services.gmail.Gmail mService;

    private MimeMessage  mMimeMessage;

    private String mGmailAccountName;

    private Callback     mCallback;


   /*
     * callback interface
     */ 
    public interface Callback {
        public void onFinish();
        public void onGooglePlayServicesAvailabilityIOException(int code);
        public void onUserRecoverableAuthIOException(Intent intent);
        public void onError(String error );
        public void onStatus(int status);
    } // interface


/**
 * constractor
 */
public GmailSend(Context context) {

            log_d("GmailSend");

        // Initialize credentials
        List<String> list__scope = Arrays.asList(SCOPES);
        mCredential = createCredential(context, list__scope);

        mSharedPreferences  = PreferenceManager.getDefaultSharedPreferences( context );

        String accountName = mSharedPreferences.getString(PREF_ACCOUNT_NAME, "");

        if (!TextUtils.isEmpty(accountName)) {
            log_d("accountName: " + accountName);
            mGmailAccountName = accountName;
            mCredential.setSelectedAccountName(accountName);
        }

} // GmailSend


    /**
     * cancelTask
     */
public void cancelTask() {
            log_d("cancelTask");
    if(mGmailRequestTask != null) {
        mGmailRequestTask.cancel(true);
    }
} // cancelTask



/**
 * createCredential
 */
public GoogleAccountCredential createCredential( Context context, List<String> list_scope) {

            log_d("createCredentiall");

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, list_scope);
                credential.setBackOff(new ExponentialBackOff());
        return credential;
} // createCredential


/**
 * createGmailService
 */
public com.google.api.services.gmail.Gmail createGmailService(GoogleAccountCredential credential) {

            log_d("createGmailService");

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

  com.google.api.services.gmail.Gmail.Builder builder = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential);
                    builder.setApplicationName(APPLICATION_NAME);
                    
com.google.api.services.gmail.Gmail service = builder.build();
    return service;
} // createGmailService


/**
 * getSelectedAccountName
 */
public String getSelectedAccountName() {
            log_d("getSelectedAccountName");
    return mCredential.getSelectedAccountName();
} // getSelectedAccountName


/**
 * newChooseAccountIntent
 */
public Intent newChooseAccountIntent() {
            log_d("newChooseAccountIntent");
    return mCredential.newChooseAccountIntent();
} // newChooseAccountIntent

/**
 * setSelectedAccountName
 */
public void setSelectedAccountName(String accountName) {

            log_d("setSelectedAccountName: " + accountName);

         if (TextUtils.isEmpty(accountName)) return;
               
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(PREF_ACCOUNT_NAME, accountName);
                    editor.apply();
                    mGmailAccountName = accountName;
                    mCredential.setSelectedAccountName(accountName);

} // setSelectedAccountName



/**
 * sendMailAsync
 */
public void sendMailAsync(String to_addr, String subject, String body, Callback callback) {

    log_d("sendMailAsync");

    mCallback = callback;

    try {
        mService = createGmailService(mCredential); 
        mMimeMessage = createEmail(to_addr, mGmailAccountName, subject, body);
    } catch (Exception e) {
                e.printStackTrace();
                notifyError( e.getMessage() );
    }

    mGmailRequestTask  =  new GmailRequestTask();
    mGmailRequestTask.execute();

} // sendMailAsync


    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    } // createEmail


    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public com.google.api.services.gmail.model.Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);


        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
               message.setRaw(encodedEmail);

        return message;
    } // createMessageWithEmail



    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
       public void sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
  

com.google.api.services.gmail.model.Message message = createMessageWithEmail(emailContent);
        
        service.users().messages().send(userId, message).execute();


        log_d("Message id: " + message.getId());
         log_d(message.toPrettyString());


    } // sendMessage



    /**
     * sendMail
     */
private void sendMail() throws Exception {
 sendMessage( mService, USER_ID_ME, mMimeMessage );
} // sendMail


    /**
     * notifyFinish
     */
    private void notifyFinish() {
                    log_d("notifyFinish");
           if ( mCallback != null ) {
            mCallback.onFinish();
        } 
}	// notifyFinish


    /**
     * notifyGooglePlayServicesAvailabilityIOException
     */
private void notifyGooglePlayServicesAvailabilityIOException(int code) {
                    log_d(" notifyGooglePlayServicesAvailabilityIOException");
           if ( mCallback != null ) {
            mCallback.onGooglePlayServicesAvailabilityIOException(code);
        } 
} //  notifyGooglePlayServicesAvailabilityIOException


    /**
     * notifyUserRecoverableAuthIOException
     */
private void notifyUserRecoverableAuthIOException(Intent intent) {
           if ( mCallback != null ) {
            mCallback.onUserRecoverableAuthIOException(intent);
        } 
} // notifyUserRecoverableAuthIOException


    /**
     * notifyError
     */
    private void notifyError(String error ) {
                    log_d("notifyError");
           if ( mCallback != null ) {
            mCallback.onError(error);
        } 
}	// notifyError


    /**
     * notifyStatus
     */
    private void notifyStatus(int status ) {
                    log_d("notifyStatus");
           if ( mCallback != null ) {
            mCallback.onStatus(status);
        } 
}	// notifyStatus



 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


    /**
     * == class GmailRequestTask ==
     */
    private class GmailRequestTask extends AsyncTask<Void, Void, Void> {



        private Exception mException = null;


    /**
     * == constractor ==
     */
        GmailRequestTask() {
            log_d("GmailRequestTask");
            /// nothing todo
        } // GmailRequestTask


        /**
     * == doInBackground ==
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(Void... params) {

                    log_d("doInBackground");
            try {
                sendMail();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
                cancel(true);
                return null;
            }

        } // doInBackground


 
    /**
     * == onPreExecute ==
     */
        @Override
        protected void onPreExecute() {
                    log_d("onPreExecute");
                notifyStatus(STATUS_PRE_EXECUTE);
        } // onPreExecute


    /**
     * == onPostExecute ==
     */
        @Override
        protected void onPostExecute(Void result) {

                    log_d("onPostExecute");
                notifyFinish();

        } // onPostExecute


    /**
     * == onCancelled ==
     */
        @Override
        protected void onCancelled() {

                    log_d("onCancelled");


            if (mException != null) {
                if (mException instanceof GooglePlayServicesAvailabilityIOException) {

                    GooglePlayServicesAvailabilityIOException exception = (GooglePlayServicesAvailabilityIOException) mException;

                    int code = exception.getConnectionStatusCode();

                    notifyGooglePlayServicesAvailabilityIOException(code);

                } else if (mException instanceof UserRecoverableAuthIOException) {
                              Intent intent = ((UserRecoverableAuthIOException) mException).getIntent();
                            notifyUserRecoverableAuthIOException(intent);

                } else if (mException instanceof GoogleAuthIOException) {
                        Throwable throwable =	mException.getCause(); 
                        String msg = throwable.getMessage();
                        notifyError(msg);

                } else {
                    notifyError( mException.getMessage() );
                }

            } else {
                notifyStatus(STATUS_CANCELLED);
            }

        } // onCancelled


    } // class GmailRequestTask


}  // class GmailSend
