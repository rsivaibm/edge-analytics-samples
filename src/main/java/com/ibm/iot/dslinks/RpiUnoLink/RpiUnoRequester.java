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
import org.dsa.iot.dslink.DSLinkHandler;
import org.dsa.iot.dslink.node.value.SubscriptionValue;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.util.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.methods.requests.ListRequest;
import org.dsa.iot.dslink.methods.requests.InvokeRequest;
import org.dsa.iot.dslink.methods.responses.ListResponse;
import org.dsa.iot.dslink.methods.responses.InvokeResponse;
import org.dsa.iot.dslink.node.actions.table.Row;
import org.dsa.iot.dslink.node.actions.table.Table;

import java.util.Map;

import org.dsa.iot.dslink.util.json.JsonObject;

public class RpiUnoRequester {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpiUnoRequester.class);

    /**
     * Initializes the requester link.
     *
     * @param link Requester link to initialize.
     */
    public static void init(DSLink link) {
        subscribeTemp(link);
        subscribeSensor(link);
        subscribeAlerts(link);
        //listChildren(link);
    }

    private static void subscribeTemp(DSLink link) {
        String path = link.getPath() + "/RpiUno/Temperature";
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                //LOGGER.info("Temperature updated by RpiUnoResponder");
            }
        });
    }

    private static void subscribeSensor(DSLink link) {
        String path = link.getPath() + "/RpiUno/Sensor";
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                //LOGGER.info("Sensor Value updated by RpiUnoResponder");
            }
        });
    }

    private static void subscribeAlerts(DSLink link) {
        String path = "/downstream/ibm-iot-edge-analytics/IBMEdgeAnalyticsAlert";
        LOGGER.info("EAA Alerts Path - "+path);
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                LOGGER.info("Received alert from EAA: "+event.getValue());
                JsonObject alertJson = event.getValue().getMap();
                String actionMessage = alertJson.get("actionMessage");
                String deviceId = alertJson.get("deviceId");
                LOGGER.info("Extracted Action Message: "+actionMessage);
                LOGGER.info("Extracted Device Id: "+deviceId);
                if(deviceId.equals("RpiUno")){
                        invokeDeviceAction(link,actionMessage);
                }
                else {
                        LOGGER.info("Ignoring EAA Alert since it's not for RpiUno");
                }
            }
        });
    }

    private static void invokeDeviceAction(DSLink link,String action){
        String buzzPath = link.getPath() + "/RpiUno/Buzz";
        String buzz5Path = link.getPath() + "/RpiUno/Buzz5";
        String buzz10Path = link.getPath() + "/RpiUno/Buzz10";
        String rebootPath = link.getPath() + "/RpiUno/Reboot";
        InvokeRequest request;

        if(action.compareToIgnoreCase("reboot")==0)
             request = new InvokeRequest(rebootPath);
        else if(action.compareToIgnoreCase("buzz5")==0)
             request = new InvokeRequest(buzz5Path);
        else if(action.compareToIgnoreCase("buzz10")==0)
             request = new InvokeRequest(buzz10Path);
        else
             request = new InvokeRequest(buzzPath);

        link.getRequester().invoke(request, new Handler<InvokeResponse>() {
        @Override
        public void handle(InvokeResponse event) {
            LOGGER.info("Successfully invoked the Device Action");
            Table t = event.getTable();
            Row row = t.getRows().get(0);
            Value value = row.getValues().get(0);
            LOGGER.info("Received response: {}", value.toString());
        }
       });
    }

    private static void listChildren(DSLink link) {
        link.getRequester().list(new ListRequest("/downstream"), new Handler<ListResponse>() {
        @Override
        public void handle(ListResponse event) {
            iterateChildren(event.getNode());
        }
    });
   }

   private static void iterateChildren(Node node) {
    LOGGER.info("Node: " + node.getPath());
    Map<String, Node> children = node.getChildren();
    if (children == null) {
        return;
    }
    for (Node child : children.values()) {
        iterateChildren(child);
    }
   }
}
