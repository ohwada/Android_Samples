/** 
 *  Dialog Sample
 *  Yes No Dialog 
 *  2019-02-01 K.OHWADA
 */

package jp.ohwada.android.dialog1;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Layout;
import android.text.method.MovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
  * class YesNoDialog
  * reference : https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/app/AlertDialog.java
 */
public class YesNoDialog extends Dialog {

   // debug
    private static final  boolean D = true;
    private static final  String TAG = "Dialog";
    private static final  String TAG_SUB = "YesNoDialog";

    private static final  int THEME_DEFAULT = R.style.Theme_YesNoDialog;

    private static final double WIDTH_RATIO_DEFAULT = 0.95;

    private View  mContentView = null;

    private TextView   mTextViewTitle;

    private TextView   mTextViewMessage;

    private ImageView         mImageViewIcon;

    private Button mButtonYes;

    private Button mButtonNo;

    private DialogInterface mDialogInterface;

    private DialogInterface.OnClickListener  mListenerYes;

    private DialogInterface.OnClickListener mListenerNo;

    private int   mDisplayWidth;

    private int   mDisplayHeight;

    private int  mContentViewWidth = 0;


/**
  * constractor
 */
public YesNoDialog(Context context) {
        super(context, THEME_DEFAULT);
    log_d("constractor");
    initDialog();
} // YesNoDialog

/**
  * constractor
 */
public YesNoDialog(Context context, int themeResId) {
    super(context, themeResId);
    initDialog();
} // YesNoDialog

/**
  * onCreate
 */
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    log_d("onCreate");
    // unnecessary, because override view
    // create();
} // onCreate


/**
  * initDialog
 */	
private void initDialog() {
    log_d("initDialog");
        create();
        setupDisplayParam();
        // setWidthRatio( WIDTH_RATIO_DEFAULT );
        mDialogInterface = this;
} // initDialog


/**
  * create
 */	
public void create() {
    log_d("create");

    // prevent to override view
    if (mContentView != null ) return;

    mContentView = getLayoutInflater().inflate( R.layout.yes_no_dialog, null );

    setContentView( mContentView );

    mTextViewTitle = (TextView) findViewById( R.id.TextView_dialog_title );

    mTextViewMessage = (TextView) findViewById( R.id.TextView_dialog_message );

    mImageViewIcon = (ImageView) findViewById( R.id.ImageView_dialog_icon );

    mButtonYes = (Button) findViewById( R.id.Button_dialog_yes );
    mButtonYes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                notifyYesClick();
            }
        }); // mButtonYes


    mButtonNo = (Button) findViewById( R.id.Button_dialog_no );
    mButtonNo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                notifyNoClick();
            }
        }); // mButtonNo

} // create


/**
 * onWindowFocusChanged
  */ 
@Override
public void onWindowFocusChanged( boolean hasFocus ) {
        super.onWindowFocusChanged( hasFocus );
        if (  mContentView == null ) return;

    if ((  mContentViewWidth > 0)&&(  mContentViewWidth < mDisplayWidth) ) {
        // getWindow().setLayout( mContentViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT );
    }
} // onWindowFocusChanged


/**
 * setTitle
  */ 
public void setTitle(CharSequence title) {
        if (mTextViewTitle != null) {
            mTextViewTitle.setText(title);
        }
}

/**
 * setTitle
  */ 
public void setTitle(int resId) {
        if (mTextViewTitle != null) {
            log_d("setTitle");
            mTextViewTitle.setText(resId);
        }
}

/**
 * setMessage
  */ 
public void setMessage(CharSequence message) {
     if ( mTextViewMessage != null) {
             mTextViewMessage.setText(message);
        }
}

/**
 * setMessage
  */ 
public void setMessage(int resId) {
     if ( mTextViewMessage != null) {
            log_d("setMessage");
             mTextViewMessage.setText(resId);
        }
}

/**
 * setIcon
  */ 
public void setIcon(int resId) {

        if (mImageViewIcon != null) {
            if (resId != 0) {
                mImageViewIcon.setVisibility(View.VISIBLE);
                mImageViewIcon.setImageResource(resId);
            } else {
                mImageViewIcon.setVisibility(View.GONE);
            }
        }

} // setIcon

/**
 * setIcon
  */ 
public void setIcon(Drawable icon) {

        if (mImageViewIcon != null) {
            if (icon != null) {
                mImageViewIcon.setVisibility(View.VISIBLE);
                mImageViewIcon.setImageDrawable(icon);
            } else {
                mImageViewIcon.setVisibility(View.GONE);
            }
        }

} // setIcon

/**
 * setYesButton
  */ 
public void setYesButton(CharSequence text, OnClickListener listener) {
    if( mButtonYes != null ) {
        mButtonYes.setText(text);
        mButtonYes.setVisibility(View.VISIBLE);
    }
        mListenerYes = listener;
} // setYesButton

/**
 * setYesButton
  */ 
public void setYesButton(int textId, OnClickListener listener) {
    if( mButtonYes != null ) {
        log_d("setYesButton");
        mButtonYes.setText(textId);
        mButtonYes.setVisibility(View.VISIBLE);
    }
        mListenerYes = listener;
} // setYesButton

/**
 *setNoButton
  */ 
public void setNoButton(CharSequence text, OnClickListener listener) {
    if( mButtonNo != null ) {
        mButtonNo.setText(text);
        mButtonNo.setVisibility(View.VISIBLE);
    }
       mListenerNo = listener;
} // setNoButton

/**
 *setNoButton
  */ 
public void setNoButton(int textId, OnClickListener listener) {
    if( mButtonNo != null ) {
        log_d("setNoButton");
        mButtonNo.setText(textId);
        mButtonNo.setVisibility(View.VISIBLE);
    }
   mListenerNo = listener;
} // setNoButton


/**
  * setWidthRatio
 */ 
public void setWidthRatio( double ratio ) {
    if ((ratio > 0)&&(ratio < 1)) {
        int width = (int)(   mDisplayWidth * ratio );
        setLayout( width, ViewGroup.LayoutParams.WRAP_CONTENT );
    }
} // calcWidth

/**
  * setLayout
 */ 
private void setLayout( int width, int height ) {
    getWindow().setLayout( width, height );
} // setLayout

/**
 * setGravity
 */ 
public void setGravity( int gravity ) {
        getWindow().setGravity( gravity );
} // setGravity


/**
 * setBackgroundDrawable
 */ 
public void setBackgroundDrawable( Drawable drawable ) {
        getWindow().setBackgroundDrawable(drawable);
} // setBackground

/**
 * setBackgroundDrawableResource
 */ 
public void setBackgroundDrawableResource( int resId ) {
        getWindow().setBackgroundDrawableResource(resId);
} // setBackground


/**
 * setupDisplayParam
 */ 
private void setupDisplayParam() {
        WindowManager wm = (WindowManager)
            getContext().getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize( size );
          mDisplayWidth = size.x;
          mDisplayHeight = size.y;
} //  setupDisplayParam

/**
  * notifyYesClick
 */  
private void notifyYesClick() {
    if ( mListenerYes != null ) {
        mListenerYes.onClick(mDialogInterface, DialogInterface.BUTTON_POSITIVE);
    } 
    dismiss();

} // notifyYesClick

/**
  * notifyNoClick
 */  
private void notifyNoClick() {
    if (mListenerNo != null ) {
       mListenerNo.onClick(mDialogInterface, DialogInterface.BUTTON_NEGATIVE);
    }
    dismiss();

} // notifyYesClick

/**
 * log_d
 */
private void log_d( String str ) {
        if (D) Log.d( TAG, TAG_SUB + " " + str );
} // log_d

} // class YesNoDialog
