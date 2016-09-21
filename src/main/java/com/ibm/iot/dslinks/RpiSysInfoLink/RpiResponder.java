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
import org.dsa.iot.dslink.node.Node;
import org.dsa.iot.dslink.node.NodeBuilder;
import org.dsa.iot.dslink.node.value.Value;
import org.dsa.iot.dslink.node.value.ValueType;
import org.dsa.iot.dslink.util.Objects;
import org.dsa.iot.dslink.util.handler.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import com.pi4j.system.SystemInfo;
import com.pi4j.system.NetworkInfo;

public class RpiResponder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpiResponder.class);

    /**
     * Initializes the responder link.
     *
     * @param link Responder link to initialize.
     */
    public static void init(DSLink link) {
        NodeBuilder builder = link.getNodeManager().createRootNode("RpiSysInfo");
        builder.setAttribute("type", new Value("device"));
        builder.setAttribute("devicetype", new Value("attached"));
        Node node = builder.build();

        initCPUTempNode(node);
        initUsedMemNode(node);
        initFreeMemNode(node);
        initCachedMemNode(node);
        initHostnameNode(node);
        initIPAddrNode(node);
    }

    private static void initCPUTempNode(Node node) {
        NodeBuilder builder = node.createChild("CPU_Temperature");
        final Node child = builder.build();
        child.setValueType(ValueType.DYNAMIC);

        Objects.getDaemonThreadPool().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                       Value val = new Value(SystemInfo.getCpuTemperature());
                       val.setSerializable(false);
                       child.setValue(val);
                }
                catch(Exception e){ e.printStackTrace();}
            }
    }, 0, 1, TimeUnit.SECONDS);
   }

   private static void initUsedMemNode(Node node) {
       NodeBuilder builder = node.createChild("Used_Memory");
       final Node child = builder.build();
       child.setValueType(ValueType.DYNAMIC);

       Objects.getDaemonThreadPool().scheduleWithFixedDelay(new Runnable() {
           @Override
           public void run() {
               try {
                      Value val = new Value(SystemInfo.getMemoryUsed());
                      val.setSerializable(false);
                      child.setValue(val);
               }
               catch(Exception e){ e.printStackTrace();}
           }
   }, 0, 1, TimeUnit.SECONDS);
  }

  private static void initFreeMemNode(Node node) {
      NodeBuilder builder = node.createChild("Free_Memory");
      final Node child = builder.build();
      child.setValueType(ValueType.DYNAMIC);

      Objects.getDaemonThreadPool().scheduleWithFixedDelay(new Runnable() {
          @Override
          public void run() {
              try {
                     Value val = new Value(SystemInfo.getMemoryFree());
                     val.setSerializable(false);
                     child.setValue(val);
              }
              catch(Exception e){ e.printStackTrace();}
          }
  }, 0, 1, TimeUnit.SECONDS);
 }

 private static void initCachedMemNode(Node node) {
     NodeBuilder builder = node.createChild("Cached_Memory");
     final Node child = builder.build();
     child.setValueType(ValueType.DYNAMIC);

     Objects.getDaemonThreadPool().scheduleWithFixedDelay(new Runnable() {
         @Override
         public void run() {
             try {
                    Value val = new Value(SystemInfo.getMemoryCached());
                    val.setSerializable(false);
                    child.setValue(val);
             }
             catch(Exception e){ e.printStackTrace();}
         }
  }, 0, 1, TimeUnit.SECONDS);
 }

 private static void initHostnameNode(Node node) {
     NodeBuilder builder = node.createChild("Hostname");
     final Node child = builder.build();
     child.setValueType(ValueType.DYNAMIC);

     Objects.getDaemonThreadPool().scheduleWithFixedDelay(new Runnable() {
         @Override
         public void run() {
             try {
                    Value val = new Value(NetworkInfo.getHostname());
                    val.setSerializable(false);
                    child.setValue(val);
             }
             catch(Exception e){ e.printStackTrace();}
         }
  }, 0, 1, TimeUnit.SECONDS);
 }

 private static void initIPAddrNode(Node node) {
     NodeBuilder builder = node.createChild("IPAddress");
     final Node child = builder.build();
     child.setValueType(ValueType.DYNAMIC);

     Objects.getDaemonThreadPool().scheduleWithFixedDelay(new Runnable() {
         @Override
         public void run() {
             try {
                    Value val = new Value(NetworkInfo.getIPAddresses()[0]);
                    val.setSerializable(false);
                    child.setValue(val);
             }
             catch(Exception e){ e.printStackTrace();}
         }
  }, 0, 1, TimeUnit.SECONDS);
 }
}
