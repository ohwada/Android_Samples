package jp.ohwada.android.aardemo1;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import jp.ohwada.android.library.LibraryActivity;

/** 
 *  class MainActivity
 */
public class MainActivity extends Activity {

/** 
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton fab = (ImageButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procClick();
            }
        }); // fab
    }

/** 
 * procClick
 */
private void procClick() {
                Intent intent = LibraryActivity.createIntent(this);
                startActivity(intent);
}

} // class MainActivity
