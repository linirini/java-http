package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;

public class HttpResponse {
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String HTTP11 = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String LOCATION = "Location";

    private String version;
    private HttpStatus status;
    private Map<String, String> headers;
    private ResponseBody body;

    public HttpResponse() {
        this.version = HTTP11;
        this.headers = new HashMap<>();
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setCookie(String jSessionId) {
        addHeader(COOKIE, jSessionId);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setContentType(MimeType type) {
        addHeader(CONTENT_TYPE, type.getType());
    }

    public void setBody(ResponseBody body) {
        addHeader(CONTENT_LENGTH, String.valueOf(body.getLength()));
        this.body = body;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\r\n");
        joiner.add(version + " " + status.getMessage()+" ");
        for (Entry<String, String> entry : headers.entrySet()) {
            joiner.add(entry.getKey() + ": " + entry.getValue() + " ");
        }
        if(Objects.nonNull(body)) {
            joiner.add("");
            joiner.add(body.getBody());
        }
        return joiner.toString();
    }

    public void setLocation(String location) {
        headers.put(LOCATION, location);
    }
}
