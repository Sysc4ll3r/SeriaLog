package com.github.sysc4ll3r.serialog.io;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.core.ToolType;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import com.github.sysc4ll3r.serialog.table.LogEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;


public final class DataTransferUtil {

    private static final Kryo kryo = new Kryo();

    static {
        kryo.register(java.util.ArrayList.class);
        kryo.register(LogEntrySerial.class);
        kryo.register(byte[].class);
        kryo.register(ToolType.class);
        kryo.register(HighlightColor.class);
    }

    private DataTransferUtil() {
    }

    public static List<LogEntry> importLogEntriesFromXml(File inputFile, Predicate<LogEntry> predicate) throws Exception {
        List<LogEntry> logEntries = new ArrayList<>();

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputFile);

        NodeList itemList = document.getElementsByTagName("item");

        for (int i = 0; i < itemList.getLength(); i++) {
            Element itemElement = (Element) itemList.item(i);

            String time = itemElement.getElementsByTagName("time").item(0).getTextContent();
            String url = itemElement.getElementsByTagName("url").item(0).getTextContent();
            Element hostElement = (Element) itemElement.getElementsByTagName("host").item(0);
            String ip = hostElement.getAttribute("ip");
            String tool = "Extensions";
            if (itemElement.getElementsByTagName("tool").getLength() != 0) {
                tool = itemElement.getElementsByTagName("tool").item(0).getTextContent();
            }
            String notes = itemElement.getElementsByTagName("comment").item(0).getTextContent();
            byte[] requestBytes = Base64.getDecoder().decode(itemElement.getElementsByTagName("request").item(0).getTextContent());
            byte[] responseBytes = null;
            if (itemElement.getElementsByTagName("response").getLength() != 0) {
                responseBytes = Base64.getDecoder().decode(itemElement.getElementsByTagName("response").item(0).getTextContent());
            }
            String color = "NONE";
            if (itemElement.getElementsByTagName("color").getLength() != 0) {
                color = itemElement.getElementsByTagName("color").item(0).getTextContent();
            }

            LogEntry logEntry = new LogEntry(new LogEntrySerial(url, requestBytes, responseBytes, time, ip, tool, notes, HighlightColor.valueOf(color)));
            if (predicate.test(logEntry)) {
                logEntries.add(logEntry);
            }
        }

        return logEntries;
    }

    public static void exportLogEntriesToXml(File outputFile, List<LogEntry> logEntries, Predicate<LogEntry> predicate) throws Exception {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("items");
        document.appendChild(root);

        for (LogEntry logEntry : logEntries) {
            if (predicate.test(logEntry)) {
                Element item = document.createElement("item");

                Element time = document.createElement("time");
                time.appendChild(document.createTextNode(logEntry.getTime()));
                item.appendChild(time);

                Element url = document.createElement("url");
                url.appendChild(document.createCDATASection(logEntry.getRequestResponse().request().toString()));
                item.appendChild(url);

                Element host = document.createElement("host");
                host.setAttribute("ip", logEntry.getIp());
                item.appendChild(host);


                Element request = document.createElement("request");
                request.setAttribute("base64", "true");
                String base64Request = Base64.getEncoder().encodeToString(logEntry.getRequestResponse().request().toByteArray().getBytes());
                request.appendChild(document.createCDATASection(base64Request));
                item.appendChild(request);

                if (logEntry.getRequestResponse().hasResponse()) {
                    Element response = document.createElement("response");
                    response.setAttribute("base64", "true");
                    String base64Response = Base64.getEncoder().encodeToString(logEntry.getRequestResponse().response().toByteArray().getBytes());
                    response.appendChild(document.createCDATASection(base64Response));
                    item.appendChild(response);
                }

                Element comment = document.createElement("comment");
                comment.appendChild(document.createTextNode(logEntry.getNotes()));
                item.appendChild(comment);
                Element tool = document.createElement("tool");
                tool.appendChild(document.createTextNode(logEntry.getTool().toString()));
                item.appendChild(tool);
                Element color = document.createElement("color");
                color.appendChild(document.createTextNode(String.valueOf(logEntry.getRequestResponse().annotations().highlightColor())));
                item.appendChild(color);

                root.appendChild(item);
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(outputFile);
        transformer.transform(domSource, streamResult);
    }


    public static List<LogEntry> importLogEntriesSerialized(File inputFile, Predicate<LogEntry> predicate) throws IOException {
        List<LogEntry> logEntries = new ArrayList<>();
        try (Input input = new Input(new ZstdInputStream(new FileInputStream(inputFile)))) {
            @SuppressWarnings("unchecked")
            List<LogEntrySerial> serializableLogEntries = kryo.readObject(input, ArrayList.class);
            for (LogEntrySerial serialEntry : serializableLogEntries) {
                LogEntry logEntry = new LogEntry(serialEntry);
                if (predicate.test(logEntry)) {
                    logEntries.add(logEntry);
                }
            }
        }
        return logEntries;
    }

    public static void exportLogEntriesSerialized(File outputFile, List<LogEntry> logEntries, Predicate<LogEntry> predicate) throws Exception {
        try (Output output = new Output(new ZstdOutputStream(new FileOutputStream(outputFile)))) {
            List<LogEntrySerial> serializableLogEntries = new ArrayList<>();
            for (LogEntry logEntry : logEntries) {
                if (predicate.test(logEntry)) {
                    serializableLogEntries.add(new LogEntrySerial(logEntry));
                }
            }
            kryo.writeObject(output, serializableLogEntries);
        }
        System.gc();
    }



/*
// Java Serialization - i replaced it with kryo + zstd
    public static List<LogEntry> importLogEntriesSerialized(File inputFile, Predicate<LogEntry> predicate) throws IOException, ClassNotFoundException {
        List<LogEntry> logEntries = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {
            @SuppressWarnings("unchecked")
            List<LogEntrySerial> serializableLogEntries = (List<LogEntrySerial>) ois.readObject();
            for (LogEntrySerial serialEntry : serializableLogEntries) {
                LogEntry logEntry = new LogEntry(serialEntry);
                if (predicate.test(logEntry)) {
                    logEntries.add(logEntry);
                }
            }
        }
        System.gc();
        return logEntries;
    }

    public static void exportLogEntriesSerialized( File outputFile,List<LogEntry> logEntries ,Predicate<LogEntry> predicate) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {
            List<LogEntrySerial> serializableLogEntries = new ArrayList<>();
            for (LogEntry logEntry : logEntries) {
                if (predicate.test(logEntry)) {
                    serializableLogEntries.add(new LogEntrySerial(logEntry));
                }
            }
            oos.writeObject(serializableLogEntries);
        }
        System.gc();
    }

 */

    public static List<LogEntry> importLogEntriesFromJson(File inputFile, Predicate<LogEntry> predicate) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<LogEntry> logEntries = new ArrayList<>();
        LogEntryJson[] jsonEntries = objectMapper.readValue(inputFile, LogEntryJson[].class);

        for (LogEntryJson jsonEntry : jsonEntries) {
            LogEntry logEntry = new LogEntry(new LogEntrySerial(
                    jsonEntry.getUrl(),
                    Base64.getDecoder().decode(jsonEntry.getRequest()),
                    jsonEntry.getResponse() != null ? Base64.getDecoder().decode(jsonEntry.getResponse()) : null,
                    jsonEntry.getTime(),
                    jsonEntry.getIp(),
                    jsonEntry.getTool(),
                    jsonEntry.getNotes(),
                    jsonEntry.getColor()
            ));
            if (predicate.test(logEntry)) {
                logEntries.add(logEntry);
            }
        }
        return logEntries;
    }


    public static void exportLogEntriesToJson(File outputFile, List<LogEntry> logEntries, Predicate<LogEntry> predicate) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<LogEntryJson> jsonEntries = new ArrayList<>();

        for (LogEntry logEntry : logEntries) {
            if (predicate.test(logEntry)) {
                LogEntryJson jsonEntry = new LogEntryJson(
                        logEntry.getUrl(),
                        Base64.getEncoder().encodeToString(logEntry.getRequestResponse().request().toByteArray().getBytes()),
                        logEntry.getRequestResponse().hasResponse() ? Base64.getEncoder().encodeToString(logEntry.getRequestResponse().response().toByteArray().getBytes()) : null,
                        logEntry.getTime(),
                        logEntry.getIp(),
                        logEntry.getTool().toString(),
                        logEntry.getNotes(),
                        logEntry.getRequestResponse().annotations().highlightColor()
                );
                jsonEntries.add(jsonEntry);
            }
        }

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, jsonEntries);
    }

    public static String importString(File inputFile) throws IOException {
        Path filePath = inputFile.toPath();
        return Files.readString(filePath);
    }

    public static void exportString(File outputFile, String data) throws IOException {
        Path filePath = outputFile.toPath();
        Files.writeString(filePath, data);
    }
}