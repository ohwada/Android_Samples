/**
 * Glide SVG sample
 * 2018-05-01 K.OHWADA 
 */

package jp.ohwada.android.glidesvgsample1;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.caverock.androidsvg.SVG;

/**
 * class SvgDrawableTranscoder
 * 
 * Convert the {@link SVG}'s internal representation to an Android-compatible one ({@link Picture}).
 * original : https://github.com/bumptech/glide/tree/3.0/samples/svg/src/main/java/com/bumptech/glide/samples/svg
 */
public class SvgDrawableTranscoder implements ResourceTranscoder<SVG, PictureDrawable> {

/**
 *  transcode
 */
    @Override
    public Resource<PictureDrawable> transcode(Resource<SVG> toTranscode) {
        SVG svg = toTranscode.get();
        Picture picture = svg.renderToPicture();
        PictureDrawable drawable = new PictureDrawable(picture);
        return new SimpleResource<PictureDrawable>(drawable);
    } // transcode

/**
 *  == getId == 
 */
    @Override
    public String getId() {
        return "";
    } // getId

} // class SvgDrawableTranscoder
