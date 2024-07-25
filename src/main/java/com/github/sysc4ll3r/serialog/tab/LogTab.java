package com.github.sysc4ll3r.serialog.tab;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import com.github.sysc4ll3r.serialog.dialog.FilterDialog;
import com.github.sysc4ll3r.serialog.io.DataTransferUtil;
import com.github.sysc4ll3r.serialog.table.LogEntry;
import com.github.sysc4ll3r.serialog.table.LogEntryTableModel;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;
import static com.github.sysc4ll3r.serialog.extension.SeriaLogExtension.montoyaApi;

public class LogTab extends JComponent {
    public static List<LogEntry> logEntries;
    private static LogEntryTableModel logEntryModel;
    private static JTable displayedTable;
    private final HttpRequestEditor requestViewer;
    private final HttpResponseEditor responseViewer;
    private final JTextArea notesArea;
    private FilterDialog filterDialog;


    @SuppressWarnings("all")
    public LogTab() {
        jiconfont.swing.IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        logEntries = new ArrayList<>();
        setLayout(new BorderLayout());
        requestViewer = montoyaApi.userInterface().createHttpRequestEditor(READ_ONLY);
        responseViewer = montoyaApi.userInterface().createHttpResponseEditor(READ_ONLY);
        JPanel topPanel = new JPanel();

        JButton filterButton = new JButton("Filter", jiconfont.swing.IconFontSwing.buildIcon(GoogleMaterialDesignIcons.SEARCH, 20, Color.GRAY));
        filterButton.addActionListener(e -> {
            if (filterDialog == null) {
                filterDialog = new FilterDialog((Frame) SwingUtilities.getWindowAncestor(this));
            }
            filterDialog.setVisible(!filterDialog.isVisible());
        });
        topPanel.add(filterButton);


        JButton callJvmGcButton = new JButton("Call JVM GC", jiconfont.swing.IconFontSwing.buildIcon(GoogleMaterialDesignIcons.DELETE_SWEEP, 20, Color.GRAY));
        callJvmGcButton.addActionListener(e -> System.gc());
        topPanel.add(callJvmGcButton);


        add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        notesArea = new JTextArea();
        notesArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLogEntryNotes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLogEntryNotes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLogEntryNotes();
            }
        });


        logEntryModel = new LogEntryTableModel(logEntries);
        displayedTable = new JTable(logEntryModel) {
            @Override
            public synchronized void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
                int modelRow = convertRowIndexToModel(rowIndex);
                LogEntry entry = logEntryModel.get(modelRow);
                requestViewer.setRequest(entry.getRequestResponse().request());
                responseViewer.setResponse(entry.getRequestResponse().response());
                notesArea.setText(entry.getNotes());
            }

            @Override
            public synchronized Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int modelRow = convertRowIndexToModel(row);
                LogEntry entry = logEntryModel.get(modelRow);

                Color highlightColor;
                Color foregroundColor;

                if (entry.getRequestResponse().annotations().hasHighlightColor()) {
                    switch (entry.getRequestResponse().annotations().highlightColor()) {
                        case HighlightColor.YELLOW -> {
                            highlightColor = ColorPalette.YELLOW;
                            foregroundColor = Color.BLACK;
                        }
                        case HighlightColor.BLUE -> {
                            highlightColor = ColorPalette.BLUE;
                            foregroundColor = Color.WHITE;
                        }
                        case HighlightColor.CYAN -> {
                            highlightColor = ColorPalette.CYAN;
                            foregroundColor = Color.BLACK;
                        }
                        case HighlightColor.GREEN -> {
                            highlightColor = ColorPalette.GREEN;
                            foregroundColor = Color.BLACK;
                        }
                        case HighlightColor.MAGENTA -> {
                            highlightColor = ColorPalette.MAGENTA;
                            foregroundColor = Color.BLACK;
                        }
                        case HighlightColor.ORANGE -> {
                            highlightColor = ColorPalette.ORANGE;
                            foregroundColor = Color.BLACK;
                        }
                        case HighlightColor.RED -> {
                            highlightColor = ColorPalette.RED;
                            foregroundColor = Color.WHITE;
                        }
                        case HighlightColor.PINK -> {
                            highlightColor = ColorPalette.PINK;
                            foregroundColor = Color.BLACK;
                        }
                        case HighlightColor.GRAY -> {
                            highlightColor = ColorPalette.GREY;
                            foregroundColor = Color.BLACK;
                        }
                        default -> {
                            highlightColor = component.getBackground();
                            foregroundColor = component.getForeground();
                        }
                    }
                    if (!isCellSelected(row, column)) {
                        component.setBackground(highlightColor);
                        component.setForeground(foregroundColor);
                    }
                } else {
                    component.setBackground(getBackground());
                    component.setForeground(getForeground());
                }
                if (isCellSelected(row, column)) {
                    component.setBackground(getSelectionBackground());
                    component.setForeground(getSelectionForeground());
                }

                return component;
            }
        };

        displayedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = displayedTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = displayedTable.convertRowIndexToModel(viewRow);
                    LogEntry entry = logEntryModel.get(modelRow);
                    requestViewer.setRequest(entry.getRequestResponse().request());
                    responseViewer.setResponse(entry.getRequestResponse().response());
                    notesArea.setText(entry.getNotes());
                }
            }
        });

        displayedTable.addMouseListener(new MouseAdapter() {
            @Override
            public synchronized void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int rowIndex = displayedTable.rowAtPoint(e.getPoint());
                    if (rowIndex >= 0 && rowIndex < displayedTable.getRowCount()) {
                        if (!displayedTable.getSelectionModel().isSelectedIndex(rowIndex)) {
                            displayedTable.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
                        }
                        showPopupMenu(e.getX(), e.getY());
                    }
                }
            }
        });


        displayedTable.setAutoCreateRowSorter(true);
        displayedTable.getRowSorter().addRowSorterListener(e -> displayedTable.repaint());

        JScrollPane tableScrollPane = new JScrollPane(displayedTable);
        splitPane.setTopComponent(tableScrollPane);


        JTabbedPane requestResponseTabs = new JTabbedPane();
        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, requestViewer.uiComponent(), responseViewer.uiComponent());
        horizontalSplit.setResizeWeight(0.5);
        requestResponseTabs.addTab("Request/Response", horizontalSplit);
        requestResponseTabs.addTab("Notes", IconFontSwing.buildIcon(GoogleMaterialDesignIcons.NOTE, 20, Color.GRAY), new JScrollPane(notesArea));

        splitPane.setBottomComponent(requestResponseTabs);

        add(splitPane, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton horizontalViewButton = new JRadioButton("Horizontal", true);
        JRadioButton tabbedViewButton = new JRadioButton("Tabbed");
        JRadioButton verticalViewButton = new JRadioButton("Vertical");


        ButtonGroup viewButtonGroup = new ButtonGroup();
        viewButtonGroup.add(horizontalViewButton);
        viewButtonGroup.add(tabbedViewButton);
        viewButtonGroup.add(verticalViewButton);


        buttonsPanel.add(horizontalViewButton);
        buttonsPanel.add(verticalViewButton);
        buttonsPanel.add(tabbedViewButton);

        bottomPanel.add(buttonsPanel, BorderLayout.WEST);


        tabbedViewButton.addActionListener(e -> setTabbedView(requestResponseTabs));
        verticalViewButton.addActionListener(e -> setVerticalView(requestResponseTabs));
        horizontalViewButton.addActionListener(e -> setHorizontalView(requestResponseTabs));


        add(bottomPanel, BorderLayout.SOUTH);

        System.gc();
    }


    public static void updateLogEntryTable(List<LogEntry> updatedEntries, Boolean replaceOriginal) {
        if (replaceOriginal) {
            logEntries = updatedEntries;
        }
        logEntryModel.logEntries = updatedEntries;
        logEntryModel.fireTableDataChanged();
        displayedTable.repaint();
    }

    public static void resetLogEntryTable() {
        logEntryModel.logEntries = logEntries;
        logEntryModel.fireTableDataChanged();
        displayedTable.repaint();
    }

    public static void addLogEntry(LogEntry logEntry) {
        int index = logEntries.size();
        logEntries.add(logEntry);
        if (logEntryModel.logEntries.size() - 1 == index) {
            logEntryModel.fireTableRowsInserted(index, index);
            displayedTable.repaint();
        }
    }


    private void clearLogEntries() {
        logEntries = null;
        System.gc();
        logEntries = new ArrayList<>();
        logEntryModel.logEntries = logEntries;
        logEntryModel.fireTableDataChanged();
        displayedTable.repaint();
    }


    private void updateLogEntryNotes() {
        int selectedRow = displayedTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = displayedTable.convertRowIndexToModel(selectedRow);
            LogEntry entry = logEntryModel.get(modelRow);
            entry.setNotes(notesArea.getText());
            logEntryModel.fireTableRowsUpdated(modelRow, modelRow);
        }
    }

    private void setTabbedView(JTabbedPane tabs) {
        tabs.removeAll();
        tabs.addTab("Request", requestViewer.uiComponent());
        tabs.addTab("Response", responseViewer.uiComponent());
        tabs.addTab("Notes", new JScrollPane(notesArea));
    }

    private void setVerticalView(JTabbedPane tabs) {
        tabs.removeAll();
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, requestViewer.uiComponent(), responseViewer.uiComponent());
        verticalSplit.setResizeWeight(0.5);
        tabs.addTab("Request/Response", verticalSplit);
        tabs.addTab("Notes", new JScrollPane(notesArea));
    }

    private void setHorizontalView(JTabbedPane tabs) {
        tabs.removeAll();
        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, requestViewer.uiComponent(), responseViewer.uiComponent());
        horizontalSplit.setResizeWeight(0.5);
        tabs.addTab("Request/Response", horizontalSplit);
        tabs.addTab("Notes", new JScrollPane(notesArea));
    }


    private void showPopupMenu(int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem menuItemIntruder = new JMenuItem("Send to Intruder");
        menuItemIntruder.addActionListener(e -> sendSelectedEntriesToIntruder());
        popupMenu.add(menuItemIntruder);

        JMenuItem menuItemRepeater = new JMenuItem("Send to Repeater");
        menuItemRepeater.addActionListener(e -> sendSelectedEntriesToRepeater());
        popupMenu.add(menuItemRepeater);


        JMenu exportMenu = new JMenu("Export");
        addExportSubMenuItems(exportMenu);
        popupMenu.add(exportMenu);

        JMenu highlightMenu = new JMenu("Highlight");
        addHighlightSubMenuItems(highlightMenu);
        popupMenu.add(highlightMenu);

        JMenuItem menuItemDelete = new JMenuItem("Delete");
        menuItemDelete.addActionListener(e -> deleteSelectedEntries());
        popupMenu.add(menuItemDelete);

        JMenuItem menuItemClear = new JMenuItem("Clear History");
        menuItemClear.addActionListener(e -> clearLogEntries());
        popupMenu.add(menuItemClear);

        popupMenu.show(displayedTable, x, y);
    }

    private void addHighlightSubMenuItems(JMenu highlightMenu) {
        for (HighlightColor color : HighlightColor.values()) {
            JMenuItem colorItem = new JMenuItem(color.name());
            colorItem.addActionListener(e -> highlightSelectedEntries(color));
            highlightMenu.add(colorItem);
        }
    }

    private void addExportSubMenuItems(JMenu exportMenu) {
        for (ExportMode exportMode : ExportMode.values()) {
            JMenuItem menuItemExportJson = new JMenuItem(exportMode.name());
            menuItemExportJson.addActionListener(e -> exportSelectedEntriesToFile(exportMode));
            exportMenu.add(menuItemExportJson);
        }
    }


    private void highlightSelectedEntries(HighlightColor color) {
        int[] selectedRows = displayedTable.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = displayedTable.convertRowIndexToModel(selectedRow);
            if (modelRow >= 0 && modelRow < logEntryModel.logEntries.size()) {
                LogEntry selectedEntry = logEntryModel.get(modelRow);
                selectedEntry.getRequestResponse().annotations().setHighlightColor(color);
                displayedTable.repaint();
            }
        }
    }

    private void deleteSelectedEntries() {
        int[] selectedRows = displayedTable.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = displayedTable.convertRowIndexToModel(selectedRow);
            if (modelRow >= 0 && modelRow < logEntryModel.logEntries.size()) {
                logEntries.remove(modelRow);
                logEntryModel.fireTableRowsDeleted(modelRow, modelRow);
                displayedTable.repaint();
            }
        }
    }

    private void sendSelectedEntriesToRepeater() {
        int[] selectedRows = displayedTable.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = displayedTable.convertRowIndexToModel(selectedRow);
            if (modelRow >= 0 && modelRow < logEntryModel.logEntries.size()) {
                LogEntry selectedEntry = logEntryModel.get(modelRow);
                montoyaApi.repeater().sendToRepeater(selectedEntry.getRequestResponse().request());
            }
        }
    }

    private void sendSelectedEntriesToIntruder() {
        int[] selectedRows = displayedTable.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = displayedTable.convertRowIndexToModel(selectedRow);
            if (modelRow >= 0 && modelRow < logEntryModel.logEntries.size()) {
                LogEntry selectedEntry = logEntryModel.get(modelRow);
                montoyaApi.intruder().sendToIntruder(selectedEntry.getRequestResponse().request());
            }
        }
    }

    private void exportSelectedEntriesToFile(ExportMode mode) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                switch (mode) {
                    case XML ->
                            DataTransferUtil.exportLogEntriesToXml(selectedFile, getSelectedLogEntries(), logEntry -> true);
                    case JSON ->
                            DataTransferUtil.exportLogEntriesToJson(selectedFile, getSelectedLogEntries(), logEntry -> true);
                    case SER ->
                            DataTransferUtil.exportLogEntriesSerialized(selectedFile, getSelectedLogEntries(), logEntry -> true);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load log entries from file: " + selectedFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<LogEntry> getSelectedLogEntries() {
        List<LogEntry> selectedEntries = new ArrayList<>();
        int[] selectedRows = displayedTable.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = displayedTable.convertRowIndexToModel(selectedRow);
            if (modelRow >= 0 && modelRow < logEntryModel.logEntries.size()) {
                selectedEntries.add(logEntryModel.get(modelRow));
            }
        }
        return selectedEntries;
    }

    enum SendTo {
        REPEATER,
        INTRUDER,
        ORGANIZER,
    }

    enum ExportMode {
        SER,
        XML,
        JSON
    }

    static class ColorPalette {
        public static final Color RED = new Color(254, 100, 101);
        public static final Color ORANGE = new Color(255, 201, 101);
        public static final Color YELLOW = new Color(254, 255, 101);
        public static final Color GREEN = new Color(101, 255, 101);
        public static final Color CYAN = new Color(100, 254, 254);
        public static final Color BLUE = new Color(101, 100, 254);
        public static final Color MAGENTA = new Color(254, 101, 255);
        public static final Color PINK = new Color(255, 201, 201);
        public static final Color GREY = new Color(181, 181, 180);
    }

}
