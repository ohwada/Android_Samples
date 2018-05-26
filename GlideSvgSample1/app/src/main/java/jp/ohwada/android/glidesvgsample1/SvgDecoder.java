/**
 * Glide SVG sample
 * 2018-05-01 K.OHWADA 
 */

package jp.ohwada.android.glidesvgsample1;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;


/**
 * class SvgDecoder
 * 
 * Decodes an SVG internal representation from an {@link InputStream}.
 * original : https://github.com/bumptech/glide/tree/3.0/samples/svg/src/main/java/com/bumptech/glide/samples/svg
 */
public class SvgDecoder implements ResourceDecoder<InputStream, SVG> {

/**
 *  decode
 */
    public Resource<SVG> decode(InputStream source, int width, int height) throws IOException {
        try {
            SVG svg = SVG.getFromInputStream(source);
            return new SimpleResource<SVG>(svg);
        } catch (SVGParseException ex) {
            throw new IOException("Cannot load SVG from stream", ex);
        }
    } // decode

/**
 *  == getId ==
 */
    @Override
    public String getId() {
        return "SvgDecoder.com.bumptech.svgsample.app";
    } // getId

} // class SvgDecoder
