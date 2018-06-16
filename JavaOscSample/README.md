Java OSC Sample  - Android Samples
===============

send OSC Message <br/>
with JavaOSC Library  <br/>

### Screenshot <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/JavaOscSample/screenshot/screenshot_java_osc_main.png" width="300" /><br/>

set IP adderess <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/JavaOscSample/screenshot/screenshot_java_osc_ip_addr.png" width="300" /><br/>

### Usage <br/>

Example  <br/>
send OSC message from Android to MAC <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/JavaOscSample/screenshot/overview_osc_android_to_mac.png" width="600" /><br/>

STEP 1 : 
connect Android and MAC to the same WiFi Router <br/>

STEP 2 : run PureData on MAC <br/>
PureData patch:  javaosc.pd <br/>
https://github.com/hoijui/JavaOSC/blob/master/modules/parent/src/main/resources/puredata/javaosc.pd <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/JavaOscSample/screenshot/pd_javaosc_pd.png" width="500" /><br/>

STEP 3 : set IP Adderess of MAC  on Android <br/>
ex) 192.168.1.4 <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/JavaOscSample/screenshot/screenshot_java_osc_ip_addr.png" width="300" /><br/>

STEP 4 : send OSC Message from Android, when click "On" button on Android  <br/>
and PureData recieve it <br/>
<image src="https://raw.githubusercontent.com/ohwada/Android_Samples/master/JavaOscSample/screenshot/pd_javaosc_console.png" width="500" /><br/>

### Reference <br/>
* https://github.com/hoijui/JavaOSC <br/>
