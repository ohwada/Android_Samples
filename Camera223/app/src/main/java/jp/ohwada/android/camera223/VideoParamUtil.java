/**
 * Camera2 Sample
  * VideoParam
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera223;


import java.util.ArrayList;
import java.util.List;

/**
  *  class VideoParamUtil
  */
public class VideoParamUtil {

// reference : https://developer.android.com/guide/topics/media/media-formats#video-formats

    private static final int FRAME_RATE = 10; // 10fps
    private static final int IFRAME_INTERVAL = 10;


    // VGA
    // displays correctly,  but response is slow
    private final static int VGA_WIDTH = 640;
    private final static int VGA_HEIGHT = 480;

    // 640*480*10*0.125 = 384000
    private static final int VGA_BIT_RATE = 400000; // 400Kbps


    // HD (720p)
    // displays correctly,  but response is slow
    private final static int HD_WIDTH = 1280;
    private final static int HD_HEIGHT = 720;
    private static final int HD_BIT_RATE = 1200000; // 1.2Mbps


    // SVGA 	800x600
    // displays correctly,  but response is slow
    private final static int SVGA_WIDTH = 800;
    private final static int SVGA_HEIGHT = 600;
    private static final int SVGA_BIT_RATE = 600000; // 600Kbps


    // SD (High quality)
    // displayed green screen
    private final static int SD_WIDTH = 480;
    private final static int SD_HEIGHT = 360;
    private static final int SD_BIT_RATE = 220000; // 220Kbps

    // QVGA
    // displayed black screen
    private final static int QVGA_WIDTH = 320;
    private final static int QVGA_HEIGHT = 240;
    private static final int QVGA_BIT_RATE = 100000; // 100Kbps

    // SD (Low quality)
    // displayed black screen
    private final static int LOW_WIDTH = 176;
    private final static int  LOW_HEIGHT = 144;
    private static final int  LOW_BIT_RATE = 32000; // 32Kbps


/**
  *  constractor
  */
    public VideoParamUtil() {
        // nop
    }

/**
  *  getList
  */
    public static List<VideoParam> getList() {

        VideoParam vgaParam = new VideoParam(VGA_WIDTH, VGA_HEIGHT, FRAME_RATE, VGA_BIT_RATE, IFRAME_INTERVAL);

        VideoParam hdParam = vgaParam.clone();
        hdParam.setWidth(HD_WIDTH);
        hdParam.setHeight(HD_HEIGHT);
        hdParam.setBitRate(HD_BIT_RATE);

        VideoParam svgaParam = vgaParam.clone();
        svgaParam.setWidth(SVGA_WIDTH);
        svgaParam.setHeight(SVGA_HEIGHT);
        svgaParam.setBitRate(SVGA_BIT_RATE);


        VideoParam sdParam = vgaParam.clone();
        sdParam.setWidth(SD_WIDTH);
        sdParam.setHeight(SD_HEIGHT);
        sdParam.setBitRate(SD_BIT_RATE);

        VideoParam qvgaParam = vgaParam.clone();
        qvgaParam.setWidth(QVGA_WIDTH);
        qvgaParam.setHeight(QVGA_HEIGHT);
        qvgaParam.setBitRate(QVGA_BIT_RATE);

        VideoParam lowParam = vgaParam.clone();
        lowParam.setWidth(LOW_WIDTH);
        lowParam.setHeight(LOW_HEIGHT);
        lowParam.setBitRate(LOW_BIT_RATE);

        List<VideoParam> list = new ArrayList<VideoParam>();
        list.add(hdParam);
        list.add(svgaParam);
        list.add(vgaParam);
        list.add(sdParam);
        list.add(qvgaParam);
        list.add(lowParam);
        return list;
    }

} // class VideoParamUtil

