/** 
 *  Preference Sample
 *  2019-02-01 K.OHWADA
 */


package jp.ohwada.android.preference1;


import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 *  class  AppCompatPreferenceActivity
 * A {@link android.preference.PreferenceActivity} which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
public abstract class AppCompatPreferenceActivity extends PreferenceActivity {

    // debug
	protected final static boolean D = true;
    protected final static String TAG = "AppCompatPreferenceActivity";
    protected final static String TAG_BASE = "PreferenceFragmentBase";


    private AppCompatDelegate mDelegate;

/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log_base("onCreate");
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
    }


/** 
 *  onPostCreate
 */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }


/** 
 *  getMenuInflater
 */
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }


/** 
 *  setContentView
 */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }


/** 
 *  setContentView
 */
    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }


/** 
 *  setContentView
 */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }


/** 
 *  setContentView
 */
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }


/** 
 *  onPostResume
 */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }


/** 
 *  onTitleChanged
 */
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }


/** 
 *  onConfigurationChanged
 */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }


/** 
 *  onStop
 */
    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }


/** 
 *  onDestroy
 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }


/** 
 *  getSupportActionBar
 */
    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }


/** 
 *  setSupportActionBar
 */
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }


/** 
 *  invalidateOptionsMenu
 */
    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }


/** 
 *  getDelegate
 */
    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }


/**
 * write into logcat
 */ 
 protected static void log_base( String msg ) {
	    if (D) Log.d( TAG, TAG_BASE + " " + msg );
} // log_base


}//  class  AppCompatPreferenceActivity

