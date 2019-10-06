Camera 221 - Android Samples
===============

 record Video using Camera2 API and MediaCodec <br/>

the VLC  and ffplay can play h264 file as Video <br/>

however, MediaRecorder is best, for the purpose of recording Video <br/>

<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera221/screenshot/app_overview.png" width="500" /><br/>

### screenshot <br/>
Camera Preview <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera221/screenshot/Camera221_preview.png" width="300" /><br/>

show Setting Activity, when click "Setting" icon <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera221/screenshot/camera221_setting_activity.png" width="300" /><br/>

### ImageFormatConverter <br/>
convert YUV420_888 to YUV420Planar <br/>
https://github.com/ohwada/Android_Samples/tree/master/ImageFormatConverter <br/>

### Note <br/>
the VLC and the ffplay can play H264 file as Video <br/>
they can not play neither H263 nor VP8 <br/>

### TODO <br/>
played video turns green,  at only resolution 480x360 <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Camera221/screenshot/vlc_on_mac_480x360.png" width="200" /><br/>


### Reference <br/>
- https://developer.android.com/reference/android/hardware/camera2/package-summary
- https://developer.android.com/reference/android/media/MediaCodec
