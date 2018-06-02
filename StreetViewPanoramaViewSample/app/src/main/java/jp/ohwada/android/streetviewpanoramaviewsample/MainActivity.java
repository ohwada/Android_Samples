/*
 * StreetView Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.streetviewpanoramaviewsample;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

/**
 * class MainActivity
 * 
 * This shows how to create a simple activity with streetview
 * original : https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/main/java/com/example/mapdemo/StreetViewPanoramaViewDemoActivity.java
 */
public class MainActivity extends AppCompatActivity {

    // George St, Sydney
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static final String STREETVIEW_BUNDLE_KEY = "StreetViewBundleKey";

    private StreetViewPanoramaView mStreetViewPanoramaView;

    private Bundle mStreetViewBundle = null;

/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
        if (savedInstanceState == null) {
            options.position(SYDNEY);
        }

    mStreetViewPanoramaView = (StreetViewPanoramaView) findViewById(R.id.streetviewpanorama);

        Button btnShow = (Button) findViewById(R.id.Button_show);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStreetView();
            }
        }); // btnShow

        // *** IMPORTANT ***
        // StreetViewPanoramaView requires that the Bundle you pass contain
        if (savedInstanceState != null) {
            mStreetViewBundle = savedInstanceState.getBundle(STREETVIEW_BUNDLE_KEY);
        }
  
    } // onCreate

/**
 * onResume
 */
    @Override
    protected void onResume() {
        super.onResume();
    } // onResume

/**
 * onPause
 */
    @Override
    protected void onPause() {
        super.onPause();
        if ( mStreetViewPanoramaView != null ) {
            mStreetViewPanoramaView.onPause();
        }
    } // onPause

/**
 * onDestroy
 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( mStreetViewPanoramaView != null ) {
            mStreetViewPanoramaView.onDestroy();
        }
    } // onDestroy

/**
 * onSaveInstanceState
 */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mStreetViewBundle = outState.getBundle(STREETVIEW_BUNDLE_KEY);
        if (mStreetViewBundle == null) {
            mStreetViewBundle = new Bundle();
            outState.putBundle(STREETVIEW_BUNDLE_KEY, mStreetViewBundle);
        }

        mStreetViewPanoramaView.onSaveInstanceState(mStreetViewBundle);
    } // onSaveInstanceState


/**
 * showStreetView
 *  reference ; https://stackoverflow.com/questions/32287209/android-using-streetviewpanoramaview-in-xml
 */
private void showStreetView() {

 mStreetViewPanoramaView.onCreate(mStreetViewBundle);

mStreetViewPanoramaView.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        panorama.setPosition(SYDNEY);
    }
});

} // showStreetView

} // class MainActivity
