/**
 * RSS Sample
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.rsssample;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * YesNo Dialog
 */
public class EntryDialog  extends Dialog {

   // debug
	private  final static boolean D = true; 
	private  final static String TAG = "RSS";
    private  final static String TAG_SUB = "EntryDialog" ;
    
    private  final static float IMAGE_WIDTH_RATIO = 0.95f;

    private ImageView mImageView;

    private TextView mTextViewTitle;
    private TextView mTextViewDate;
    private TextView mTextViewDescription;
 
    // screen sze
    private Point mDisplaySize;


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
     * @ param Context context
     */ 	
    public EntryDialog( Context context ) {
                super( context ); 
                initView();
    } // EntryDialog

    /**
     * === Constructor ===
     * @ param Context context
     * @ param int theme
     */ 
    public EntryDialog( Context context, int theme ) {
        super( context, theme ); 
                        initView();
    } // EntryDialog


    /**
     * setImageUrl
     * @ param String url
     */ 
    public void setImageUrl( String imageUrl, int imageWidth, int imageHeight ) {
        
        log_d( "setImageUrl: " + imageUrl +" , "+ imageWidth +" , "+ imageHeight );

        float ratio = 1f;
        if ((imageHeight > 0)&&(imageWidth > 0)) {
            ratio = (float)imageHeight / (float)imageWidth;
        }

            int targetWidth = (int)(  IMAGE_WIDTH_RATIO * mDisplaySize.x );
            int targetHeight = (int)( ratio * targetWidth );

            Picasso.with( getContext() ).load( imageUrl ).resize( targetWidth, targetHeight ).into( mImageView );
    } // setImageUrl
    

     /**
     * setDescription
     * @ param String str
     */ 
    public void setDescription( String str ) {
        mTextViewDescription.setText(str);
    } // setDescription

    /**
     *  setContentTtitle
     * @ param String str
     */ 
    public void setContentTtitle( String str ) {
        mTextViewTitle.setText(str);
    } //  setContentTtitle

   
    /**
     * setDate
     * @ param String str
     */ 
    public void setDate( String str ) {
        mTextViewDate.setText(str);
    } // setDate
    

    /**
     * initView
     */	
    private void  initView() {
        log_d(" initView");

        setContentView( R.layout.dialog_entry);
        
        mImageView = (ImageView) findViewById( R.id.ImageView_dialog_image );
        mTextViewTitle = (TextView) findViewById( R.id.TextView_dialog_title );
        mTextViewDate = (TextView) findViewById( R.id.TextView_dialog_date );
        mTextViewDescription = (TextView) findViewById( R.id.TextView_dialog_description );
        

        Button btnYes = (Button) findViewById( R.id.Button_dialog_yes );
        btnYes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                log_d("onClick Yes");
                notifyClickYes();
            }
        }); // btnYes
        

                Button btnNo = (Button) findViewById( R.id.Button_dialog_no );
        btnNo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                log_d("onClick No");
                notifyClickNo();
            }
        }); // btnNo


        initDisplayParam();
        setLayoutDisplaySize();

    } // initView



    /**
     * initDisplayParam
     */ 
    private void initDisplayParam() {
        WindowManager wm = (WindowManager)
            getContext().getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize( size );
        mDisplaySize = size;
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
     } // initDisplayParam


    /**
     * setLayoutDisplaySize
     */
    private void setLayoutDisplaySize() {
       int width = (int) mDisplaySize.x;
              int height = (int) mDisplaySize.y;
              log_d(" setLayoutDisplaySize " + width + " x " + height );
        getWindow().setLayout( width, height);
    } // setLayoutDisplaySize


    /**
     * setNoTitle
     */
     private  void setNoTitle() {
    requestWindowFeature( Window.FEATURE_NO_TITLE );
    } // setNotitle

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
