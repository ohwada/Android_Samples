/**
 * Camera2 Sample
  * VideoParam
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera221;


import java.util.ArrayList;
import java.util.List;

/**
  *  class VideoParamUtil
  *  TODO :
  *  played video turns green
  *  at only resolution 480x360
  */
public class VideoParamUtil {

// reference : https://developer.android.com/guide/topics/media/media-formats#video-formats

    private static final int FRAME_RATE = 30; // 30fps
    private static final int IFRAME_INTERVAL = 10;

    // VGA
    private final static int VGA_WIDTH = 640;
    private final static int VGA_HEIGHT = 480;

    // 640*480*30*0.125 = 1152000
    private static final int VGA_BIT_RATE = 1200000; // 1.2Mbps

    // HD (720p)
    private final static int HD_WIDTH = 1280;
    private final static int HD_HEIGHT = 720;
    private static final int HD_BIT_RATE = 3500000; // 3.5Mbps


    // SD (High quality)
    // TODO :
    // played video turns green
    // at only this resolution
    private final static int SD_WIDTH = 480;
    private final static int SD_HEIGHT = 360;
    private static final int SD_BIT_RATE = 650000; // 650Kbps


    // SD (Low quality)
    private final static int LOW_WIDTH = 176;
    private final static int  LOW_HEIGHT = 144;
    private static final int  LOW_BIT_RATE = 95000; // 95Kbps

    // XVGA
    private final static int XVGA_WIDTH = 1280;
    private final static int XVGA_HEIGHT = 960;
    private static final int XVGA_BIT_RATE = 4600000; // 4.6Mbps

    // QVGA
    private final static int QVGA_WIDTH = 320;
    private final static int QVGA_HEIGHT = 240;
    private static final int QVGA_BIT_RATE = 290000; // 290Kbps



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

        VideoParam sdParam = vgaParam.clone();
        sdParam.setWidth(SD_WIDTH);
        sdParam.setHeight(SD_HEIGHT);
        sdParam.setBitRate(SD_BIT_RATE);

        VideoParam lowParam = vgaParam.clone();
        lowParam.setWidth(LOW_WIDTH);
        lowParam.setHeight(LOW_HEIGHT);
        lowParam.setBitRate(LOW_BIT_RATE);


        VideoParam xvgaParam = vgaParam.clone();
        xvgaParam.setWidth(XVGA_WIDTH);
        xvgaParam.setHeight(XVGA_HEIGHT);
        xvgaParam.setBitRate(XVGA_BIT_RATE);

        VideoParam qvgaParam = vgaParam.clone();
        qvgaParam.setWidth(QVGA_WIDTH);
        qvgaParam.setHeight(QVGA_HEIGHT);
        qvgaParam.setBitRate(QVGA_BIT_RATE);

        List<VideoParam> list = new ArrayList<VideoParam>();
        list.add(xvgaParam);
        list.add(hdParam);
        list.add(vgaParam);
        list.add(sdParam);
        list.add(qvgaParam);
        list.add(lowParam);
        return list;
    }

} // class VideoParamUtil

