/** 
 *  AlertDialog Sample
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.alertdialog1;

// must support.v7
import android.support.v7.app.AlertDialog;

import android.content.Context;


/** 
 *  class V7DialogUtil
 */
public class V7DialogUtil  {

	private Context mContext;


/** 
 *  constractor
 */
public V7DialogUtil(Context context) {
    mContext = context;
} // V7DialogUtil

/** 
  * showCustomButtonDialog
 * require : android.support.v7.app.AlertDialog;
 */
public void showCustomButtonDialog() {

    new AlertDialog.Builder(mContext, R.style.Theme_CustonButtonDialog )
        .setTitle( R.string.sample_title )
        .setMessage( R.string.sample_message )
        .setPositiveButton( R.string.button_ok, null ) 
        .show();

} // showCustomButtonDialog


}//  class V7DialogUtil
