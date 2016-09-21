/**
 *****************************************************************************
 * Copyright (c) 2016 IBM Corporation and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Lokesh Haralakatta - Initial Contribution
 *****************************************************************************
 */

package com.ibm.iot.dslinks.RpiUnoLink;

import org.dsa.iot.dslink.DSLink;
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeBuilder;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;
import org.dsa.iot.dslink.util.Objects;
import org.dsa.iot.dslink.util.handler.Handler;
import org.dsa.iot.dslink.node.Permission;
import org.dsa.iot.dslink.node.Writable;
import org.dsa.iot.dslink.node.actions.Action;
import org.dsa.iot.dslink.node.actions.ActionResult;
import org.dsa.iot.dslink.node.actions.Parameter;
import org.dsa.iot.dslink.node.actions.table.Row;
import org.dsa.iot.dslink.node.actions.table.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.util.Enumeration;

public class RpiUnoResponder implements SerialPortEventListener {
    private final Logger LOGGER = LoggerFactory.getLogger(RpiUnoResponder.class);
    private SerialPort serialPort;
    private String port ="/dev/ttyACM0";
    private BufferedReader input;
    private BufferedWriter output;
    private final int TIME_OUT = 2000;
    private final int DATA_RATE = 9600;
    private final int BLINK = 101;
    private final int BLINK5 = 105;
    private final int BLINK10 = 110;
    private final int REBOOT = 115;


    private Node tempNode;
    private Node sensorNode;

    public RpiUnoResponder() {}

    public void init(DSLink link) {
        NodeBuilder builder = link.getNodeManager().createRootNode("RpiUno");
        builder.setAttribute("type", new Value("device"));
        builder.setAttribute("devicetype", new Value("attached"));
        Node node = builder.build();

        boolean serialInit = initialize();

        if(serialInit){
            initTempNode(node);
            initSensorNode(node);
            initBlinkActionNode(node);
            initBlink5ActionNode(node);
            initBlink10ActionNode(node);
            initRebootActionNode(node);
        }
    }

    private void initTempNode(Node node) {
        NodeBuilder builder = node.createChild("Temperature");
        tempNode = builder.build();
        tempNode.setValueType(ValueType.DYNAMIC);
    }

    private void initSensorNode(Node node) {
        NodeBuilder builder = node.createChild("Sensor");
        sensorNode = builder.build();
        sensorNode.setValueType(ValueType.DYNAMIC);
    }

    private void initBlinkActionNode(Node node) {
        NodeBuilder builder = node.createChild("Blink");
        builder.setAction(new Action(Permission.READ, new Handler<ActionResult>() {
            @Override
            public void handle(ActionResult event) {
                LOGGER.info("Responder Blink action invoked from requester");
                try {
                      output.write(BLINK);
		      output.flush();
                } catch(Exception e) { e.printStackTrace(); }
                Table t = event.getTable();
                t.addRow(Row.make(new Value("Blink on Arduino Uno Invoked")));
            }
        }).addResult(new Parameter("response", ValueType.STRING)));
        builder.build();
    }

    private void initBlink5ActionNode(Node node) {
        NodeBuilder builder = node.createChild("Blink5");
        builder.setAction(new Action(Permission.READ, new Handler<ActionResult>() {
            @Override
            public void handle(ActionResult event) {
                LOGGER.info("Responder Blink5 action invoked from requester");
                try {
                      output.write(BLINK5);
		      output.flush();
                } catch(Exception e) { e.printStackTrace(); }
                Table t = event.getTable();
                t.addRow(Row.make(new Value("Blink5 on Arduino Uno Invoked")));
            }
        }).addResult(new Parameter("response", ValueType.STRING)));
        builder.build();
    }

    private void initBlink10ActionNode(Node node) {
        NodeBuilder builder = node.createChild("Blink10");
        builder.setAction(new Action(Permission.READ, new Handler<ActionResult>() {
            @Override
            public void handle(ActionResult event) {
                LOGGER.info("Responder Blink10 action invoked from requester");
                try {
                      output.write(BLINK10);
		      output.flush();
                } catch(Exception e) { e.printStackTrace(); }
                Table t = event.getTable();
                t.addRow(Row.make(new Value("Blink10 on Arduino Uno Invoked")));
            }
        }).addResult(new Parameter("response", ValueType.STRING)));
        builder.build();
    }

    private void initRebootActionNode(Node node) {
        NodeBuilder builder = node.createChild("Reboot");
        builder.setAction(new Action(Permission.READ, new Handler<ActionResult>() {
            @Override
            public void handle(ActionResult event) {
                LOGGER.info("Responder Reboot action invoked from requester");
                try {
                      output.write(REBOOT);
		      output.flush();
                } catch(Exception e) { e.printStackTrace(); }
                Table t = event.getTable();
                t.addRow(Row.make(new Value("Reboot on Arduino Uno Invoked")));
            }
        }).addResult(new Parameter("response", ValueType.STRING)));
        builder.build();
    }

    public void serialEvent(SerialPortEvent oEvent) {
	String line = null;
	synchronized(this) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
			        line = input.readLine();
				//LOGGER.info("Data from Uno: "+line);
                                String valuePair = line.split(" ")[1];
                                String values[] = valuePair.split(",");
                                Value tempVal = new Value(Float.parseFloat(values[0].split(":")[1]));
                                tempNode.setValue(tempVal);
                                Value sensorVal = new Value(Integer.parseInt(values[1].split(":")[1]));
                                sensorNode.setValue(sensorVal);
                                //LOGGER.info("Temperature set to "+tempVal);
                                //LOGGER.info("Sensor Value set to "+sensorVal);
			} catch (Exception e) {
					LOGGER.error(e.toString());
			}
		}

	}
    }

    private boolean initialize() {
 	System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

 	try {
 		CommPortIdentifier portId = null;
 		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

 		//First, Find an instance of serial port as set in PORT_NAMES.
 		while (portEnum.hasMoreElements()) {
 			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
 			if (currPortId.getName().equals(this.port)) {
 				portId = currPortId;
 				break;
 			}
 		}
 		if (portId == null) {
 			LOGGER.info("Could not find COM port.");
 			return false;
 		}

 		// open serial port, and use class name for the appName.
 		serialPort = (SerialPort) portId.open("RpiUnoResponder",
 					TIME_OUT);

 		// set port parameters
 		serialPort.setSerialPortParams(DATA_RATE,
 					SerialPort.DATABITS_8,
 					SerialPort.STOPBITS_1,
 					SerialPort.PARITY_NONE);

 		// open the streams
 		input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                output = new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));

 		// add event listeners
 		serialPort.addEventListener(this);
 		serialPort.notifyOnDataAvailable(true);
 	   } catch (Exception | Error e) {
 		LOGGER.error(" Got the following error "+e.getMessage());
 		return false;
 	   }
 	   return true;
    }

    public synchronized void close() {
	if (serialPort != null) {
		serialPort.removeEventListener();
		serialPort.close();
	}
    }
}
