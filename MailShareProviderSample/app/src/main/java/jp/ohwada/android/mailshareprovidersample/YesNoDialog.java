
package jp.ohwada.android.mailshareprovidersample;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * YesNo Dialog
 */
public class YesNoDialog  extends Dialog {

   // debug
	protected  final static boolean D = true; 
	protected String TAG = "Share";
    private String TAG_SUB = "YesNoDialog" ;
    
 private TextView mTextViewMessage;
 
   // callback 
    private OnChangedListener mListener;  

    /*
     * callback interface
     */    
    public interface OnChangedListener {
        public void  onClickYes();
        public void onClickNo();
    }

    /*
     * callback
     */ 
    public void setOnChangedListener( OnChangedListener listener ) {
        mListener = listener;
    }

    /**
     * === Constructor ===
     * @param Context context
     */ 	
    public YesNoDialog( Context context ) {
                super( context ); 
                intView();
    } // PermissionDialog

    /**
     * === Constructor ===
     * @param Context context
     * @param int theme
     */ 
    public YesNoDialog( Context context, int theme ) {
        super( context, theme ); 
                        intView();
    } // PermissionDialog


    /**
     * setMessage
     * @param String str
     */ 
    public void setMessage( int res_id ) {
        mTextViewMessage.setText( res_id );
    } // setMessage
    
    
    /**
     * setMessage
     * @param String str
     */ 
    public void setMessage( String str ) {
        mTextViewMessage.setText(str);
    } // setMessage
    
    /**
     * intView
     */	
    private void                 intView() {
        log_d(" intView");

        setContentView( R.layout.dialog_yes_no);
        
        mTextViewMessage = (TextView) findViewById( R.id.TextView_dialog_message );
        
        Button btnYes = (Button) findViewById( R.id.Button_dialog_yes );
        btnYes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                notifyClickYes();
            }
        });
        
                Button btnNo = (Button) findViewById( R.id.Button_dialog_yes );
        btnNo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                notifyClickYes();
            }
        });


    } // intView







    /**
     * notifyClickYes
     */
    private void notifyClickYes() {
        if ( mListener != null ) {
            mListener.onClickYes();
        }
    } // notifyClickYes


    /**
     * notifyClickNo
     */
    private void notifyClickNo() {
        if ( mListener != null ) {
            mListener.onClickNo();
        }
    } // notifyClickNo
    
    
    /**
     * log_d
     */
    protected void log_d( String str ) {
        if (D) Log.d( TAG, TAG_SUB + " " + str );
    } // log_d
    
}
