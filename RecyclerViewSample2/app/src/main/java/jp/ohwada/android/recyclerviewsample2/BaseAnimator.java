/**
 * RecyclerView sample
 * 2018-03-01 K.OHWADA 
 */

package jp.ohwada.android.recyclerviewsample2;

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
     * class BaseAnimator
     */ 
public abstract class BaseAnimator extends SimpleItemAnimator {

   	// debug
    	private final static String TAG_SUB = "baseAnimator";

    protected Interpolator mInterpolator = new FastOutSlowInInterpolator();

    protected boolean isRunning = false;

    /*
     * constractor
     */ 
  public BaseAnimator() {
    super();
  } // BaseAnimator


    /*
     * ==animateAdd ==
     * Called when an item is added to the RecyclerView
     */ 
  @Override public boolean animateAdd(final ViewHolder holder) {
    log_d("animateAdd");
    return procAnimateAdd(holder);
  } // animateAdd


    /*
     * ==animateRemove ==
     * Called when an item is removed from the RecyclerView.
     */ 
@Override 
public boolean animateRemove(final ViewHolder holder) {
    log_d("animateRemove");
    return procAnimateRemove(holder);
  } // animateRemove


    /*
     * ==animateMove ==
     * Called when an item is moved in the RecyclerView
     * TODO : some process are required, 
     * because  called when remove item
     */ 
  @Override
  public boolean animateMove(final ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        log_d("animateMove");
       return  false;
} // animateMove


    /*
     * ==animateChange ==
     * Called when an item is changed in the RecyclerView
     */ 
  @Override
  public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder, int fromX, int fromY,
      int toX, int toY) {
        log_d("animateChange");
      return  false;
} // animateChange


    /*
     * ==isRunning ==
     * Method which returns whether there are any item animations currently running
     */ 
  @Override 
public boolean isRunning() {
        log_d("isRunning");
      return isRunning;
  } // isRunning


    /*
     * == endAnimation ==
     * Method called when an animation on a view should be ended immediately.
     */ 
  @Override public void endAnimation(ViewHolder item) {
} // endAnimation


    /*
     * == endAnimations ==
     * Method called when all item animations should be ended immediately
     */ 
  @Override public void endAnimations() {
} // endAnimations


    /*
     * == runPendingAnimations ==
     * Called when there are pending animations waiting to be started
     */ 
  @Override public void runPendingAnimations() {
} // unPendingAnimations


    /*
     * procAnimateAdd
     * processing of animateAdd
     */ 
protected boolean procAnimateAdd(ViewHolder holder) {
    return false;
}// procAnimateAdd


    /*
     * procAnimateRemove
     * processing of animateRemove
     */ 
protected boolean procAnimateRemove(ViewHolder holder) {
    return false;
}// procAnimateRemove


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d


} // class BaseAnimator
