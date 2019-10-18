package org.lemon.http.server;

import java.util.ArrayList;
import java.util.List;

public class HttpRequestMessage {

    private String requestLine;
    private List<String> headers = new ArrayList<String>();
    private String body;

    public String getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
