package com.github.sysc4ll3r.serialog.extension;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.github.sysc4ll3r.serialog.tab.LogTab;
import com.github.sysc4ll3r.serialog.table.LogEntry;

import java.util.Set;

public class LoggerHttpHandler implements HttpHandler {
    private final Set<ToolType> tools;
    private final Boolean inScopeOnly;

    public LoggerHttpHandler(Set<ToolType> tools, Boolean inScopeOnly) {
        this.tools = tools;
        this.inScopeOnly = inScopeOnly;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        if (this.inScopeOnly && !responseReceived.initiatingRequest().isInScope())
            return ResponseReceivedAction.continueWith(responseReceived);

        if (tools.contains(responseReceived.toolSource().toolType())) {
            HttpRequest request = responseReceived.initiatingRequest();
            HttpResponse response = responseReceived;
            HttpRequestResponse requestResponse = HttpRequestResponse.httpRequestResponse(request, response)
                    .withAnnotations(responseReceived.annotations());
            LogTab.addLogEntry(new LogEntry(requestResponse, responseReceived.toolSource().toolType()));
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}
