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

package com.ibm.iot.dslinks.RpiSysInfoLink;

import org.dsa.iot.dslink.DSLink;
import org.dsa.iot.dslink.DSLinkHandler;
import org.dsa.iot.dslink.node.value.SubscriptionValue;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.util.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RpiRequester {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpiRequester.class);

    /**
     * Initializes the requester link.
     *
     * @param link Requester link to initialize.
     */
    public static void init(DSLink link) {
        subscribeTemp(link);
        subscribeUsedMem(link);
        subscribeFreeMem(link);
        subscribeCachedMem(link);
        subscribeHostname(link);
        subscribeIPAddr(link);
    }

    private static void subscribeTemp(DSLink link) {
        String path = link.getPath() + "/RpiSysInfo/CPU_Temperature";
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                float val = event.getValue().getNumber().intValue();
                //LOGGER.info("CPU Temperature {}", val);
            }
        });
    }

    private static void subscribeUsedMem(DSLink link) {
        String path = link.getPath() + "/RpiSysInfo/Used_Memory";
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                int val = event.getValue().getNumber().intValue();
                //LOGGER.info("Used Memory {}", val);
            }
        });
    }

    private static void subscribeFreeMem(DSLink link) {
        String path = link.getPath() + "/RpiSysInfo/Free_Memory";
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                int val = event.getValue().getNumber().intValue();
                //LOGGER.info("Free Memory {}", val);
            }
        });
    }

    private static void subscribeCachedMem(DSLink link) {
        String path = link.getPath() + "/RpiSysInfo/Cached_Memory";
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                int val = event.getValue().getNumber().intValue();
                //LOGGER.info("Cached Memory {}", val);
            }
        });
    }

    private static void subscribeHostname(DSLink link) {
        String path = link.getPath() + "/RpiSysInfo/Hostname";
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                String val = event.getValue().getString();
                //LOGGER.info("Hostname {}", val);
            }
        });
    }

    private static void subscribeIPAddr(DSLink link) {
        String path = link.getPath() + "/RpiSysInfo/IPAddress";
        link.getRequester().subscribe(path, new Handler<SubscriptionValue>() {
            @Override
            public void handle(SubscriptionValue event) {
                String val = event.getValue().getString();
                //LOGGER.info("IPAddress {}", val);
            }
        });
    }
}
