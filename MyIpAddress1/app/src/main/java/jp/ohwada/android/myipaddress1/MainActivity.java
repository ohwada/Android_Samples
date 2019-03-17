/*
 * My IP Address Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.myipaddress1;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * class MainActivity
 */ 
public class MainActivity extends Activity {

/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv1 = (TextView) findViewById(R.id.TextView_1);
        String addr = NetworkUtil.getMyIPAddress();
        tv1.setText(addr);
    } // onCreate

} // class MainActivity
