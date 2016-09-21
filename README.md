# iot-dslinks
The **iot-dslinks** repository contains the code for DS Links implemented by **IBM IoT Client Library team** to be used with IBM Edge Analytics Agent. Refer [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) recipe to get to know about how to install and configure IBM Edge Analytics Agent on Raspberry Pi.

This respository contains the code for DS Links to be used with Raspberry Pi Device. 
#### Raspberry Pi System Info DS Link (RpiSysInfoLink):
The purpose of RpiSysInfoLink is to get in-built CPU Temperature from Raspberry Pi and the memory statistics from Raspberry Pi. When we install RpiSysInfoLink on Raspberry Pi configured with IBM Edge Analytcs Agent, we should be able to see the events on IBM Watson IoT Platform.
#### Raspberry Pi Uno DS Link (RpiUnoLink):
The purpose of RpiUnoLink is to get temperature and moisture data from the Temperature Sensor and Moisture Sensor connected to Arduino Uno Device and Arduino Uno is attached to Raspberry Pi. With RpiUnoLink installed on Raspberry Pi configured with IBM Edge Analytics Agent, we should be able to get the events on to IBM Watson IoT Platform and also perform device actions like LED Blink and Reboot of Arduino Uno by handling the local alerts from IBM Edge Analytics Agent. To get complete details on how to work with alerts and device actions, refer to [Alerts and Actions with Edge Analytics](Need to publish the recipe) Recipe.

## Steps to play around with DS Links:
### DS Links Build Prerequisites:
1. Install JDK 1.8 on Raspberry Pi referring to [RpiBlog](http://www.rpiblog.com/2014/03/installing-oracle-jdk-8-on-raspberry-pi.html)
2. Install git on Raspberry Pi 
  * sudo apt-get update
  * sudo apt-get install git
3. Install maven on Raspberry Pi
  * sudo apt-get update
  * sudo apt-get install maven

### Building DS Links:
1. Get the code on to Raspberry Pi - **git clone https://github.com/ibm-watson-iot/iot-dslinks.git**
2. Go to iot-dslinks directory - **cd iot-dslinks**
3. Build dslinks jar using the command - **mvn clean package**
4. Copy dslinks jar to DS Links directories 
  * cp target/dslinks-0.0.1.jar target/classes/RpiSysInfoLink
  * cp target/dslinks-0.0.1.jar target/classes/RpiUnoLink

### Using RpiSysInfoLink:
1. Raspberry Pi with IBM Edge Analytics Agent configured to connect to IBM Watson IoT Platform. Refer [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) Recipe
2. Copy **iot-dslinks/target/classes/RpiSysInfoLink** directory to **DSA-PATH/dglux-server/dslinks**
3. Stop and Start DSA Server - **sudo service stop dsa**  and **sudo service start dsa**
4. Log into DSA Server, we should be able to see new link with the name **RpiSysInfoLink** under **project->data->sys->links**
5. Restart IBM Edge Analytcs Agent link - **project->data->sys->link->restart**
6. We should be able to see a new attached device added by IBM Edge Analytics Agent on IBM Watson IoT Platform with the name RpiSysInfo
7. Use steps described in [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) Recipe to play around with data filtering

### Using RpiUnoLink: 
1. Raspberry Pi with IBM Edge Analytics Agent configured to connect to IBM Watson IoT Platform. Refer [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) Recipe
2. Copy **iot-dslinks/target/classes/RpiUnoLink** directory to **DSA-PATH/dglux-server/dslinks**
3. Stop and Start DSA Server - **sudo service stop dsa**  and **sudo service start dsa**
4. Log into DSA Server, we should be able to see new link with the name **RpiUnoLink** under **project->data->sys->links**
5. Restart IBM Edge Analytcs Agent link - **project->data->sys->link->restart**
6. We should be able to see a new attached device added by IBM Edge Analytics Agent on IBM Watson IoT Platform with the name RpiUno
7. Use steps described in [Edge Analytics](https://developer.ibm.com/recipes/tutorials/getting-started-with-edge-analytics-in-watson-iot-platform/) Recipe to play around with data filtering
8. Use steps described in [Alerts and Actions with Edge Analytics](Need to publish the recipe) Recipe to play around with alerts and device actions
