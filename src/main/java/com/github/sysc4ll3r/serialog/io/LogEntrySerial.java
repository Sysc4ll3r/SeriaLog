package com.github.sysc4ll3r.serialog.io;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.core.ToolType;
import com.github.sysc4ll3r.serialog.table.LogEntry;
import lombok.Getter;

import java.io.Serializable;

@Getter
public final class LogEntrySerial implements Serializable {
    private static final long serialVersionUID = 1L;
    private byte[] request;
    private byte[] response;
    private String ip;
    private String notes;
    private String url;
    private String time;
    private ToolType tool;
    private HighlightColor color;

    public LogEntrySerial(String url, byte[] request, byte[] response, String time, String ip, String tool, String notes, HighlightColor color) {
        this.url = url;
        this.time = time;
        this.ip = ip;
        this.request = request;
        this.response = response;
        this.tool = ToolType.valueOf(tool);
        this.notes = notes;
        this.color = color;
    }


    public LogEntrySerial(LogEntry logEntry) {
        this.request = logEntry.getRequestResponse().request().toByteArray().getBytes();
        if (logEntry.getRequestResponse().hasResponse())
            this.response = logEntry.getRequestResponse().response().toByteArray().getBytes();
        this.ip = logEntry.getIp();
        this.url = logEntry.getUrl();
        this.time = logEntry.getTime();
        this.notes = logEntry.getNotes();
        this.tool = logEntry.getTool();
        this.color = logEntry.getRequestResponse().annotations().highlightColor();
    }

    public LogEntrySerial() {
    }
}
