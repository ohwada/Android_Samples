/**
 * Wasp DB Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.waspdbsample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;
import net.rehacktive.waspdb.WaspListener;
import net.rehacktive.waspdb.WaspObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * class MainActivity
 * original : https://github.com/rehacktive/waspdb
 */
public class MainActivity extends ActionBarActivity {

// debug
    private final static boolean D = true; 
	private final static String TAG = "waspdb";
	private final static String TAG_SUB = "MainActivity";

    // WaspDb
	private final static String DB_NAME = "example";

	private final static String DB_PASSWORD = "password";

	private final static String HASH_NAME = "users";


    private WaspDb mDb;

    private WaspHash mHash;

    private WaspObserver mObserver;

    private UserAdapter mAdapter;


    private ListView mListView;

    private ProgressBar mProgressBar;


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button btnCreate = (Button) findViewById(R.id.Button_create);
         btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCreateDb();
            }
        }); // btnCreate


        Button btnAdd = (Button) findViewById(R.id.Button_add);
         btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        }); // btnAdd

        Button btnFlush = (Button) findViewById(R.id.Button_flush);
         btnFlush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flushUsers();
            }
        }); // btnFlush

        mListView = (ListView) findViewById(R.id.userlist);
        mAdapter = new UserAdapter(this);
        mListView.setAdapter(mAdapter);

    } // onCreate

/**
 * onDestroy
 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHash != null){
            mHash.unregister(mObserver);
        }
    } // onDestroy

/**
 * onCreateOptionsMenu
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // onCreateOptionsMenu



/**
 * onOptionsItemSelected
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id == R.id.action_settings) {
            toast_short(R.string.action_settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected

/**
 * openOrCreateDb
 */
private void openOrCreateDb() {
            mProgressBar.setVisibility(View.VISIBLE);

        String db_path = getFilesDir().getPath();
            WaspFactory.openOrCreateDatabase(db_path, DB_NAME, DB_PASSWORD, new WaspListener<WaspDb>() {
                @Override
                public void onDone(WaspDb waspDb) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    openDb(waspDb);
                }
            }); // WaspListener
} // openOrCreateDb


/**
 * openDb
 */
private void openDb(WaspDb waspDb) {
            log_d("openDb");
            toast_short("open DB");

                    mDb = waspDb;

                    mHash = mDb.openOrCreateHash(HASH_NAME);
                    updateListView();

                   mObserver = new WaspObserver() {
                       @Override
                       public void onChange() {
                           updateListView();
                       }
                   }; // WaspObserver
                    mHash.register(mObserver);
} // openDb

/**
 * updateListView
 */
private void updateListView() {
            log_d("updateListView");
            List<User> users = mHash.getAllValues();
            mAdapter.setUsers(users);
            mListView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mListView.invalidate();
} // updateListView



/**
 * addUser
 */
    private void addUser() {
        if (mHash == null){
            toast_short("please create DB");
            return;
        }
        User user = new User("user "+System.currentTimeMillis(), "");
        mHash.put(user.getUser_name(),user);
        toast_short("add user");
    } // addUser

/**
 * flushUsers
 */
    private void flushUsers() {
        if (mHash == null){
            toast_short("please create DB");
            return;
        }
        mHash.flush();
        toast_short("flush users");
    } // flushUsers

/**
 * toast_short
 */
	private void toast_short( int res_id ) {
        toast_short( getString(res_id) );
	} // toast_short


/**
 * toast_short
 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
