Opencv47 - Android Samples
===============

OpenCV Sample <br/>

Camera Calibration <br/>
 this sample is based on "Camera calibration With OpenCV" tutorial  <br/>
https://docs.opencv.org/3.4/d4/d94/tutorial_camera_calibration.html  <br/>

### Usage <br/>
It uses standard OpenCV asymmetric circles grid pattern 11x4 <br/>
// https://github.com/opencv/opencv/blob/3.4/doc/acircles_pattern.png <br/>
Shooting the circles grid pattern with the camera <br/>
Tap on highlighted pattern to capture pattern corners for calibration. <br/>
Move pattern along the whole screen and capture data. <br/>
When you've captured necessary amount of pattern corners (usually ~20 are enough),
Press "Calibrate" button for performing camera calibration. <br/>
when you've captured necessary amount of pattern corners (usually ~20 are enough) <br/>
The results are the camera matrix and 5 distortion coefficients. <br/>

### Menu <br/>
- Preview mode <br/>
- Calibrate <br/>

### Option of Preview mode <br/>
- Calibration <br/>
find calibration pattern and highlight pattern <br/>
- Undistortion <br/>
display undistorted image <br/>
- Comparison <br/>
display original and undistorted images side by side <br/>

### Screenshot <br/>
show Permission Dialog, when launch the app <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Opencv47/screenshot/opencv47_camera_permission.png" width="300" /><br/>

Preview <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Opencv47/screenshot/Opencv47_preview.png" width="300" /><br/>

Menu <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Opencv47/screenshot/opencv47_menu_preview_mode.png" width="300" /><br/>

Calibration Mode <br/>
find Camera Calibration Pattern and highlight pattern <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/Opencv47/screenshot/ opencv47_mode_calibration.png" width="300" /><br/>


### Opencv412Lib <br/>
AAR for OpenCV Android 4.1.2 <br/>
https://github.com/ohwada/Android_Samples/tree/master/Opencv412Lib <br/>

### Reference <br/>
- https://opencv.org/android/
- https://github.com/opencv/opencv/tree/master/samples/android/camera-calibration

