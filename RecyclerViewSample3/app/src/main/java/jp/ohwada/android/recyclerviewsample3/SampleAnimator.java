/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewsample3;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;


    /*
     * class SampleAnimator
     */ 
public class SampleAnimator extends BaseAnimator {

   	// debug
    	private final static String TAG_SUB = "SampleAnimator";


    /*
     * constractor
     */ 
  public SampleAnimator() {
    super();
  } // SampleAnimator


    /*
     * procAnimateAdd
     * animate to increase size gradually
     */ 
  @Override 
protected boolean procAnimateAdd(final ViewHolder holder) {
    log_d("procAnimateAdd");
    ViewCompat.animate(holder.itemView)
        .scaleX(2) // zoom
        .scaleY(2) // zoom
        .setDuration(10000) // 1 sec
        .setInterpolator(mInterpolator)
        .setListener(new AddListener(holder))
        .start();
    return true;
  } // animateAdd


    /*
     * procAnimateRemove
     * animate to rotatie sideways, and become transparent gradually
     */
@Override 
protected boolean procAnimateRemove(final ViewHolder holder) {
    log_d("procAnimateRemove");
    ViewCompat.animate(holder.itemView)
        .rotationY(-90)
        .alpha(0)
        .setDuration(20000) // 2sec
        .setInterpolator(mInterpolator)
        .setListener(new RemoveListener(holder))

        .start();
    return true;
  } // animateRemove



    /*
     * class AddListener
     */
    public class AddListener extends ViewPropertyAnimatorListenerAdapter {

    public  AddListener(final RecyclerView.ViewHolder holder) {
        super();
        log_d(" AddListenert");
    } //  AddListener

    @Override public void onAnimationStart(View view) {
        log_d("AddListener onAnimationStart");
isRunning = true;
    } // onAnimationStart

    @Override public void onAnimationCancel(View view) {
        log_d("AddListener onAnimationCance");
    } // onAnimationCance

    @Override public void onAnimationEnd(View view) {
        log_d("AddListener onAnimationEnd");
        view.setScaleX(1); // normal
        view.setScaleY(1);
isRunning = false;
    }  // onAnimationEnd

  } //class  AddListener



    /*
     * class RemoveListener
     */
    public class RemoveListener extends ViewPropertyAnimatorListenerAdapter {

    public RemoveListener(final RecyclerView.ViewHolder holder) {
        super();
        log_d("RemoveListenert");
    } // RemoveListener

    @Override public void onAnimationStart(View view) {
        log_d("RemoveListener onAnimationStart");
        isRunning = true;
    } // onAnimationStart

    @Override public void onAnimationCancel(View view) {
        log_d("RemoveListener onAnimationCance");
    } // onAnimationCance

    @Override public void onAnimationEnd(View view) {
        // hide view when animation ends
        log_d("RemoveListener onAnimationEnd");
        view.setVisibility(View.INVISIBLE);
        isRunning = false;
    }  // onAnimationEnd

  } //class RemoveListener



 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

} // class SampleAnimator
