package org.apache.coyote.http11.response;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;

public class HttpResponse {
    private static final String HTTP11 = "HTTP/1.1";
    private static final CharSequence LINE_SEPARATOR = "\r\n";
    private static final String BLANK_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ": ";
    private static final CharSequence EMPTY = "";

    private String version;
    private HttpStatus status;
    private HttpHeaders headers;
    private ResponseBody body;

    public HttpResponse() {
        this.version = HTTP11;
        this.headers = new HttpHeaders();
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setCookie(String jSessionId) {
        headers.setCookie(jSessionId);
    }

    public void setLocation(String location) {
        headers.setLocation(location);
    }

    public void setContentType(MimeType type) {
        headers.setContentType(type.getType());
    }

    public void setBody(ResponseBody body) {
        headers.setContentLength(body.getLength());
        this.body = body;
    }

    public String serialize() {
        StringJoiner joiner = new StringJoiner(LINE_SEPARATOR);
        joiner.add(version + BLANK_DELIMITER + status.getMessage() + BLANK_DELIMITER);
        for (Entry<String, String> entry : headers.getHeaders().entrySet()) {
            joiner.add(entry.getKey() + HEADER_DELIMITER + entry.getValue() + BLANK_DELIMITER);
        }
        if (Objects.nonNull(body)) {
            joiner.add(EMPTY);
            joiner.add(body.getBody());
        }
        return joiner.toString();
    }
}
