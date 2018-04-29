/**
 * livedoor Weather sample
 * 2018-03-01 K.OHWADA
 */

package jp.ohwada.android.livedoorweather;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import jp.ohwada.android.livedoorweather.model.*;


/**
 * class TabActivity
 */
public class TabActivity extends AppCompatActivity {

// debug
    	protected String TAG_SUB = "TabActivity";


        // Fragment
        protected OverviewFragment mOverviewFragment;
        protected ForecastFragment mForecastFragment;
        protected LocationFragment mLocationFragment;


/**
 * constractor
 */
public TabActivity() {
    super();
} // TabActivity



/**
 * == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log_d("onCreate");
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupFragment();
        setupTabLayout();

    } // onCreate



/**
 * setupToolbar
 */
protected void setupToolbar() {
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
} // setupToolbar


/**
 * setupFragment
 */
protected void setupFragment() {
        mOverviewFragment = new OverviewFragment(this);
        mOverviewFragment.setOnClickListener( new OverviewFragment.OnClickListener() {
            @Override
            public void onUpdateClick() {
                log_d( "onUpdateClick");
                procUpdateClick();
            }

        }); // OverviewFragment setOnClickListener

        mForecastFragment = new ForecastFragment(this);

        mLocationFragment = new LocationFragment(this);
        mLocationFragment.setOnClickListener( new LocationFragment.OnClickListener() {
            @Override
            public void onItemClick( PinpointLocation location ) {
                log_d( "location onItemClick");
                procLocationItemClick(location);
            }
        }); // LocationFragment setOnClickListener

} // setupFragment



/**
 * setupTabLayout
 */
protected void setupTabLayout() {
        
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);

       TabPagerAdapter adapter = new TabPagerAdapter( getSupportFragmentManager() );

        adapter.addTitleFragment("TAB1",  mOverviewFragment);
        adapter.addTitleFragment("TAB2",  mForecastFragment);
        adapter.addTitleFragment("TAB3",  mLocationFragment);

         viewPager.setAdapter(adapter);
        
        tabLayout.setupWithViewPager(viewPager);

    } // setupTabLayout



/**
	 *  procUpdateClick
	 */
    protected void procUpdateClick() {
        log_d("procUpdateClick");
    } // procUpdateClick



/**
	 * procLocationItemClick
	 */
    protected void procLocationItemClick( PinpointLocation location ) {
        log_d("procLocationItemClick");
    } // procLocationItemClick



 	/**
	 * write into logcat
	 */ 
	protected void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class TabActivity
