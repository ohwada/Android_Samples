/**
 * font sample
 * 2017-06-01 K.OHWADA 
 */
 
 package jp.ohwada.android.fontsample4;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * MainActivity
 */
public class MainActivity extends Activity {
	
    	// debug
    	private final static String TAG_SUB = "MainActivity";
    	
    	    		private String DIR_FONT = "fonts";
    	    		
	private static final float TEXT_SIZE = 24; 
private static final String DEFAULT_LABEL = "default font";

	private static final String DOT = ".";
	private static final String EXT_TTF = "ttf";
private static final String EXT_ZIP = "zip";

// http://www001.upp.so-net.ne.jp/mikachan/
	private final  String FONT_NAME_1 = "mikachan";
private String FONT_JP_1 = "" ;
private String FONT_ZIP_1 = "" ;

// http://www.forest.impress.co.jp/library/software/aoyagifont/
	private final  String FONT_NAME_2 = "kouzanmouhitu";
private String FONT_JP_2 = "" ;
private String FONT_ZIP_2 = ""  ;

	private TextView mTextView_2;
	private TextView mTextView_4;
		private TextView mTextView_6;
		
		private FontFile mFontFile;
			
	/**
	 * === onCreate ===
	 */	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
 	FONT_JP_1 = FONT_NAME_1 + DOT +  EXT_TTF;
	FONT_ZIP_1 = FONT_NAME_1 + DOT + EXT_ZIP ;
	FONT_JP_2 = FONT_NAME_2 + DOT + EXT_TTF ;
	 FONT_ZIP_2 = FONT_NAME_2 + DOT + EXT_ZIP ;  
	 
        		TextView tv1 = (TextView) findViewById( R.id.TextView_1 );
        			tv1.setText( DEFAULT_LABEL );
           		mTextView_2 = (TextView) findViewById( R.id.TextView_2 );
           		        		TextView tv3  = (TextView) findViewById( R.id.TextView_3 );
           		       tv3.setText( FONT_NAME_1 );
           		        		mTextView_4 = (TextView) findViewById( R.id.TextView_4 ); 
         TextView tv5  = (TextView) findViewById( R.id.TextView_5 );
           		       tv5.setText( FONT_NAME_2 );
           		        		mTextView_6 = (TextView) findViewById( R.id.TextView_6 ); 
           		        		
           		       	        Button btnInstall = (Button) findViewById( R.id.Button_install );
				btnInstall.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procClickButtonInstall();
			}
		});    
		
		           		mFontFile = new FontFile( this );

    }
    
    
        	/**
	 * clickButtonInstall
	 */	
    private void procClickButtonInstall() {
    	
    	mFontFile.mkdirsPrivate( DIR_FONT );
    				mFontFile.unzipPrivate( FONT_ZIP_1 );
	    				mFontFile.unzipPrivate( FONT_ZIP_2 );
	    					
	    					showFont();
	
} // clickButtonInstall  


	/**
	 * sshowFont
	 */ 
	private void showFont() {	

    	mTextView_2.setText( R.string.sample_text );
    	    	mTextView_4.setText( R.string.sample_text );
    	    	    	mTextView_6.setText( R.string.sample_text );
    	    	    	
    	    	    	Typeface typeface1 = mFontFile.getPrivateTypeface( FONT_JP_1  );
    	    	    	    		mTextView_4.setTypeface( typeface1 );
    	    	    	    		
	    	    	    	Typeface typeface2 = mFontFile.getPrivateTypeface( FONT_JP_2  );
    	    	    	    		mTextView_4.setTypeface( typeface2 );

}  // howFont
	


	
 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d


	} // MainActivity		
	  

