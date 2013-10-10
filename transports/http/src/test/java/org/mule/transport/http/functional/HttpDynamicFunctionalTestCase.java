/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.functional;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpDynamicFunctionalTestCase extends FunctionalTestCase
{
    protected static String TEST_REQUEST = "Test Http Request";

    @Rule
    public DynamicPort dynamicPort1 = new DynamicPort("port1");

    @Rule
    public DynamicPort dynamicPort2 = new DynamicPort("port2");

    @Override
    protected String getConfigResources()
    {
        return "http-dynamic-functional-test.xml";
    }

    @Test
    public void testSend() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("port", dynamicPort1.getNumber());
        props.put("path", "foo");

        MuleMessage result = client.send("clientEndpoint", TEST_REQUEST, props);
        assertEquals(TEST_REQUEST + " Received 1", result.getPayloadAsString());

        props.put("port", dynamicPort2.getNumber());
        result = client.send("clientEndpoint", TEST_REQUEST, props);
        assertEquals(TEST_REQUEST + " Received 2", result.getPayloadAsString());
    }
}
