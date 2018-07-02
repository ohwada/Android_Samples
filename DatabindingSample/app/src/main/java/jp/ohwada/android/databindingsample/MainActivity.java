/**
 * Databinding Sample
 * 2018-05-01 K.OHWADA
 */
package jp.ohwada.android.databindingsample;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import jp.ohwada.android.databindingsample.databinding.ActivityMainBinding;



/**
 * class MainActivity
 * original : https://qiita.com/t_sakaguchi/items/a83910a990e64f4dbdf1
 */
public class MainActivity extends AppCompatActivity implements SampleEventHandlers {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "databinding";
    	private final static String TAG_SUB = "MainActivity";

    	private final static String USER_NAME_1 = "Taro";

    	private final static String USER_NAME_2 = "Jiro";

    private ActivityMainBinding mBinding;

       private  User mUser;


/**
 * onCreate
 */
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    mBinding.setHandlers(this);

        Button btnBind = (Button) findViewById(R.id.Button_bind);
        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_d("onClick");
                bindView();
            }
        }); // btnBind


} // onCreate


 	/**
	 * onChangeClick
	 */ 
    @Override
    public void onChangeClick(View view) {
        log_d("onChangeClick");
        mUser.setName(USER_NAME_2);
    } // onChangeClick


 	/**
	 * bindView
	 */ 
private void bindView() {
    log_d("bindView");
    mUser = new User(USER_NAME_1);
    mBinding.setUser(mUser);
} // bindView


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
