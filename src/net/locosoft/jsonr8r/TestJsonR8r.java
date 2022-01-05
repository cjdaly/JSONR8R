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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.locosoft.jsonr8r.JsonR8r.Response;

/**
 * Note! These tests run against the REST_area server
 * (https://github.com/cjdaly/REST_area).
 */
class TestJsonR8r {

	@Test
	void testGET() throws IOException, InterruptedException {
		JsonR8r j = new JsonR8r();
		Response rsp = j.Send(j.GET("/"));
		assertEquals(200, rsp.status());
		assertTrue(rsp.text().startsWith("Hello from REST_area server"));
	}

	@Test
	void testPUT_Text() {
		fail("Not yet implemented");
	}

	@Test
	void testPUT_File() {
		fail("Not yet implemented");
	}

	@Test
	void testPOST_Text() {
		fail("Not yet implemented");
	}

	@Test
	void testPOST_File() {
		fail("Not yet implemented");
	}

	@Test
	void testDELETE() {
		fail("Not yet implemented");
	}

}
