/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.routing;

import org.mule.api.ExceptionPayload;
import org.mule.api.MessagingException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FirstSuccessfulTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "first-successful-test.xml";
    }

    @Test
    public void testFirstSuccessful() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage response = client.send("vm://input", "XYZ", null);
        assertEquals("XYZ is a string", response.getPayloadAsString());

        response = client.send("vm://input", Integer.valueOf(9), null);
        assertEquals("9 is an integer", response.getPayloadAsString());

        response = client.send("vm://input", Long.valueOf(42), null);
        assertEquals("42 is a number", response.getPayloadAsString());

        response = client.send("vm://input", Boolean.TRUE, null);
        ExceptionPayload ep = response.getExceptionPayload();
        assertNotNull(ep);
        Throwable ex = ep.getException();
        assertTrue(ex instanceof MessagingException);
    }

    @Test
    public void testFirstSuccessfulWithExpression() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage response = client.send("vm://input2", "XYZ", null);
        assertEquals("XYZ is a string", response.getPayloadAsString());
    }

    @Test
    public void testFirstSuccessfulWithExpressionAllFail() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage response = client.send("vm://input3", "XYZ", null);
        ExceptionPayload ep = response.getExceptionPayload();
        assertNotNull(ep);
        Throwable ex = ep.getException();
        assertTrue(ex instanceof MessagingException);
    }

    @Test
    public void testFirstSuccessfulWithOneWayEndpoints() throws Exception
    {
        MuleClient client = muleContext.getClient();
        client.dispatch("vm://input4.in", TEST_MESSAGE, null);

        MuleMessage response = client.request("vm://output4.out", RECEIVE_TIMEOUT);
        assertNotNull(response);
        assertEquals(TEST_MESSAGE, response.getPayload());
    }
}
