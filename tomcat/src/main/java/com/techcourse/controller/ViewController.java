package com.techcourse.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.ResponseBody;

import com.techcourse.exception.InvalidResourceException;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.exception.UnsupportedMethodException;

import javassist.NotFoundException;

public class ViewController extends Controller {
    private static final String DEFAULT_EXTENSION = MimeType.HTML.getExtension();
    private static final ViewController instance = new ViewController();

    private ViewController() {
    }

    public static ViewController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        String fileName = getFileName(request.getURI());
        ResponseBody responseBody = new ResponseBody(getResponseBody(fileName));
        response.setStatus(HttpStatus.OK);
        response.setContentType(MimeType.HTML);
        response.setBody(responseBody);
        return response;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }

    private String getFileName(String endpoint) {
        int index = endpoint.indexOf("?");
        String path = endpoint;
        if (index != -1) {
            path = path.substring(0, index);
        }
        String fileName = path.substring(1);
        if (fileName.isEmpty()) {
            fileName = "hello.html";
        }
        if (isWithoutExtension(fileName)) {
            fileName = String.join(".", fileName, DEFAULT_EXTENSION);
        }
        return fileName;
    }

    private boolean isWithoutExtension(String fileName) {
        return !fileName.contains(".");
    }

    private String getResponseBody(String fileName) throws IOException {
        URL resource = findResource(fileName);
        if (Objects.isNull(resource)) {
            throw new InvalidResourceException("Cannot find resource with name: " + fileName);
        }
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private URL findResource(String fileName) {
        return getClass().getClassLoader().getResource("static/" + fileName);
    }

}

