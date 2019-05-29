package mk.learning.utilities;

import java.util.HashMap;

import mk.learning.httpserver.Server;

public class HttpResponse {
	String httpVersion;
	String httpStatusCode;
	String httpReasonPhrase;
	HashMap<String, String> headers;
	String body;

	public HttpResponse() {
		headers = new HashMap<String, String>();
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public String getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(String httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public String getHttpReasonPhrase() {
		return httpReasonPhrase;
	}

	public void setHttpReasonPhrase(String httpReasonPhrase) {
		this.httpReasonPhrase = httpReasonPhrase;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String key,String value) {
		this.headers.put(key, value);
	}
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getHttpResponse() {
		StringBuilder response = new StringBuilder();
		response.append(this.httpVersion+" "+this.httpStatusCode+" "+this.httpReasonPhrase);
		response.append("\n");
		for(String key: this.headers.keySet()) {
			response.append(key+": "+this.headers.get(key));
			response.append("\n");
		}
		response.append("\n");
		response.append(this.body);
		Server.logger.info("Http response is: \n"+response.toString());
		return response.toString();
	}
	
}
