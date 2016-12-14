# Java dslinks
Contains the code for DS Links to be used with Raspberry Pi 3 Device.
#### Raspberry Pi System Info DS Link (RpiSysInfoLink):
The purpose of RpiSysInfoLink is to get in-built CPU Temperature from Raspberry Pi and the memory statistics from Raspberry Pi. When we install RpiSysInfoLink on Raspberry Pi configured with IBM Edge Analytcs Agent, we should be able to see the events on IBM Watson IoT Platform.
#### Raspberry Pi Uno DS Link (RpiUnoLink):
The purpose of RpiUnoLink is to get temperature and moisture data from the Temperature Sensor and Moisture Sensor connected to Arduino Uno Device and Arduino Uno is attached to Raspberry Pi. With RpiUnoLink installed on Raspberry Pi 3 configured with IBM Edge Analytics Agent, we should be able to get the events on to IBM Watson IoT Platform and also perform device actions like LED Blink and Reboot of Arduino Uno by handling the local alerts from IBM Edge Analytics Agent. To get complete details on how to work with alerts and device actions, refer to [Alerts and Actions with Edge Analytics](https://developer.ibm.com/recipes/tutorials/handling-alerts-and-device-actions-with-edge-analytics-in-ibm-watson-iot-platform/) Recipe.

## Steps to play around with DS Links:
### DS Links Build Prerequisites:
1. Install JDK 1.8 on Raspberry Pi 3 referring to [RpiBlog](http://www.rpiblog.com/2014/03/installing-oracle-jdk-8-on-raspberry-pi.html)
2. Install git on Raspberry Pi 3
  * sudo apt-get update
  * sudo apt-get install git
3. Install maven on Raspberry Pi 3
  * sudo apt-get update
  * sudo apt-get install maven

### Building DS Links:
1. Get the code on to Raspberry Pi 3
   * git clone https://github.com/ibm-watson-iot/iot-dslinks.git
2. Go to iot-dslinks/java/RpiLinks directory
   * cd iot-dslinks/java/RpiLinks
3. Build dslinks jar using the maven command
   * mvn clean package
4. Copy dslinks jar to DS Links directories
  * cp target/dslinks-0.0.1.jar target/classes/RpiSysInfoLink
  * cp target/dslinks-0.0.1.jar target/classes/RpiUnoLink

### Using RpiSysInfoLink:
1. Raspberry Pi with IBM Edge Analytics Agent configured to connect to IBM Watson IoT Platform. Refer [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) Recipe
2. Copy **iot-dslinks/java/RpiLinks/target/classes/RpiSysInfoLink** directory to **DSA-PATH/dglux-server/dslinks**
   * sudo cp -r target/classes/RpiSysInfoLink /opt/dsa/dglux-server/dslinks
3. Stop and Start DSA Server
   * sudo service dsa stop
   * sudo service dsa start
4. Log into DGLux Tool, we should be able to see new link with the name **RpiSysInfoLink**
   * Data->sys->links
5. Restart IBM Edge Analytcs Agent link from DGLux Tool
   * Data->sys->links->ibm-watson-iot-edge-analytics-dslink-java-1.0.0->Restart Link
6. We should see values retrieved from Raspberry Pi 3 device in DGLux Matrix Window
   * Data->downstream->RpiSysInfo->RpiSysInfo
7. We should be able to see a new attached device added by IBM Edge Analytics Agent on IBM Watson IoT Platform with the name **RpiSysInfo**
8. Use steps described in [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) Recipe to play around with data filtering
9. Uninstall RpiSysInfoLink from DSA Server
  * Data->sys->links->RpiSysInfo->Uninstall Link
10. Logout from DGLux Tool and Stop DSA Server
  * sudo service dsa stop

### Using RpiUnoLink:
1. Raspberry Pi with IBM Edge Analytics Agent configured to connect to IBM Watson IoT Platform. Refer [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) Recipe
2. Make ready the Arduino Uno board with Sketch
   * [Sketch Program](https://github.com/ibm-watson-iot/iot-dslinks/blob/master/ArduinoSketches/sketch_moisture.ino)
3. Connect required sensors to Uno by referring to details in recipe
   * [Alerts and Actions with Edge Analytics](https://developer.ibm.com/recipes/tutorials/handling-alerts-and-device-actions-with-edge-analytics-in-ibm-watson-iot-platform/)
4. Connect Arduino Uno board to Raspberry Pi 3 using USB Cable and make sure there exists /dev/ttyACM0 on Raspberry Pi 3
5. Install Java RXTX Library in Raspberry Pi 3 for Serial Communication with Arduino Uno board
   * sudo apt-get install librxtx-java
   * sudo cp /usr/lib/jni/librxtxSerial.so $JAVA_HOME/jre/lib/arm
   * sudo cp /usr/share/java/RXTXcomm.jar $JAVA_HOME/jre/lib/
6. Copy **iot-dslinks/java/RpiLinks/target/classes/RpiUnoLink** directory to **DSA-PATH/dglux-server/dslinks**
   * sudo cp -r target/classes/RpiUnoLink  /opt/dsa/dglux-server/dslinks
7. Stop and Start DSA Server
   * sudo service dsa stop
   * sudo service dsa start
8. Log into DGLux Tool, we should be able to see new link with the name **RpiUnoLink**
   * Data->sys->links
9. Restart IBM Edge Analytcs Agent link from DGLux Tool
   * Data->sys->links->link->ibm-watson-iot-edge-analytics-dslink-java-1.0.0->Restart Link
10. We should see values retrieved from Arduino Uno board in DGLux Matrix Window
   * Data->downstream->RpiUno->RpiUno
11. We should be able to see a new attached device added by IBM Edge Analytics Agent on IBM Watson IoT Platform with the name **RpiUno**
12. Use steps described in [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) Recipe to play around with data filtering
13. Use steps described in [Alerts and Actions with Edge Analytics](https://developer.ibm.com/recipes/tutorials/handling-alerts-and-device-actions-with-edge-analytics-in-ibm-watson-iot-platform/) Recipe to play around with alerts and device actions
14. Uninstall RpiUnoLink from DSA Server
  * Data->sys->links->RpiSysInfo->Uninstall Link
15. Logout from DGLux Tool and Stop DSA Server
  * sudo service dsa stop


#### Note:
If we are installing **IBM Edge Analytics Agent Link** in DGLux Tool for more than once, then we see random names for IBM Edge Analytics Agent node data under **Data->downstream**. Need to avoid these random names in order to make the **RpiSysInfoLink** and **RpiUnoLink** work properly with **IBM Watson IoT Platform**. Stop DSA Server, remove these random entries present in the file **conns.json** located at the path **DSA-PATH/dglux-server/conns.json** and Start DSA Server.
