/**
 * RecycleView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recycleviewsample1;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
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
     * class MainActivity
     */ 
public class MainActivity extends Activity {

   	// debug
    	private final static String TAG_SUB = "MainActivity";

    private RecycleAdapter mAdapter;

private RecyclerView mRecyclerView;

    /*
     * == onCreate ==
     */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
log_d( "onCreate");
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView_1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

      mAdapter = new RecycleAdapter(AndroidVersion.getListCodename());
        mAdapter.setOnItemClickListener( new RecycleAdapter.OnItemClickListener() {
        @Override
        public void onItemClick( int position, String codename ){
log_d( "onItemClick");
toast_short( codename );
}
}); // setOnItemClickListener

        mRecyclerView.setAdapter( mAdapter);

    } //onCreate


/**
	 * toast_short
	 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_shor


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity

