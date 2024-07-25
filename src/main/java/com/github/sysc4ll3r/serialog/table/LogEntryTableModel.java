package com.github.sysc4ll3r.serialog.table;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class LogEntryTableModel extends AbstractTableModel {

    private final String[] columnNames = {
            "#", "Tool", "Host", "Method", "URL", "Params", "Status Code", "Length", "MIME type",
            "Extension", "Cookies", "Notes", "TLS", "IP", "Time"};
    public List<LogEntry> logEntries;

    public LogEntryTableModel(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @Override
    public synchronized int getRowCount() {
        return logEntries.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex) {
        LogEntry entry = logEntries.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return entry.getTool();
            case 2:
                return entry.getUrl();
            case 3:
                return entry.getMethod();
            case 4:
                return entry.getPath();
            case 5:
                return entry.hasParams();
            case 6:
                return entry.getStatus();
            case 7:
                return entry.getResponseLength();
            case 8:
                return entry.getMimeType();
            case 9:
                return entry.getExtension();
            case 10:
                return entry.hasCookies();
            case 11:
                return entry.getNotes();
            case 12:
                return entry.hasTls();
            case 13:
                return entry.getIp();
            case 14:
                return entry.getTime();

            default:
                return null;
        }
    }

    public synchronized LogEntry get(int rowIndex) {
        return logEntries.get(rowIndex);
    }

}
