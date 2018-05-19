/**
 * Gmail API Sample
 * 2018-05-01 K.OHWADA
 */


package jp.ohwada.android.gmaillapisample;



import android.Manifest;
import android.accounts.AccountManager;
import android.support.v7.app.AppCompatActivity;
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
 import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * class MainActivity
 * refarence : https://developers.google.com/gmail/api/quickstart/android
 */
public class MainActivity extends AppCompatActivity
    implements EasyPermissions.PermissionCallbacks {


        // debug
	private final static boolean D = true;
    	private final static String TAG = "GMail";
    	private final static String TAG_SUB = "MainActivity";

    private static final String LF = "\n";


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    static final int MODE_LABEL = 1;
    static final int MODE_SEND = 2;

   
// mail content
                private final static String MAIL_TO = "iot.ohwada@gmail.com";

                private final static String SUBJECT = "TEST1";

                private final static String BODY = "test from Android with GMAIL API";

    private TextView mTextViewOutput;

       private   Button  mButtonCall;

    private ProgressDialog mProgress;

    private GmailLabel mGmailLabel;

    private GmailSend mGmailSend;

    private int mMode = 0;

    /**
     * == onCreate ==
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 mTextViewOutput = (TextView) findViewById(R.id.TextView_output);
        mTextViewOutput.setVerticalScrollBarEnabled(true);
        mTextViewOutput.setMovementMethod(new ScrollingMovementMethod());


        mButtonCall = (Button) findViewById(R.id.Button_call);
        mButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonCall.setEnabled(false);
                mTextViewOutput.setText("");
                mMode = MODE_LABEL;
                callGmailApi();
                mButtonCall.setEnabled(true);
            }
        }); //  mButtonCall


        Button btnSend = (Button) findViewById(R.id.Button_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextViewOutput.setText("");
                mMode = MODE_SEND;
                callSendMail();
            }
        }); //  btnSend


        mProgress = new ProgressDialog(this);
        mProgress.setMessage( getString(R.string.progress) );


        mGmailLabel = new GmailLabel(this);

        mGmailSend = new GmailSend(this);

    } // onCreate

/**
 * == onPause ==
 */
       @Override
    protected void onPause() {
    super.onPause();
   cancelTask();
} // onPause

/**
 * == onDestroy ==
 */
       @Override
    protected void onDestroy() {
    super.onDestroy();
    cancelTask();
} // onDestroy


/**
 * cancelTask
 */
    private void cancelTask() {
    if (mProgress != null) {
        mProgress.hide();
    }
    if (mGmailLabel != null) {
        mGmailLabel.cancelTask();
    }
    if (mGmailSend != null) {
        mGmailSend.cancelTask();
    }
} // cancelTask


    /**
     * == onActivityResult ==
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log_d("onActivityResult");

        switch(requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:
                procActivityResultGooglePlayService(resultCode);
                break;

            case REQUEST_ACCOUNT_PICKER:
                procActivityResultAccountPicker(resultCode, data);
                break;

            case REQUEST_AUTHORIZATION:
                procActivityResultAuthorization(resultCode);
                break;
        }

    } // onActivityResult


/**
 * procActivityResultGooglePlayService
 */
private void procActivityResultGooglePlayService(int resultCode) {
        log_d(" procActivityResultGooglePlayService");
                if (resultCode == RESULT_OK) {
                        if (mMode == MODE_LABEL ) {
                            callGmailApi();
                        } else if (mMode == MODE_SEND ) {
                            callSendMail();
                        }
                } else {
                    mTextViewOutput.setText(R.string.please_install);
                }
} // procActivityResultGooglePlayService


/**
 * procActivityResultAccountPicker
 */
private void procActivityResultAccountPicker(int resultCode, Intent data) {

        log_d("procActivityResultAccountPicker");
        // if (resultCode == RESULT_OK && data != null &&
                        // data.getExtras() != null)) {

        if ((resultCode != RESULT_OK) || (data == null )||
                        (data.getExtras() == null)) {
                            log_d("AccountPicker result invalid");
                            return;
                        }

                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (TextUtils.isEmpty(accountName)) {
                            log_d("accountName empty");
                            return;
                    }

            if (mMode == MODE_LABEL) {
                        mGmailLabel.setSelectedAccountName(accountName);
                        callGmailApi();
                } else if (mMode == MODE_SEND) {
                        mGmailSend.setSelectedAccountName(accountName);
                        callSendMail();
                }

} // procActivityResultAccountPicker


/**
 * procActivityResultAuthorization
 */
private void procActivityResultAuthorization(int resultCode) {

    log_d("procActivityResultAuthorization");
                if (resultCode == RESULT_OK) {
                        if (mMode == MODE_LABEL ) {
                            callGmailApi();
                        } else if (mMode == MODE_SEND ) {
                            callSendMail();
                        }
                }

} // procActivityResultAuthorization



    /**
     * == onRequestPermissionsResult ==
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

log_d("onRequestPermissionsResult");

        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);

    } // onRequestPermissionsResult



    /**
     * == onPermissionsGranted ==
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        log_d("onPermissionsGranted");
        // Do nothing.
    } // onPermissionsGranted


    /**
     * == onPermissionsDenied ==
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        log_d("onPermissionsDenied");
        // Do nothing.
    } // onPermissionsDenied


    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {

        log_d("isDeviceOnline");
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    } // isDeviceOnline


    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {

        log_d("isGooglePlayServicesAvailable");
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return ( connectionStatusCode == ConnectionResult.SUCCESS );

    }  // isGooglePlayServicesAvailable


    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {

        log_d("acquireGooglePlayServices");
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }

    } // acquireGooglePlayServices


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();

    } // showGooglePlayServicesAvailabilityErrorDialog


    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {

        if ( ! hasAcountPermissions() ) {
            // Request the GET_ACCOUNTS permission via a user dialog
            requestAcountPermissions();
            return;
        }

         if (mMode == MODE_LABEL ) {
                chooseAccountLabel();

        } else if (mMode == MODE_SEND ) {
                chooseAccountSend();

        }

    } // chooseAccount


/**
 *  chooseAccountLabel
 */
    private void chooseAccountLabel() {

            String accountName = mGmailLabel.getSelectedAccountName();
                if (TextUtils.isEmpty(accountName)) {

                        // Start a dialog from which the user can choose an account
                        Intent intent = mGmailLabel.newChooseAccountIntent();
                        startActivityAccountPicker(intent);

                } else {
                        callGmailApi();

                }

} // chooseAccountLabel


/**
 *  chooseAccountSend
 */
    private void chooseAccountSend() {

            String accountName = mGmailSend.getSelectedAccountName();
                if (TextUtils.isEmpty(accountName)) {

                        // Start a dialog from which the user can choose an account
                        Intent intent = mGmailSend.newChooseAccountIntent();
                        startActivityAccountPicker(intent);

                } else {
                        callSendMail();

                }

} // chooseAccountSend


/**
 * hasAcountPermissions
 */
private boolean hasAcountPermissions() {
      return EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS);
} // hasAcountPermissions


/**
 * requestAcountPermissions
 * Request the GET_ACCOUNTS permission via a user dialog
 */
private void requestAcountPermissions() {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.request_permission),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
} // requestAcountPermissions


/**
 * startActivityAccountPicker
 * Start a dialog from which the user can choose an account
 */
private void startActivityAccountPicker(Intent intent) {
                startActivityForResult(
                        intent,
                        REQUEST_ACCOUNT_PICKER);
} // startActivityAccountPicker


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void callGmailApi() {

log_d("callGmailApi");

String accountName = mGmailLabel.getSelectedAccountName();

        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();

        } else if (TextUtils.isEmpty(accountName)) {
            chooseAccount();

        } else if (! isDeviceOnline()) {
            mTextViewOutput.setText(R.string.no_network);

        } else {
            getGmailLabels();
        }
    } // callGmailApi

/**
 * getGmailLabels
 */
private void getGmailLabels() {

log_d("getGmailLabels");

            mGmailLabel.getLabelsAsync( new GmailLabel.Callback() {

            @Override
            public void onFinish(List<String> list) {
                log_d( "onFinish");
                mProgress.hide();
                procFinish(list);
            }

            @Override
        public void onGooglePlayServicesAvailabilityIOException(int code) {
                log_d( "onGooglePlayServicesAvailabilityIOException: " +code);
            mProgress.hide();
            showGooglePlayServicesAvailabilityErrorDialog(code);
}

        @Override
        public void onUserRecoverableAuthIOException(Intent intent) {
                log_d( "onUserRecoverableAuthIOException");
            mProgress.hide();
            startActivityAuth(intent);
        }

            @Override
            public void onError(String error) {
                log_d( "onError: " +error);
                mProgress.hide();
                procError(error);
            }

            @Override
        public void onStatus(int status) {
                log_d( "onStatus: " + status);
            procStatus(status);
        }


        }); // getMailAsync

} // getGmailLabels


/**
 *  procFinish
 */
            private void procFinish(List<String> list) {
                log_d( "procFinish");
            if (list == null || list.size() == 0) {
                String msg = getString(R.string.no_result);
                mTextViewOutput.setText(msg);
                log_d(msg);
                toast_short(msg);

            } else {
                list.add(0, "Data retrieved using the Gmail API:");
                mTextViewOutput.setText( TextUtils.join(LF, list) );
                toast_short("get labels");

            }

} //  procFinish

/**
 *  startActivityAuth
 */
private void startActivityAuth(Intent intent) {
                log_d( "startActivityAuth");
              startActivityForResult(
intent, REQUEST_AUTHORIZATION);
} // startActivityAuth


/**
 *  procError
 */
private void procError(String error) {
        String text = getString(R.string.error_occurred) +LF;
                            text += error +LF;
               mTextViewOutput.setText(text);
                log_d(text);
                toast_short(error);
            } // procError

/**
 * procStatus
 */
        private void procStatus(int status) {
                log_d( "procStatus: " + status);
if (status == GmailLabel.STATUS_PRE_EXECUTE) {
            mTextViewOutput.setText("");
            mProgress.show();

} else if (status == GmailLabel.STATUS_CANCELLED) {
            mProgress.hide();
            String msg = getString(R.string.msg_cancelled);
            mTextViewOutput.setText(msg);
                toast_short(msg);

}

        } // procStatus


/**
 * callSendMail
 */
    private void callSendMail() {

log_d("callSendMail");

String accountName = mGmailSend.getSelectedAccountName();

        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();

        } else if (TextUtils.isEmpty(accountName)) {
            chooseAccount();

        } else if (! isDeviceOnline()) {
            mTextViewOutput.setText(R.string.no_network);

        } else {
            sendMail();
        }

    } // callSendMail

/**
 * sendMail
 */
private void sendMail() {

log_d("sendMail");

            mGmailSend.sendMailAsync(MAIL_TO, SUBJECT, BODY,  new GmailSend.Callback() {

            @Override
            public void onFinish() {
                log_d( "onFinish");
                mProgress.hide();
                toast_short("send mail finished");
            }

            @Override
        public void onGooglePlayServicesAvailabilityIOException(int code) {
                log_d( "onGooglePlayServicesAvailabilityIOException: " +code);
            mProgress.hide();
            showGooglePlayServicesAvailabilityErrorDialog(code);
}

        @Override
        public void onUserRecoverableAuthIOException(Intent intent) {
                log_d( "onUserRecoverableAuthIOException");
            mProgress.hide();
            startActivityAuth(intent);
        }

            @Override
            public void onError(String error) {
                log_d( "onError: " +error);
                mProgress.hide();
                procError(error);
            }

            @Override
        public void onStatus(int status) {
                log_d( "onStatus: " + status);
            procStatus(status);
        }

        }); // sendMailAsync

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
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

}  // class MainActivity
