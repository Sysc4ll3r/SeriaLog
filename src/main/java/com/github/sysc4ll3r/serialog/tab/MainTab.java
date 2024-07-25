package com.github.sysc4ll3r.serialog.tab;

import burp.api.montoya.proxy.ProxyWebSocketMessage;
import burp.api.montoya.proxy.websocket.ProxyWebSocket;
import burp.api.montoya.proxy.websocket.ProxyWebSocketCreation;
import burp.api.montoya.proxy.websocket.ProxyWebSocketCreationHandler;
import burp.api.montoya.websocket.TextMessage;
import burp.api.montoya.websocket.TextMessageAction;

import javax.swing.*;

import static com.github.sysc4ll3r.serialog.extension.SeriaLogExtension.montoyaApi;

public class MainTab extends JComponent {

    public MainTab() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JTabbedPane maintab = new JTabbedPane();
        maintab.addTab("Logs", new JScrollPane(new LogTab()));
        maintab.addTab("Settings", new JScrollPane(new SettingsTab()));
        add(maintab);
    }
}
