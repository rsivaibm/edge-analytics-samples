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
import org.dsa.iot.dslink.DSLinkFactory;
import org.dsa.iot.dslink.DSLinkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends DSLinkHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private RpiUnoResponder linkResponder;

    @Override
    public boolean isRequester() {
        return true;
    }

    @Override
    public boolean isResponder() {
        return true;
    }

    @Override
    public void onResponderInitialized(DSLink link) {
        linkResponder = new RpiUnoResponder();
        linkResponder.init(link);
        LOGGER.info("Rpi Uno Responder initialized");
    }

    @Override
    public void onRequesterConnected(final DSLink link) {
        RpiUnoRequester.init(link);
        LOGGER.info("Rpi Uno Requester initialized");
    }

    @Override
    public void onResponderDisconnected(DSLink link) {
        linkResponder.close();
        LOGGER.info("Rpi Uno Responder Disconnected");
    }

    @Override
    public void onRequesterDisconnected(DSLink link) {
        LOGGER.info("Rpi Uno Requester Disconnected");
    }

    public static void main(String[] args) {
        DSLinkFactory.start(args, new Main());
    }
}
