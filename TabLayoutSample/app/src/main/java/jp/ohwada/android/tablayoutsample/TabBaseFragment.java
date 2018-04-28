/**
 * TabLayout sample
 * 2018-03-01 K.OHWADA
 */

package jp.ohwada.android.tablayoutsample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;



/**
 * class TabBaseFragment
 */
public class TabBaseFragment extends Fragment {

    // debug
    protected String TAG_SUB = "TabBaseFragment";


    protected TextView mTextView;

    protected OnClickListener mListener;

    // page id
    protected int mPage;

    // content
    protected String mText;

    // button name
    protected String mButtonText;


/**
 *  interface OnClickListener
 */
    public interface OnClickListener {
        public void onClick(int page);
    } //  interface OnClickListener


/**
 * constractor
 */
    public TabBaseFragment(int page) {
        super();
        mPage = page;
        mButtonText = "Tab" + page;
        mText = "Fragment #" + page;
    } // TabBaseFragment


    /*
     * callback
     */ 
    public void setOnClickListener( OnClickListener listener ) {
        log_d("setOnClickListener");
        mListener = listener;
    } // setOnClickListener



/**
 * onCreateView
 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        log_d("onCreateView: " + mPage);
        View view = inflater.inflate(R.layout.fragment_tab_base, container, false);
        mTextView = (TextView) view.findViewById(R.id.TextView_tab);
        showText( mText );
        Button  btnTab = (Button) view.findViewById(R.id.Button_tab);
        btnTab.setText(mButtonText);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyClick(mPage);
            }
        }); //  btnTab.setOnClickListener

        return view;
    } // onCreateView


/**
 * showText
 */
    public void showText(String text) {
        if ( mTextView != null ) {
            mTextView.setText(text);
        }
        mText = text;
} // showText



/**
 * notifyClick
 */
    protected void notifyClick(int page) {
            log_d("notifyClick: " + page);
        if (mListener != null) {
            mListener.onClick(page);
        }
    } // notifyClick


 	/**
	 * write into logcat
	 */ 
	protected void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

 } // class FragmentTabPage
