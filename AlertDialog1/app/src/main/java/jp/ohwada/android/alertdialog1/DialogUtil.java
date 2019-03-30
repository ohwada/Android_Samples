/** 
 *  AlertDialog Sample
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.alertdialog1;


import android.app.AlertDialog;

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

    // list dialog
	private final static String ITEM1 = "Apple";
	private final static String ITEM2 = "Banana";
	private final static String ITEM3 = "Coconut";
	private final static String ITEM_CANCEL = "Cancel";

	private final static String LF = "\n";

	private Context mContext;

	private LayoutInflater mLayoutInflater;

	private AlertDialog mDialog;

/** 
 *  constractor
 */
public DialogUtil(Context context) {
    mContext = context;
mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
} /// DialogUtil


/** 
 *  showSimpleDialog
 */
public void showSimpleDialog() {
    new AlertDialog.Builder(mContext)
        .setTitle( R.string.sample_title )
        .setMessage( R.string.sample_message )
    .setPositiveButton(R.string.button_ok, null )
        .show();
} // showSimpleDialog

/** 
 *  showThreeButtonDialog
 */
public void showThreeButtonDialog() {
    new AlertDialog.Builder(mContext)
        .setTitle( "Three Buttons" )
        .setMessage( "select button" )

    .setPositiveButton(R.string.button_yes, new  DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                toast_long(R.string.button_yes);
            }
        }) // setPositiveButton

    .setNegativeButton(R.string.button_no, new  DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                toast_long(R.string.button_no);
            }
        }) // setNegativeButton

    .setNeutralButton(R.string.button_cancel, null )
        .show();

} // showThreeButtonDialog

/** 
 *  showCustomIconDialog
 */
public void showCustomIconDialog() {

new AlertDialog.Builder(mContext)
         .setTitle(R.string.sample_title )
        .setMessage( R.string.sample_message )
        .setIcon( R.drawable.droid )
        .setPositiveButton( R.string.button_ok, null ) 
        .show();
} // showCustomIconDialog

/** 
 *  showCustomTitleDialog
 */
public void showCustomTitleDialog() {

    TextView tvTitle = new TextView(mContext);
    tvTitle.setText(R.string.sample_title);
    tvTitle.setTextSize(20);
    tvTitle.setTextColor( Color.BLUE );
    tvTitle.setGravity( Gravity.CENTER_HORIZONTAL );

new AlertDialog.Builder(mContext)
         .setCustomTitle( tvTitle )
        .setMessage( R.string.sample_message )
        .setPositiveButton( R.string.button_ok, null ) 
        .show();
} // showCustomTitleDialog

/** 
 *  showCustomMessageDialog
 */
public void showCustomMessageDialog() {

    TextView tvMessage = new TextView(mContext);
    tvMessage.setText(R.string.sample_message);
    tvMessage.setTextColor( Color.YELLOW );
    tvMessage.setBackgroundColor( Color.GRAY );

new AlertDialog.Builder(mContext)
         .setTitle( R.string.sample_title )
        .setView( tvMessage )
        .setPositiveButton( R.string.button_ok, null ) 
        .show();
} // showCustomMessageDialog


/** 
 * showCustomLayoutDialog
 */
public void showCustomLayoutDialog() {

    View view = mLayoutInflater.inflate( R.layout.custom_dialog, null );

       TextView tvTitle = (TextView) view.findViewById(R.id.TextView_dialog_title);
        tvTitle.setText(R.string.sample_title);

       TextView tvMessage = (TextView) view.findViewById(R.id.TextView_dialog_message);
        tvMessage.setText(R.string.sample_message);

        Button btnPositive = (Button) view.findViewById(R.id.Button_dialog_positive);
        btnPositive.setText(R.string.button_ok);
    btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast_long(R.string.button_ok);
                mDialog.dismiss();
            }
        }); // btnSubmit

mDialog =    new AlertDialog.Builder(mContext)
        .setView( view )
        .create();
    mDialog.show();

} // showCustomLayoutDialog


/** 
 *  showIListDialog
 */
public void showListDialog() {

      AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        // set title
        builder.setTitle( "select Item" );

        // set dialog message
        builder.setItems(new CharSequence[]{
                        ITEM1,
                        ITEM2,
                        ITEM3,
                        ITEM_CANCEL
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                toast_long(ITEM1);
                                break;
                            case 1:
                                toast_long(ITEM2);
                                break;
                            case 2:
                                toast_long(ITEM3);
                                break;
                            default:
                                dialog.dismiss();
                                toast_long(ITEM_CANCEL);
                                break;
                        } // switch
                    } // onClick
                } // OnClickListener
        ); // setItems

        // show it
        AlertDialog dialog = builder.create();
        dialog.show();

} // showIListDialog

/** 
 * showImageDialog
 */
public void showImageDialog() {

    ImageView imageView = new ImageView(mContext);
    imageView.setImageResource( R.drawable.palau );

    new AlertDialog.Builder(mContext)
         .setTitle( "Palau" )
        .setView(  imageView )
        .setPositiveButton( R.string.button_ok, null ) 
        .show();
} // showImageDialog

/** 
 *  showInputDialog
 */
public void showInputDialog() {

    View view = mLayoutInflater.inflate( R.layout.input_dialog, null );

   final EditText editName = (EditText) view.findViewById(R.id.EditText_dialog_name);
   final EditText editAge = (EditText) view.findViewById(R.id.EditText_dialog_age);

        Button btnSubmit = (Button) view.findViewById(R.id.Button_dialog_submit);
         btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =editName.getText().toString();
                String age =editAge.getText().toString();
                if (name.length() > 0 ) {
                    toast_long( name + LF + age );
                } else {
                    toast_long( "please enter Name" );
                }
            }
        }); // btnSubmit

new AlertDialog.Builder(mContext)
        .setTitle( "enter Name and Age" )
        .setView( view )
        .setPositiveButton(R.string.button_cancel, null )
        .show();

} // showInputDialog


/** 
 * showCustomButtonDialog
 */
public void showCustomButtonDialog() {

    V7DialogUtil util = new V7DialogUtil(mContext);
    util.showCustomButtonDialog();

} // showCustomButtonDialog



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
