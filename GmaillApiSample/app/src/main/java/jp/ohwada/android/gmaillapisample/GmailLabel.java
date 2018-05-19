/**
 * Gmail API Sample
 * get Gmail Label with Gmail API
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
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import com.google.api.services.gmail.model.*;

import android.Manifest;
import android.accounts.AccountManager;
 import android.preference.PreferenceManager;
 import android.support.v7.app.AppCompatActivity;
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
import android.text.method.ScrollingMovementMethod;
 import android.util.Log;
 import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * class GmailLabel
 * refarence : https://developers.google.com/gmail/api/quickstart/android
 */
public class GmailLabel {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "GMail";
    	private final static String TAG_SUB = "GmailLabel";


    public static final int STATUS_PRE_EXECUTE = 1;
    public static final int STATUS_CANCELLED = 2;


    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String APPLICATION_NAME = "Gmail API Sample";

    private static final String USER_ID_ME = "me";

// Gmail scopes
// https://developers.google.com/gmail/api/auth/scopes
// Create, read, update, and delete labels only.
    private static final String[] SCOPES = { GmailScopes.GMAIL_LABELS };

    private GmailRequestTask mGmailRequestTask;

        private SharedPreferences mSharedPreferences;

        private GoogleAccountCredential mCredential;

    private Callback     mCallback;


   /*
     * callback interface
     */ 
    public interface Callback {
        public void onFinish(List<String> list);
        public void onGooglePlayServicesAvailabilityIOException(int code);
        public void onUserRecoverableAuthIOException(Intent intent);
        public void onError(String error );
        public void onStatus(int status);
    } // interface


/**
 * constractor
 */
public GmailLabel(Context context) {

            log_d("GmailLabel");

        // Initialize credentials
        List<String> list__scope = Arrays.asList(SCOPES);
        mCredential = createCredential(context, list__scope);

        mSharedPreferences  = PreferenceManager.getDefaultSharedPreferences( context );

        String accountName = mSharedPreferences.getString(PREF_ACCOUNT_NAME, "");

        if (!TextUtils.isEmpty(accountName)) {
            log_d("accountName: " + accountName);
mCredential.setSelectedAccountName(accountName);
        }

} // GmailLabel


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
                  mCredential.setSelectedAccountName(accountName);

} // setSelectedAccountName



/**
 * getLabelsAsync
 */
public void getLabelsAsync(Callback callback) {

            log_d("getLabelsAsync");
    mCallback = callback;

com.google.api.services.gmail.Gmail service = createGmailService(mCredential);
    mGmailRequestTask =  new GmailRequestTask();
    mGmailRequestTask.setGmailService(service);
    mGmailRequestTask.execute();

} // getLabelsAsync


       /**
         * Fetch a list of Gmail labels attached to the specified account.
         * @return List of Strings labels.
         * @throws IOException
         */
       public List<String>getLabels( com.google.api.services.gmail.Gmail service ) throws Exception {

            log_d("getLabels");
            // Get the labels in the user's account.

            List<String> list = new ArrayList<String>();

            ListLabelsResponse listResponse =
                service.users().labels().list(USER_ID_ME).execute();

            for (Label label : listResponse.getLabels()) {
                list.add(label.getName());
            }
            return list;

        } //getLabels



    /**
     * notifyFinish
     */
    private void notifyFinish(List<String> list) {
                    log_d("notifyFinish");
           if ( mCallback != null ) {
            mCallback.onFinish(list);
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
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class GmailRequestTask extends AsyncTask<Void, Void, List<String>> {

        private com.google.api.services.gmail.Gmail mService = null;

        private Exception mException = null;


    /**
     * == constractor ==
     */
        GmailRequestTask() {
            log_d("GmailRequestTask");
            /// nothing todo
        } // GmailRequestTask


    /**
     * setGmailService
     */
public void setGmailService(com.google.api.services.gmail.Gmail service) {
            log_d("setGmailService");
mService = service;
} // setGmailService


        /**
     * == doInBackground ==
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {

                    log_d("doInBackground");
            try {
                return getLabels(mService);
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
        protected void onPostExecute(List<String> list) {

                    log_d("onPostExecute");
                notifyFinish(list);

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


}  // class GmailLabel
