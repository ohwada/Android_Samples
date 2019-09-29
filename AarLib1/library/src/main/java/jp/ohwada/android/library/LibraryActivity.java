/** 
 *  AAR Sample
 *  2019-08-01 K.OHWADA
 */

package jp.ohwada.android.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/** 
 *  class LibraryActivity
 */
public class LibraryActivity extends Activity {

/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
    }

public static Intent createIntent(Context context){
            return new Intent(context, LibraryActivity.class);
        }

} // class LibraryActivity
