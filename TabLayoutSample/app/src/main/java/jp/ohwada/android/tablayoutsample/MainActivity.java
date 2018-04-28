/**
 * TabLayout sample
 * 2018-03-01 K.OHWADA
 */

package jp.ohwada.android.tablayoutsample;

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
import android.widget.Toast;

/**
 * class MainActivity
 */
public class MainActivity extends AppCompatActivity {

// debug
	    private  final static boolean D = Constant.DEBUG; 
    	private final static String TAG_SUB = "MainActivity";


        // Fragment
        private Tab1Fragment mTab1Fragment;
        private Tab2Fragment mTab2Fragment;
        private Tab3Fragment mTab3Fragment;

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
 * == onCreateOptionsMenu ==
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // onCreateOptionsMenu


/**
 * == onOptionsItemSelected ==
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            toast_short( "settings" );
            return true;
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected


/**
 * setupToolbar
 */
private void setupToolbar() {
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
} // setupToolbar

/**
 * setupFragment
 */
private void setupFragment() {
        mTab1Fragment = new Tab1Fragment();
        mTab1Fragment.setOnClickListener( new TabBaseFragment.OnClickListener() {
            @Override
            public void onClick( int page ) {
                log_d( "Tab1 onClick");
                procClick(page);
            }
        }); // Tab1Fragment setOnClickListener

        mTab2Fragment = new Tab2Fragment();
        mTab2Fragment.setOnClickListener( new TabBaseFragment.OnClickListener() {
            @Override
            public void onClick( int page ) {
                log_d( "Tab2 onClick");
                procClick(page);
            }
        }); // Tab2Fragment setOnClickListener

        mTab3Fragment = new Tab3Fragment();
        mTab3Fragment.setOnClickListener( new TabBaseFragment.OnClickListener() {
            @Override
            public void onClick( int page ) {
                log_d( "Tab3 onClick");
                procClick(page);
            }
        }); // Tab3Fragment setOnClickListener

} // Fragment


/**
 * setupTabLayout
 */
private void setupTabLayout() {
        
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);

       TabPagerAdapter adapter = new TabPagerAdapter( getSupportFragmentManager() );

        adapter.addTitleFragment("TAB1",  mTab1Fragment);
        adapter.addTitleFragment("TAB2",  mTab2Fragment);
        adapter.addTitleFragment("TAB3",  mTab3Fragment);

         viewPager.setAdapter(adapter);
        
        tabLayout.setupWithViewPager(viewPager);

    } // setupTabLayout


/**
	 * procClick
	 */
    private void procClick(int page) {
        String msg = "TAB" + page + " clicked";
        log_d(msg);
        toast_short(msg);
        mTab1Fragment.showText(msg);
        mTab2Fragment.showText(msg);
        mTab3Fragment.showText(msg);
    } // onClick


/**
	 * toast_short
	 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
