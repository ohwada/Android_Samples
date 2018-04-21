/**
 * ListView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.listviewsample1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

    // sample data
   private  static final String[] ANDROID_CODE_NAMES = {
            "Cupcake", "Donuts", "Eclair", "Froyo", "Gingerbread", "Honeycomb",
            "IceCreamSandwich", "JellyBean", "Kitkat", "Lollipop", "Marshmallow", "Nougat", "Oreo" };

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		ListView listView = (ListView) findViewById( R.id.ListView_1 );

// simple_list_item_1 : system  predefined layout file
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, ANDROID_CODE_NAMES );
 
        listView.setAdapter(arrayAdapter);

		}
		}
		// check position
		int n = position - 1;
		if (( n < 0 )||( n >= mList.size() )) return;
        VersionItem item = mList.get( n );
        toast_short( item.codename );

    } // onCreate

} // class MainActivity
