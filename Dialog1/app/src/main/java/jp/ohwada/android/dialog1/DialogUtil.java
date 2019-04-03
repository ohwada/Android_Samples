/** 
 *  Dialog Sample
 *  Yes No Dialog 
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.dialog1;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** 
 *  class DialogUtil
 */
public class DialogUtil  {

	private Context mContext;


/** 
 *  constractor
 */
public DialogUtil(Context context) {
    mContext = context;
} /// DialogUtil

/** 
 *  showStandardDialog
 */
public void showStandardDialog() {

    YesNoDialog dialog = 
        dialog = new YesNoDialog(mContext );

        dialog.setTitle( R.string.sample_title );
        dialog.setMessage( R.string.sample_message );
        dialog.setIcon( R.drawable.droid );

    dialog.setYesButton(R.string.button_yes, new  DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                toast_long(R.string.button_yes);
            }
        }); // setYesButton

    dialog.setNoButton(R.string.button_no, null );
    dialog.show();

} // showStandardDialog

/** 
 *  showSimpleDialog
 */
public void showSimpleDialog() {
    YesNoDialog dialog = getOkDialog();
    dialog.show();
} // showSimpleDialog

/** 
 *  getDialog
 */
private YesNoDialog getOkDialog() {

    YesNoDialog dialog = 
        dialog = new YesNoDialog(mContext );

        dialog.setTitle( R.string.sample_title );
        dialog.setMessage( R.string.sample_message );

        dialog.setYesButton(R.string.button_ok, null );

    return dialog;
} // getSimpleDialog


/** 
 *  showCustomWidthDialog
 */
public void showCustomWidthDialog() {
    YesNoDialog dialog = getOkDialog();
    dialog.setWidthRatio( 0.5 );
    dialog.show();

} // showCustomWidthDialog

/** 
 * showCustemGravityDialog
 */
public void showCustemGravityDialog() {
    YesNoDialog dialog = getOkDialog();
    dialog.setGravity( Gravity.BOTTOM );
    dialog.show();
} // showCustemGravityDialog

/** 
 *  showCustomBackgroundDialog
 */
public void showCustomBackgroundDialog() {
    YesNoDialog dialog = getOkDialog();
    dialog.setBackgroundDrawableResource( R.drawable.bg_yellow );
    dialog.show();
} // showCustomBacgroundDialog


/** 
 *  showTransparentBackgroundDialog
 */
public void showTransparentBackgroundDialog() {
    YesNoDialog dialog = getOkDialog();
    dialog.setBackgroundDrawableResource( R.drawable.bg_yellow_transparent );
    dialog.show();
} // showCustomBacgroundDialog


/** 
 *  showTransparentThemeDialog
 */
public void showTransparentThemeDialog() {
    YesNoDialog dialog = 
        dialog = new YesNoDialog(mContext, R.style.Theme_TransparentDialog );

    dialog.setTitle( R.string.sample_title );
    dialog.setMessage( R.string.sample_message );
    dialog.setYesButton(R.string.button_ok, null );

    dialog.show();

} // showCustomBacgroundDialog

   /**
 * toast_long
 */
private void toast_long( int res_id ) {
		ToastMaster.makeText( mContext, res_id, Toast.LENGTH_LONG ).show();
} // toast_long

   /**
 * toast_long
 */
private void toast_long( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_LONG ).show();
} // toast_long


}//  class DialogUtil
