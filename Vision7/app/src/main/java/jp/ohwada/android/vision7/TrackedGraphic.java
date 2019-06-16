/**
 * Vision Sample
 * TrackedGraphic
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.vision7;


import jp.ohwada.android.vision7.ui.GraphicOverlay;


/**
 * abstract class TrackedGraphic
 * Common base class for defining graphics for a particular item type.  This along with
 * {@link GraphicTracker} avoids the need to duplicate this code for both the face and barcode
 * instances.
 */
abstract class TrackedGraphic<T> extends GraphicOverlay.Graphic {

    private int mId;


/**
 *  constractor
  */
    TrackedGraphic(GraphicOverlay overlay) {
        super(overlay);
    }

/**
 *  setId
  */
    void setId(int id) {
        mId = id;
    }

/**
 * getId
  */
    protected int getId() {
        return mId;
    }

    abstract void updateItem(T item);

} // class TrackedGraphic