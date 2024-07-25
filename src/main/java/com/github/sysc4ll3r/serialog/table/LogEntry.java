package com.github.sysc4ll3r.serialog.table;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.github.sysc4ll3r.serialog.io.LogEntrySerial;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;


@Getter
@Setter
public final class LogEntry {
    private ToolType tool;
    private String time;
    private String ip;
    private HttpRequestResponse requestResponse;


    public LogEntry(HttpRequestResponse requestResponse, ToolType tool) {
        this.ip = requestResponse.httpService().ipAddress();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        this.time = sdf.format(new Date());
        this.requestResponse = requestResponse;
        this.tool = tool;
    }

    public LogEntry(LogEntrySerial logEntrySerial) {
        this.ip = logEntrySerial.getIp();
        this.time = logEntrySerial.getTime();
        HttpRequest request = HttpRequest.httpRequest(
                ByteArray.byteArray(
                        logEntrySerial.getRequest()
                )
        ).withService(
                HttpService.httpService(
                        logEntrySerial.getUrl()
                )
        );
        HttpResponse response = null;
        if (logEntrySerial.getResponse() != null) {
            response = HttpResponse.httpResponse(
                    ByteArray.byteArray(
                            logEntrySerial.getResponse()
                    )
            );
        }
        this.requestResponse = HttpRequestResponse.httpRequestResponse(request, response)
                .withAnnotations(Annotations.annotations(
                        logEntrySerial.getNotes(),
                        logEntrySerial.getColor()
                ));
        this.tool = logEntrySerial.getTool();
    }

    public String getNotes() {
        return this.requestResponse.annotations().notes();
    }

    public void setNotes(String comment) {
        this.requestResponse.annotations().setNotes(comment);//.withAnnotations(Annotations.annotations(comment, requestResponse.annotations().highlightColor()));
    }

    public String getPath() {
        return this.requestResponse.request().path();
    }

    public String getUrl() {
        return this.requestResponse.request().url();
    }


    public String getStatus() {
        HttpResponse response = this.requestResponse.response();
        return (response != null) ? String.valueOf(response.statusCode()) : "";
    }


    public String hasTls() {
        return this.requestResponse.httpService().secure() ? "✔" : "";
    }

    public String getResponseLength() {
        HttpResponse response = this.requestResponse.response();
        return (response != null) ? String.valueOf(response.toByteArray().length()) : "";
    }

    public String getExtension() {
        return this.requestResponse.request().fileExtension();
    }

    public String hasCookies() {
        return this.requestResponse.request().hasParameters(HttpParameterType.COOKIE) ? "✔" : "";
    }

    public String getMimeType() {
        HttpResponse response = this.requestResponse.response();
        return (response != null) ? response.inferredMimeType().name() : "";
    }

    public String getMethod() {
        return this.requestResponse.request().method();
    }

    public String hasParams() {
        return this.requestResponse.request().hasParameters() ? "✔" : "";
    }
}

