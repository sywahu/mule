/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.tcp.issues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.transport.DispatchException;

import java.net.InetAddress;
import java.net.SocketException;

import org.junit.Test;

/**
 * Tests how sockets are bound to addresses by the TCP transport. This test is related to MULE-6584.
 */
public class TcpSocketToAddressBindingTestCase extends AbstractTcpSocketToAddressBindingTestCase
{
    public TcpSocketToAddressBindingTestCase() throws SocketException
    {
        super();
    }

    @Test
    public void testRequestUsingLoopbackAddressAtLoopbackAddress() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result;

        // Request using loopback address at endpoint listening at 127.0.0.1 should get an appropiate response.
        result = client.send(getTransportName()+"://127.0.0.1:"+dynamicPort1.getNumber(), TEST_MESSAGE, null);
        assertEquals(TEST_MESSAGE + " Received", result.getPayloadAsString());
    }

    @Test
    public void testRequestUsingLocalhostAtLocalhost() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result;

        // Request using localhost address at endpoint listening at localhost should get an appropiate response.
        result = client.send(getTransportName()+"://localhost:"+dynamicPort2.getNumber(), TEST_MESSAGE, null);
        assertEquals(TEST_MESSAGE + " Received", result.getPayloadAsString());
    }

    @Test
    public void testRequestUsingLoopbackAddressAtAllAddresses() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result;

        // Request using loopback address at endpoint listening at all addresses should get an appropiate response.
        result = client.send(getTransportName()+"://127.0.0.1:"+dynamicPort3.getNumber(), TEST_MESSAGE, null);
        assertEquals(TEST_MESSAGE + " Received", result.getPayloadAsString());
    }

    @Test
    public void testRequestNotUsingLoopbackAddressAtLoopbackAddress() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result;

        // Iterate over local addresses.
        for (InetAddress inetAddress : localInetAddresses)
        {
            // Request not using loopback address to endpoint listening at 127.0.0.1 should timeout.
            try
            {
                result = client.send(getTransportName()+"://"+inetAddress.getHostAddress()+":"+dynamicPort1.getNumber(), TEST_MESSAGE, null);
                assertNull(result);
            }
            catch (DispatchException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testRequestNotUsingLoopbackAddressAtAllAddresses() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result;

        // Iterate over local addresses.
        for (InetAddress inetAddress : localInetAddresses)
        {
            /* Request not using loopback address to endpoint listening at all local addresses should get an
             * appropriate response. */
            result = client.send(getTransportName()+"://"+inetAddress.getHostAddress()+":"+dynamicPort3.getNumber(), new DefaultMuleMessage(TEST_MESSAGE, muleContext));
            assertEquals(TEST_MESSAGE + " Received", result.getPayloadAsString());
        }
    }
}
