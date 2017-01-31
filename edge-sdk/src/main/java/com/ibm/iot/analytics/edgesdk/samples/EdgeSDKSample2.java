/**
 *****************************************************************************
 * Copyright (c) 2017 IBM Corporation and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * This is sample Java Program to illustrate the use of IBM Edge SDK to handle
 * alerts sent from Watson IoT Platform as part of Edge Rule and apply action
 * present as part of actionMessage on receiving critical values from 2 attached
 * devices.
 *
 * To successfully use this sample, we are using Raspberry Pi 3 as Edge gateway
 * device connected with 2 Arduino Uno devices with appropriate Sketches to send
 * out temperature data. We define an Edge Rule from Watson IoT Platform to send
 * alert to edge gateway on receiving critical value for temperature as part of
 * actionMessage. We have 3 actions implemented to be performed on device
 * based on the message present in actionMessage.
 *
 * We should already have Watson IoT Organization, Application Credentials and
 * Gateway Credentials to be used in this sample application. Replace the x's
 * with actual values for the credentials in the program before compiling.
 *
 * This Java Sample has to be compiled with IBM Edge SDK Jar to get executable.
 * On executing Java Class for this SDK Sample, we should see Gateway status as
 * connected on Watson IoT Dashboard and the specified attached devices getting
 * created, if they don't exist already and we should be able to see the devices
 * data on Watson IoT Platform Dashboard.
 *
 * Steps to build this sample:
 *      1. Get IBM Edge SDK Jar from Watson IoT Platform Dashboard
 *      2. Let SDK Home be: export SDK_HOME="/home/pi/EAA-SDK"
 *      3. Let SDK Jar path be: export SDK_JAR_PATH="$SDK_HOME/edge-sdk-master.jar"
 *      4. Place this sample program into path
 *        $SDK_HOME/edge-sdk/src/main/java/com/ibm/iot/analytics/edgesdk/samples
 *      5. Go to samples path and run javac to compile the sample program
 *        javac -cp $SDK_JAR_PATH EdgeSDKSample2.java
 *
 * To execute the sample:
 *      java -cp $SDK_JAR_PATH:$SDK_HOME/edge-sdk/src/main/java com.ibm.iot.analytics.edgesdk.samples.EdgeSDKSample2
 *
 * Contributors:
 * Lokesh Haralakatta - Initial Contribution
 *****************************************************************************
 */

 package com.ibm.iot.analytics.edgesdk.samples;

 // Import SLF4J Logger Classes
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 // Import required Edge SDK Classes
 import com.ibm.iot.analytics.edgesdk.api.EdgeAnalyticsAgent;
 import com.ibm.iot.analytics.edgesdk.api.client.EdgeAnalyticsClient;
 import com.ibm.iot.analytics.edgesdk.api.client.EdgeProperties;
 import com.ibm.iot.analytics.edgesdk.api.AbstractDeviceActionHandler;
 import com.ibm.iot.analytics.edgesdk.api.connectors.StringConnector;
 import com.ibm.iot.analytics.edgesdk.api.connectors.file.FileConnector;
 import com.ibm.iot.analytics.edgesdk.api.connectors.transformers.string.JsonTransformer;

 // Import required serial communication classes
 import gnu.io.CommPortIdentifier;
 import gnu.io.SerialPort;
 import gnu.io.SerialPortEvent;
 import gnu.io.SerialPortEventListener;

 // Import JSON Object Class from gson package
 import com.google.gson.JsonObject;

 // Import required Java IO Classes
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.FileWriter;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.nio.file.Files;

 // Import required classes from Java Util Package
 import java.util.concurrent.TimeUnit;
 import java.util.Enumeration;

 // Public Class to represent Edge Agent, collect device events
 // Overrides handleDeviceAlert Method to hanlde alert from Edge
 public class EdgeSDKSample2 extends AbstractDeviceActionHandler{
         // WIoTP specific values like Organization Id, Application Credentials,
         // Gateway Credentials, Attached devices names
         // Replace x's with actual values
         private final String ORG_ID = "xxxxxx";
         private final String APP_ID = "Edge_Sdk_Sample2_App_Id";
         private final String AUTH_KEY = "x-xxxxxx-xxxxxxxxxx";
         private final String AUTH_TOKEN = "xxxxxxxxxxxxxxxxxx";
         private final String GATEWAY_ID = "egDevice";
         private final String GATEWAY_TYPE = "egType";
         private final String GATEWAY_AUTH_TOKEN = "xxxxxxxx";
         private final String ATTACHED_DEV_1 = "MR-108";
         private final String ATTACHED_DEV_2 = "MR-110";

         // COM Ports for serial communication with 2 attached devices
         // Replace port names as existing on Raspberry Pi 3
         private final String COM_PORT_1 = "/dev/ttyACM0";
         private final String COM_PORT_2 = "/dev/ttyACM1";

         // Edge SDK Specific Members
         private EdgeProperties edgeProps;
         private EdgeAnalyticsClient edgeAnalyticsClient;
         private EdgeAnalyticsAgent eaa;

         // SLF4J Logger Member
         private Logger LOGGER;

         // Members for 2 attached devices
         private attachedDevice dev1;
         private attachedDevice dev2;

         // Directory paths for devices on Edge to monitor for device events
         private static final String dev1DirPath = "/home/pi/EAA-SDK/edge-sdk/src/main/java/com/ibm/iot/analytics/edgesdk/samples/dev1Data";
         private static final String dev2DirPath = "/home/pi/EAA-SDK/edge-sdk/src/main/java/com/ibm/iot/analytics/edgesdk/samples/dev2Data";

         // File paths for devices to buffer and write device events with given chunk value
         private final String dev1FilePath = "/home/pi/EAA-SDK/edge-sdk/src/main/java/com/ibm/iot/analytics/edgesdk/samples/dev1Data/dev1_events.json";
         private final String dev2FilePath = "/home/pi/EAA-SDK/edge-sdk/src/main/java/com/ibm/iot/analytics/edgesdk/samples/dev2Data/dev2_events.json";

         // Constructor to initialize members and devices
         EdgeSDKSample2() throws Exception {
                 // Initialize Logger Instance
                 LOGGER = LoggerFactory.getLogger(EdgeSDKSample2.class);
                 // Initialize EdgeProperties Instance
                 edgeProps = new EdgeProperties(ORG_ID, APP_ID, AUTH_KEY, AUTH_TOKEN,
                                         GATEWAY_TYPE, GATEWAY_ID, GATEWAY_AUTH_TOKEN);
                 // Create EdgeAnalyticsClient Instance using properties instance
                 edgeAnalyticsClient = new EdgeAnalyticsClient(edgeProps);
                 // Get Edge Analytics Agent
                 eaa = edgeAnalyticsClient.getAgent();
                 // Create Instnaces for Attached Devices which inturn initialize
                 // COM Ports for Serial Communication with actual attached devices
                 dev1 = new attachedDevice(ATTACHED_DEV_1,COM_PORT_1,dev1FilePath);
                 dev2 = new attachedDevice(ATTACHED_DEV_2,COM_PORT_2,dev2FilePath);
         }

         // Override Method to handle alert present in Edge Rule
         // Gets called whenever there is an alert to indicate critical situation
         @Override
         public void handleDeviceAlert(JsonObject deviceAlert)  {
                 // Get alert fields from the edge rule
                 String actionMessage = deviceAlert.get("actionMessage").getAsString();
                 String deviceId = deviceAlert.get("deviceId").getAsString();
                 // Identify the device for which alert is being received for
                 // And then route to device with the actionMessage contents
                 if(deviceId.compareTo(ATTACHED_DEV_1)==0)
                        dev1.handleDeviceAction(actionMessage);
                 else
                        dev2.handleDeviceAction(actionMessage);
         }

         // Main Entry Point to Edge Analytics Agent that's get started and
         // continously collect devices data using File Connectors
         // Registers device action handler to handle alerts and device actions
         public static void main(String[] args) throws Exception {
                 // Create an Instance for Edge SDK Sample
                 EdgeSDKSample2 edgeApp = new EdgeSDKSample2();

                 // Start edge analytics agent on gateway device.
                 edgeApp.eaa.start();

                 // Register hanlder for alerts to EAA
                 edgeApp.eaa.registerDeviceActionHandler(edgeApp);

                 // Connector to read data from device-1 events text file
                 final StringConnector dev1Connector = createJsonConnector(dev1DirPath);
                 // Send dev1 data to WIoTP through agent
                 edgeApp.eaa.deviceData(edgeApp.dev1.getDeviceName(), "sdkDevices", "eventType", dev1Connector);

                 //Connector to read data from device-2 events text file
                 final StringConnector dev2Connector = createJsonConnector(dev2DirPath);
                 // Send dev2 data to WIoTP through agent
                 edgeApp.eaa.deviceData(edgeApp.dev2.getDeviceName(), "sdkDevices", "eventType", dev2Connector);
         }

         //Connector to watch directory for new files and read the data for every 1 seconds
         private static StringConnector createJsonConnector(String dirPath) throws IOException {
           FileConnector connector = new FileConnector();
           connector.setTransformer(new JsonTransformer());
           connector.directoryWatcher(dirPath);
           connector.setPollPeriod(1, TimeUnit.SECONDS);
           return connector;
         }
 }

 // Class to represent attached devices
 // Implements SerialPortEventListener Interface for data transfer
 class attachedDevice implements SerialPortEventListener {
         // Logger member
         private Logger LOGGER;
         // Serial Port to be initialized and opened for communication
         private SerialPort serialPort;
         // Portname to be associated with device
         private String portName;
         // Device Name for identifying the attached device
         private String deviceName;
         // Filepath to buffer and dump received device events
         private String filePath;
         // Reader and Writter Instances to be associated with given serial port
         private BufferedReader input;
         private BufferedWriter output;
         // Buffer member to store chunk of device events
         private StringBuffer events[];
         // Variable to track number of device events in buffer
         private int eventsCount;
         // Serial Port Initialization Values
         private final int TIME_OUT = 2000;
         private final int DATA_RATE = 9600;
         // Device events chunk size to buffer
         private final int MAX_EVENTS = 10;
         // Device Actions constants
         private final int BUZZ = 1;
         private final int BLINK = 2;
         private final int REBOOT = 3;

         // Constructor to initialize instance with given values
         attachedDevice(String dName, String pName, String fPath) throws Exception {
                 LOGGER = LoggerFactory.getLogger(dName);
                 deviceName = dName;
                 portName = pName;
                 filePath = fPath;
                 // Initialize serial port
                 initialize();
                 // Buffer to store device events
                 events = new StringBuffer[MAX_EVENTS];
                 // Initialize current events count to 0
                 eventsCount=0;
                 // open the streams to connected with serial port
                 input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                 output = new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));
         }

         // Method to handle given action on the device
         public void handleDeviceAction(String action){
                 try {
                         // Check for action Buzz
                         if(action.compareToIgnoreCase("buzz")==0){
                                 output.write(BUZZ);
                                 output.flush();
                         }
                         // Check for action Blink
                         else if(action.compareToIgnoreCase("blink")==0){
                                 output.write(BLINK);
                                 output.flush();
                         }
                         // Check for action Reboot
                         else if(action.compareToIgnoreCase("reboot")==0){
                                 output.write(REBOOT);
                                 output.flush();
                         }
                         // Buzz as default action
                         else {
                                 output.write(BUZZ);
                                 output.flush();
                         }
             }
             catch(Exception e){
                    LOGGER.error("Exception caught while applying action on device - "+deviceName);
                    LOGGER.error(e.toString());
             }
         }

         // Method to return name associated with attached device
         public String getDeviceName(){
                 return deviceName;
         }

         // Method to initialize given serial port and establish the connection
         private boolean initialize() {
                 System.setProperty("gnu.io.rxtx.SerialPorts", portName);

                 try {
                         CommPortIdentifier portId = null;
                         Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

                         // Search for given Serial Portname and break the loop as we find one
                         while (portEnum.hasMoreElements()) {
                                 CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                                 if (currPortId.getName().equals(portName)) {
                                         portId = currPortId;
                                         break;
                                 }
                         }
                         if (portId == null) {
                                 LOGGER.error("Could not find COM port.");
                                 return false;
                         }

                         // open serial port, and use device name for the appName.
                         serialPort = (SerialPort) portId.open(deviceName,TIME_OUT);

                         // set port parameters
                         serialPort.setSerialPortParams(DATA_RATE,
                                                        SerialPort.DATABITS_8,
                                                        SerialPort.STOPBITS_1,
                                                        SerialPort.PARITY_NONE);

                         // add event listeners ti listen for serial event
                         serialPort.addEventListener(this);
                         serialPort.notifyOnDataAvailable(true);
                 } catch (Exception | Error e) {
                         LOGGER.error(" Got the following error "+e.getMessage());
                         return false;
                 }
                 return true;
         }

         // Method to close the opened port
         public synchronized void close() {
                 if (serialPort != null) {
                         serialPort.removeEventListener();
                         serialPort.close();
                 }
         }

         // Method to handle received serial event from the opened serial port
         public void serialEvent(SerialPortEvent oEvent) {
                 String line = null;
                 synchronized(this) {
                         // Check for availability of device data
                         if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                                 try {
                                         // We assume that device data line is in the form status temp:value
                                         line = input.readLine();
                                         String valuePair=null;
                                         // Get temperature value pair
                                         if(line != null && line.contains(" ")) valuePair = line.split(" ")[1];
                                         if(valuePair != null && valuePair.length()>0){
                                                 // Get temperature value
                                                 float tempVal = Float.parseFloat(valuePair.split(":")[1]);
                                                 // Build Json Object
                                                 JsonObject jObj = new JsonObject();
                                                 jObj.addProperty("temperature",new Float(tempVal));
                                                 // Add Json Object to buffer as string
                                                 events[eventsCount] = new StringBuffer(jObj.toString());
                                                 events[eventsCount].append("\n");
                                                 // Increase events count by 1
                                                 eventsCount+=1;
                                         }

                                 } catch (Exception e) {
                                         LOGGER.error("Exception thrown while reading Data from attached device -" +deviceName);
                                         LOGGER.error(e.toString());
                                 }
                         }

                 }
                 // If we have reached MAX EVENTS in buffer, then write to file for connector to consume
                 if(eventsCount == MAX_EVENTS){
                         try {
                                 Files.deleteIfExists(Paths.get(filePath));
                                 BufferedWriter evtsStream = new BufferedWriter(new FileWriter(filePath));
                                 for(int i=0;i<MAX_EVENTS;i++){
                                         String event = events[i].toString();
                                         evtsStream.write(event,0,event.length());
                                 }
                                 evtsStream.flush();
                                 evtsStream.close();
                                 eventsCount=0;
                         }
                         catch(Exception e){
                                 LOGGER.error("Exception thrown while writing Data to file -" +filePath);
                                 LOGGER.error(e.toString());
                         }
                 }
         }
 }
