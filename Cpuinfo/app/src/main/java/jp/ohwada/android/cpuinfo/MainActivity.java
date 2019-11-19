/**
 * CPU Info
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.cpuinfo;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


/**
 * class MainActivity
 * show Cpuinfo
 * reference : http://www.office-matsunaga.biz/android/description.php?id=13
 */ 
public class MainActivity extends Activity {

    // debug
    private final static String TAG = "CpuInfo";


    private final static String FILE_PATH = "/proc/cpuinfo";

    private final static String LF = "\n";


    private TextView mTextView1;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView1 = (TextView) findViewById(R.id.TextView_1);
    }


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        String text = getCpuinfo();
        mTextView1.setTextColor(Color.BLACK);
        mTextView1.setTextSize(18);
        mTextView1.setText(text);
        log_d(text);
} 


/**
 * getCpuinfo
 */
private String getCpuinfo() {

    List<String> list = readFile(FILE_PATH);
    if(list == null) {
        showToast("can not read file");
        return null;
    }

    String str = "CPU Info" + LF;
    for(String line: list){
            str += line + LF;
    }
    return str;
}


/**
 * readFile
 */
private List<String> readFile(String filePath) {
    List<String> list =  new ArrayList<String>();
    try {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while((line = reader.readLine()) != null) {
            list.add( line );
        }
        reader.close();
    } catch (Exception e) {
        e.printStackTrace();
        list = null;
    }
    return list;
}


/**
 * showToast
 */
private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	   Log.d( TAG,  msg );
}


}
