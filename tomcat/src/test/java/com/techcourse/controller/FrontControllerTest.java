package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FrontControllerTest {
    private FrontController frontController;

    @BeforeEach
    void setUp() {
        frontController = FrontController.getInstance();
    }

    @DisplayName("올바른 리소스에 대해 200 응답을 반환한다.")
    @Test
    void findResource() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("GET /css/styles.css HTTP/1.1");
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        frontController.handle(httpRequest, httpResponse);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expectedResponseLine = "HTTP/1.1 200 OK \r\n";
        String expectedContentLength = "Content-Length: 211991 \r\n";
        String expectedContentType = "Content-Type: text/css \r\n";
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedContentLength,
                expectedContentType,
                expectedResponseBody
        );
    }

    @DisplayName("존재하지 않는 리소스에 접근하면 404 응답을 반환한다.")
    @Test
    void wrongURL() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("GET /unknown.html HTTP/1.1 ");
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        frontController.handle(httpRequest, httpResponse);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expectedResponseLine = "HTTP/1.1 404 NOT FOUND \r\n";
        String expectedContentLength = "Content-Length: 2426 \r\n";
        String expectedContentType = "Content-Type: text/html;charset=utf-8 \r\n";
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedContentLength,
                expectedContentType,
                expectedResponseBody
        );
    }

    @DisplayName("잘못된 메서드로 요청하면 405 응답을 반환한다.")
    @Test
    void wrongMethod() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("WRONG /login HTTP/1.1 ");
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        frontController.handle(httpRequest, httpResponse);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/405.html");
        String expectedResponseLine = "HTTP/1.1 405 METHOD NOT ALLOWED \r\n";
        String expectedContentType = "Content-Type: text/html;charset=utf-8 \r\n";
        String expectedContentLength = "Content-Length: 2190 \r\n";
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedContentType,
                expectedContentLength,
                expectedResponseBody
        );
    }
}
