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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;

/**
 * Burn through JSON so fast, you'll need to REST!
 * 
 * Use the methods <code>GET</code>, <code>PUT</code>, etc. to construct a
 * <code>Request</code> object to pass to the <code>Send</code> method. Use the
 * returned <code>Response</code> object to get HTTP status code and returned
 * content. Example:
 * 
 * <code>
 *	</br>JsonR8r jr = new JsonR8r();
 *	</br>Response rsp = jr.Send(jr.GET("/"));
 * </code>
 */
public class JsonR8r {

	private String _serverUrl;
	private HttpClient _httpClient;

	/**
	 * Construct a new <code>JsonR8r</code> with default server URL.
	 */
	public JsonR8r() {
		this(null);
	}

	/**
	 * Construct a new <code>JsonR8r</code> with provided <code>serverURL</code>.
	 */
	public JsonR8r(String serverUrl) {
		_serverUrl = serverUrl != null ? serverUrl : "http://localhost:5000";
		_httpClient = HttpClient.newHttpClient();
	}

	/**
	 * Send the provided <code>Request</code> via HTTP and return an associated
	 * <code>Response</code>.
	 */
	public Response Send(Request request) throws IOException, InterruptedException {
		HttpRequest httpRequest = null;
		switch (request._Method) {
		case "GET":
			httpRequest = HttpRequest.newBuilder(). //
					uri(URI.create(_serverUrl + request._Endpoint)). //
					headers(request._Headers). //
					GET().build();
			break;
		case "PUT":
			if (request._ContentIsFilePath) {
				httpRequest = HttpRequest.newBuilder(). //
						uri(URI.create(_serverUrl + request._Endpoint)). //
						headers(request._Headers). //
						PUT(BodyPublishers.ofFile(Path.of(request._Content))).build();
			} else {
				httpRequest = HttpRequest.newBuilder(). //
						uri(URI.create(_serverUrl + request._Endpoint)). //
						headers(request._Headers). //
						PUT(BodyPublishers.ofString(request._Content)).build();
			}
			break;
		case "POST":
			if (request._ContentIsFilePath) {
				httpRequest = HttpRequest.newBuilder(). //
						uri(URI.create(_serverUrl + request._Endpoint)). //
						headers(request._Headers). //
						POST(BodyPublishers.ofFile(Path.of(request._Content))).build();
			} else {
				httpRequest = HttpRequest.newBuilder(). //
						uri(URI.create(_serverUrl + request._Endpoint)). //
						headers(request._Headers). //
						POST(BodyPublishers.ofString(request._Content)).build();
			}
			break;
		case "DELETE":
			httpRequest = HttpRequest.newBuilder(). //
					uri(URI.create(_serverUrl + request._Endpoint)). //
					headers(request._Headers). //
					DELETE().build();
			break;
		}

		HttpResponse<String> response = _httpClient.send(httpRequest, BodyHandlers.ofString());
		return new Response(request, response);
	}

	/**
	 * Construct a GET request with the provided <code>endpoint</code>.
	 */
	public Request GET(String endpoint) {
		return new Request( //
				"GET", //
				endpoint, //
				new String[] { "accept", "application/json" } //
		);
	}

	/**
	 * Construct a PUT request with the provided <code>endpoint</code> and
	 * <code>content</code> text.
	 */
	public Request PUT(String endpoint, String content) {
		return PUT(endpoint, content, false);
	}

	/**
	 * Construct a PUT request with the provided <code>endpoint</code>. If
	 * <code>contentIsFilePath</code> is true, the value of <code>content</code> is
	 * interpreted as a path to a file with the content, otherwise the value of
	 * <code>content</code> is used as literal content text.
	 */
	public Request PUT(String endpoint, String content, boolean contentIsFilePath) {
		return new Request( //
				"PUT", //
				endpoint, //
				new String[] { //
						"accept", "application/json", //
						"content-type", "application/json" //
				}, //
				content, //
				contentIsFilePath //
		);
	}

	/**
	 * Construct a POST request with the provided <code>endpoint</code> and
	 * <code>content</code> text.
	 */
	public Request POST(String endpoint, String content) {
		return POST(endpoint, content, false);
	}

	/**
	 * Construct a POST request with the provided <code>endpoint</code>. If
	 * <code>contentIsFilePath</code> is true, the value of <code>content</code> is
	 * interpreted as a path to a file with the content, otherwise the value of
	 * <code>content</code> is used as literal content text.
	 */
	public Request POST(String endpoint, String content, boolean contentIsFilePath) {
		return new Request( //
				"POST", //
				endpoint, //
				new String[] { //
						"accept", "application/json", //
						"content-type", "application/json" //
				}, //
				content, //
				contentIsFilePath //
		);
	}

	/**
	 * Construct a DELETE request with the provided <code>endpoint</code>.
	 */
	public Request DELETE(String endpoint) {
		return new Request( //
				"DELETE", //
				endpoint, //
				new String[] { "accept", "application/json" } //
		);
	}

	/**
	 * Request encapsulates HTTP request details.
	 */
	public class Request {
		public final String _Method;
		public final String _Endpoint;
		public final String[] _Headers;
		public final String _Content;
		public final boolean _ContentIsFilePath;

		Request(String method, String endpoint, String[] headers, String content, boolean contentIsFilePath) {
			_Method = method;
			_Endpoint = endpoint;
			_Headers = headers;
			_Content = content;
			_ContentIsFilePath = contentIsFilePath;
		}

		Request(String method, String endpoint, String[] headers) {
			this(method, endpoint, headers, "", false);
		}
	}

	/**
	 * Response encapsulates HTTP response details.
	 */
	public class Response {
		public final Request _Request;
		public final HttpResponse<String> _HttpResponse;

		Response(Request request, HttpResponse<String> httpResponse) {
			_Request = request;
			_HttpResponse = httpResponse;
		}

		public int status() {
			return _HttpResponse.statusCode();
		}

		public String text() {
			return _HttpResponse.body();
		}

		public JsonValue json() {
			return JsonValue.readFrom(_HttpResponse.body());
		}
	}

}
