/*********************************************************************
* Copyright (c) 2022 Chris J Daly (github user cjdaly)
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package net.locosoft.jsonr8r;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.junit.jupiter.api.Test;

import net.locosoft.jsonr8r.JsonR8r.Response;

/**
 * Note! These tests run against the REST_area server
 * (https://github.com/cjdaly/REST_area).
 */
class TestJsonR8r {

	@Test
	void GET() throws IOException, InterruptedException {
		JsonR8r j = new JsonR8r();
		Response rsp = j.Send(j.GET("/"));
		assertEquals(200, rsp.status());
		assertTrue(rsp.text().startsWith("Hello from REST_area server"));
	}

	@Test
	void GET_serverURL() throws IOException, InterruptedException {
		JsonR8r j = new JsonR8r("http://localhost:5000");
		Response rsp = j.Send(j.GET("/"));
		assertEquals(200, rsp.status());
		assertTrue(rsp.text().startsWith("Hello from REST_area server"));
	}

	@Test
	void GET_PUT_DELETE_Text() throws IOException, InterruptedException {
		JsonR8r j = new JsonR8r();
		Response rsp;
		String prop = "/props/JSONR8R_testPUT_Text";
		String content = "Hello World!";

		// check property is initially not defined
		rsp = j.Send(j.GET(prop));
		assertEquals(404, rsp.status());

		// PUT content to property
		rsp = j.Send(j.PUT(prop, content));
		assertEquals(200, rsp.status());

		// GET and confirm property now defined
		rsp = j.Send(j.GET(prop));
		assertEquals(200, rsp.status());
		assertEquals(content, rsp.text());

		// DELETE property
		rsp = j.Send(j.DELETE(prop));
		assertEquals(200, rsp.status());

		// confirm property was deleted
		rsp = j.Send(j.GET(prop));
		assertEquals(404, rsp.status());
	}

	@Test
	void GET_PUT_DELETE_File() throws IOException, InterruptedException {
		JsonR8r j = new JsonR8r();
		Response rsp;
		String prop = "/props/JSONR8R_testPUT_File";
		String filepath = "testdata/menu.json";

		// check property is initially not defined
		rsp = j.Send(j.GET(prop));
		assertEquals(404, rsp.status());

		// PUT file to property
		rsp = j.Send(j.PUT(prop, filepath, true));
		assertEquals(200, rsp.status());

		// GET and confirm property now defined
		rsp = j.Send(j.GET(prop));
		assertEquals(200, rsp.status());
		// TODO: check against file contents!
		// assertEquals(, rsp.text());

		// check JSON parsing
		JsonValue json = rsp.json();
		assertNotNull(json);
		assertTrue(json.isObject());

		// DELETE property
		rsp = j.Send(j.DELETE(prop));
		assertEquals(200, rsp.status());

		// confirm property was deleted
		rsp = j.Send(j.GET(prop));
		assertEquals(404, rsp.status());
	}

	private static final Pattern MSG_NUM_REGEX = Pattern.compile("^New message #(\\d+).*");

	@Test
	void POST_GET_Text() throws IOException, InterruptedException {
		JsonR8r j = new JsonR8r();
		Response rsp;
		String content = "Hello World!";

		// POST content to messages
		rsp = j.Send(j.POST("/msgs", content));
		assertEquals(200, rsp.status());

		// get the message number from the response
		assertTrue(rsp.text().startsWith("New message #"));
		Matcher matcher = MSG_NUM_REGEX.matcher(rsp.text());
		assertTrue(matcher.matches());
		String msgNum = matcher.group(1);

		// GET the posted message by number
		rsp = j.Send(j.GET("/msgs/" + msgNum));
		assertEquals(200, rsp.status());
		assertEquals(content, rsp.text());
	}

	@Test
	void POST_GET_File() throws IOException, InterruptedException {
		JsonR8r j = new JsonR8r();
		Response rsp;
		String filepath = "testdata/menu.json";

		// POST file to messages
		rsp = j.Send(j.POST("/msgs", filepath, true));
		assertEquals(200, rsp.status());

		// get the message number from the response
		assertTrue(rsp.text().startsWith("New message #"));
		Matcher matcher = MSG_NUM_REGEX.matcher(rsp.text());
		assertTrue(matcher.matches());
		String msgNum = matcher.group(1);

		// GET the posted message by number
		rsp = j.Send(j.GET("/msgs/" + msgNum));
		assertEquals(200, rsp.status());
		// TODO: check against file contents!
		// assertEquals(, rsp.text());

		// check JSON parsing
		JsonValue json = rsp.json();
		assertNotNull(json);
		assertTrue(json.isObject());
	}

}
