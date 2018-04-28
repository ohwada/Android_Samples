/**
 * TabLayout sample
 * 2018-03-01 K.OHWADA
 */

package jp.ohwada.android.tablayoutsample;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * class TabPagerAdapter
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

// debug
    	private final static String TAG_SUB = "SamplePagerAdapter";


    private List<TitleFragment> mList = new ArrayList<TitleFragment>();


/**
 * constractor
 */
    public TabPagerAdapter (FragmentManager fm) {
        super(fm);
        log_d("SamplePagerAdapter");
    } //TabPagerAdapter


/**
 * getCount
 */
    @Override
    public int getCount() {
        log_d("getCount");
        return mList.size();
    } // getCount


/**
 * getItem
 */
    @Override
    public Fragment getItem(int position) {
        log_d("getItem: " + position);
        TitleFragment list = mList.get(position);
        return list.fragment;
    } // getItem



/**
 * getPageTitle
 */
    @Override
    public CharSequence getPageTitle(int position) {
        log_d("getPageTitle: " + position);
        TitleFragment list = mList.get(position);
        return (CharSequence)list.title;
    } // getPageTitle


/**
 * addTitleFragment
 */
public void addTitleFragment(String title, Fragment fragment) {
    mList.add( new TitleFragment(title, fragment));
} //  addTitleFragment


/**
 * class TitleFragment
 */
public class TitleFragment {

    public String title;
    public Fragment fragment;

/**
 * constractor
 */
    public TitleFragment(String _title, Fragment _fragment){
        this.title = _title;
        this.fragment = _fragment;
    } //TitleFragment

} // class TitleFragment


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // classTabPagerAdapter
