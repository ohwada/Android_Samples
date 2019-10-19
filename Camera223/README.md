Camera 223 - Android Samples
===============

RTSP Server using Camera2 API and MediaCodec <br/>

<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera223/screenshot/rtsp_system_overview.png" width="500" /><br/>


### screenshot <br/>
Camera Preview <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera223/screenshot/camera223_preview.png" width="300" /><br/>

show Setting Activity, when click "Setting" icon <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera223/screenshot/Camera223_setting_activity.png" width="300" /><br/>


### ImageFormatConverter <br/>
convert YUV420_888 to YUV420Planar <br/>
https://github.com/ohwada/Android_Samples/tree/master/ImageFormatConverter <br/>

### TODO <br/>
the phenomenon varies depending on the size of the image.

- SD Low quality( 176x144) <br/>
displayed black screen  <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera223/screenshot/vlc_on_mac_176x144.png" width="300" /><br/>


- SD High quality ( 480x360) <br/>
displayed green screen  <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera223/screenshot/vlc_on_mac_480x360.png" width="300" /><br/>


- VGA ( 640x 480 ) <br/>
displays correctly,  but response is slow. <br/>
does not easily reflect, when chang direction of the camera.  <br/>
it takes about 10 seconds. <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera223/screenshot/vlc_on_mac_640x480.png" width="300" /><br/>


### Reference <br/>

- https://developer.android.com/reference/android/hardware/camera2/package-summary
- https://github.com/fyhertz/libstreaming


### Docement <br/>
- https://qiita.com/ohwada/items/9698c3b7e426b3e66996

