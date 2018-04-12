/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewsample3;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

    /*
     * class SampleHolder
    * reference https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html
     */
    public    class SampleHolder extends RecyclerView.ViewHolder {

    	// debug
    	private final static String TAG_SUB = "SampleHolder";

        private static final int LAYOUT_ID = R.layout.list_item;

//        final TextView textView;
        // public final TextView textView;
        private TextView mTextView;



    /*
     * == constractor ==
     */
        public SampleHolder(View itemView) {
            super(itemView);
            log_d("SampleHolder");
           mTextView = (TextView) itemView;
        } // SampleHolder



    /*
     * setText
     */
    public void setText(String text) { 
            log_d("setText");
            mTextView.setText( text );
} // setText


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class SampleHolder
