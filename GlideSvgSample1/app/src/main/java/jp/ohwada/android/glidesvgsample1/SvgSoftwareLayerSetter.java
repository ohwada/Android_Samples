/**
 * Glide SVG sample
 * 2018-05-01 K.OHWADA 
 */

package jp.ohwada.android.glidesvgsample1;

import android.annotation.TargetApi;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

/**
 * class SvgSoftwareLayerSetter
 * 
 * Listener which updates the {@link ImageView} to be software rendered,
 * because {@link com.caverock.androidsvg.SVG SVG}/{@link android.graphics.Picture Picture}
 * can't render on a hardware backed {@link android.graphics.Canvas Canvas}.
 * @param <T> not used, here to prevent unchecked warnings at usage
 * original : https://github.com/bumptech/glide/tree/3.0/samples/svg/src/main/java/com/bumptech/glide/samples/svg
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SvgSoftwareLayerSetter<T> implements RequestListener<T, PictureDrawable> {

/**
 *  == onException ==
 */
    @Override
    public boolean onException(Exception e, T model, Target<PictureDrawable> target, boolean isFirstResource) {
        ImageView view = ((ImageViewTarget<?>) target).getView();
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            view.setLayerType(ImageView.LAYER_TYPE_NONE, null);
        }
        return false;
    } // onException

/**
 *  == onResourceReady ==
 */
    @Override
    public boolean onResourceReady(PictureDrawable resource, T model, Target<PictureDrawable> target,
                                   boolean isFromMemoryCache, boolean isFirstResource) {
        ImageView view = ((ImageViewTarget<?>) target).getView();
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
        }
        return false;
    } // onResourceReady

} // class SvgSoftwareLayerSetter

