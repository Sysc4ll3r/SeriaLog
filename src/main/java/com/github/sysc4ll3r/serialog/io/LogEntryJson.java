package com.github.sysc4ll3r.serialog.io;

import burp.api.montoya.core.HighlightColor;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class LogEntryJson {

    @JsonProperty("url")
    private String url;

    @JsonProperty("request")
    private String request;

    @JsonProperty("response")
    private String response;

    @JsonProperty("time")
    private String time;

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("tool")
    private String tool;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("color")
    private HighlightColor color;


    public LogEntryJson() {
    }

    public LogEntryJson(String url, String request, String response, String time, String ip, String tool, String notes, HighlightColor color) {
        this.url = url;
        this.request = request;
        this.response = response;
        this.time = time;
        this.ip = ip;
        this.tool = tool;
        this.notes = notes;
        this.color = color;
    }
}
