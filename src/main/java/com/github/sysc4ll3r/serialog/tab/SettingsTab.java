package com.github.sysc4ll3r.serialog.tab;

import burp.api.montoya.core.Registration;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import com.github.sysc4ll3r.serialog.extension.LoggerHttpHandler;
import com.github.sysc4ll3r.serialog.extension.SeriaLogExtension;
import com.github.sysc4ll3r.serialog.io.DataTransferUtil;
import com.github.sysc4ll3r.serialog.table.LogEntry;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.github.sysc4ll3r.serialog.extension.SeriaLogExtension.montoyaApi;
import static com.github.sysc4ll3r.serialog.extension.SeriaLogExtension.props;


public class SettingsTab extends JPanel {

    private Set<ToolType> selectedTools = new HashSet<>();
    private Set<ToolType> loggingTools = new HashSet<>();
    private Registration httpHandlerRegistration;

    private JRadioButton serRadioButton;
    private JRadioButton xmlRadioButton;
    private JRadioButton jsonRadioButton;
    private JCheckBox inscopeOnlyCheckBox;
    private JCheckBox sitemapCheckBox;
    private JCheckBox proxyCheckBox;
    private JCheckBox intruderCheckBox;
    private JCheckBox repeaterCheckBox;
    private JCheckBox extensionCheckBox;
    private JCheckBox sequencerCheckBox;
    private JCheckBox scannerCheckBox;
    private JSpinner maxResponseSizeSpinner;
    private JRadioButton appendRadioButton;
    private JRadioButton replaceRadioButton;
    private JButton importFileButton;
    private JButton exportFileButton;
    private JButton importProxyHistoryButton;
    private JRadioButton fromSerRadioButton;
    private JRadioButton fromXmlRadioButton;
    private JRadioButton fromJsonRadioButton;
    private JRadioButton toSerRadioButton;
    private JRadioButton toXmlRadioButton;
    private JRadioButton toJsonRadioButton;
    private JButton selectFileButton;
    private JCheckBox logInScopeOnlyCheckBox;
    private JCheckBox logProxyCheckBox;
    private JCheckBox logRepeaterCheckBox;
    private JCheckBox logIntruderCheckBox;
    private JCheckBox logSequencerCheckBox;
    private JCheckBox logExtensionCheckBox;
    private JCheckBox logScannerCheckBox;

    public SettingsTab() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(createFileModePanel(), gbc);


        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(createFilterPanel(), gbc);


        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(createMaxResponseSizeAndImportModePanel(), gbc);


        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(createImportExportPanel(), gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(createLogPanel(), gbc);



        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(createConvertPanel(), gbc);

        updateHttpHandler();
    }

    private JPanel createFileModePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("File Mode"));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        serRadioButton = new JRadioButton("SER", props.getBoolean("fileModeSer"));
        xmlRadioButton = new JRadioButton("XML", props.getBoolean("fileModeXml"));
        jsonRadioButton = new JRadioButton("JSON", props.getBoolean("fileModeJson"));

        ButtonGroup fileModeGroup = new ButtonGroup();
        fileModeGroup.add(serRadioButton);
        fileModeGroup.add(xmlRadioButton);
        fileModeGroup.add(jsonRadioButton);

        panel.add(serRadioButton);
        panel.add(xmlRadioButton);
        panel.add(jsonRadioButton);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Filter"));
        panel.setLayout(new GridLayout(8, 1));
        inscopeOnlyCheckBox = new JCheckBox("InScope Only", props.getBoolean("fileFilterInScope"));
        sitemapCheckBox = new JCheckBox("Sitemap", props.getBoolean("fileFilterSitemap"));
        proxyCheckBox = new JCheckBox("Proxy", props.getBoolean("fileFilterProxy"));
        intruderCheckBox = new JCheckBox("Intruder", props.getBoolean("fileFilterIntruder"));
        repeaterCheckBox = new JCheckBox("Repeater", props.getBoolean("fileFilterRepeater"));
        extensionCheckBox = new JCheckBox("Extensions", props.getBoolean("fileFilterExtensions"));
        sequencerCheckBox = new JCheckBox("Sequencer", props.getBoolean("fileFilterSequencer"));
        scannerCheckBox = new JCheckBox("Scanner", props.getBoolean("fileFilterScanner"));

        panel.add(inscopeOnlyCheckBox);
        panel.add(sitemapCheckBox);
        panel.add(proxyCheckBox);
        panel.add(intruderCheckBox);
        panel.add(repeaterCheckBox);
        panel.add(extensionCheckBox);
        panel.add(sequencerCheckBox);
        panel.add(scannerCheckBox);

        return panel;
    }

    private JPanel createMaxResponseSizeAndImportModePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 5, 5));


        JPanel maxResponseSizePanel = new JPanel();
        maxResponseSizePanel.setBorder(BorderFactory.createTitledBorder("Max Response Size (MB)"));
        maxResponseSizePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        maxResponseSizeSpinner = new JSpinner(new SpinnerNumberModel((int) props.getInteger("fileFilterMaxResponse"), 0, Integer.MAX_VALUE, 1));
        maxResponseSizeSpinner.setPreferredSize(new Dimension(100, 30));
        maxResponseSizePanel.add(maxResponseSizeSpinner);


        JPanel importModePanel = new JPanel();
        importModePanel.setBorder(BorderFactory.createTitledBorder("Import Mode"));
        importModePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        appendRadioButton = new JRadioButton("Append", props.getBoolean("fileImportAppend"));
        replaceRadioButton = new JRadioButton("Replace", props.getBoolean("fileImportReplace"));

        ButtonGroup importModeGroup = new ButtonGroup();
        importModeGroup.add(appendRadioButton);
        importModeGroup.add(replaceRadioButton);

        importModePanel.add(appendRadioButton);
        importModePanel.add(replaceRadioButton);

        panel.add(maxResponseSizePanel);
        panel.add(importModePanel);

        return panel;
    }

    private JPanel createImportExportPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Import/Export"));
        panel.setLayout(new GridLayout(1, 3));

        importFileButton = new JButton("Import File", UIManager.getIcon("FileView.directoryIcon"));
        importFileButton.setPreferredSize(new Dimension(60, 40));
        importFileButton.addActionListener(e -> importFromFile());

        exportFileButton = new JButton("Export File", UIManager.getIcon("FileView.floppyDriveIcon"));
        exportFileButton.setPreferredSize(new Dimension(60, 40));
        exportFileButton.addActionListener(e -> exportToFile());

        importProxyHistoryButton = new JButton("Import Proxy History");
        importProxyHistoryButton.setPreferredSize(new Dimension(150, 40));
        importProxyHistoryButton.addActionListener(e -> importFromHistory());

        panel.add(importFileButton);
        panel.add(exportFileButton);
        panel.add(importProxyHistoryButton);
        panel.setPreferredSize(new Dimension(700, 50));

        return panel;
    }

    private JPanel createConvertPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Convert"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("From:"), gbc);

        fromSerRadioButton = new JRadioButton("SER", props.getBoolean("convertFromSer"));
        fromXmlRadioButton = new JRadioButton("XML", props.getBoolean("convertFromXml"));
        fromJsonRadioButton = new JRadioButton("JSON", props.getBoolean("convertToJson"));

        ButtonGroup fromGroup = new ButtonGroup();
        fromGroup.add(fromSerRadioButton);
        fromGroup.add(fromXmlRadioButton);
        fromGroup.add(fromJsonRadioButton);

        gbc.gridy = 1;
        panel.add(fromSerRadioButton, gbc);
        gbc.gridy = 2;
        panel.add(fromXmlRadioButton, gbc);
        gbc.gridy = 3;
        panel.add(fromJsonRadioButton, gbc);


        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(new JLabel("To:"), gbc);

        toSerRadioButton = new JRadioButton("SER", props.getBoolean("convertToSer"));
        toXmlRadioButton = new JRadioButton("XML", props.getBoolean("convertToXml"));
        toJsonRadioButton = new JRadioButton("JSON", props.getBoolean("convertToJson"));

        ButtonGroup toGroup = new ButtonGroup();
        toGroup.add(toSerRadioButton);
        toGroup.add(toXmlRadioButton);
        toGroup.add(toJsonRadioButton);

        gbc.gridy = 1;
        panel.add(toSerRadioButton, gbc);
        gbc.gridy = 2;
        panel.add(toXmlRadioButton, gbc);
        gbc.gridy = 3;
        panel.add(toJsonRadioButton, gbc);


        selectFileButton = new JButton("Select File");
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        panel.add(selectFileButton, gbc);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Log Options"));
        panel.setLayout(new GridLayout(8, 1));

        logInScopeOnlyCheckBox = new JCheckBox("InScope Only", props.getBoolean("loggingFilterInScope"));
        logProxyCheckBox = new JCheckBox("Proxy", props.getBoolean("loggingFilterProxy"));
        logRepeaterCheckBox = new JCheckBox("Repeater", props.getBoolean("loggingFilterRepeater"));
        logIntruderCheckBox = new JCheckBox("Intruder", props.getBoolean("loggingFilterIntruder"));
        logSequencerCheckBox = new JCheckBox("Sequencer", props.getBoolean("loggingFilterSequencer"));
        logExtensionCheckBox = new JCheckBox("Extensions", props.getBoolean("loggingFilterExtensions"));
        logScannerCheckBox = new JCheckBox("Scanner", props.getBoolean("loggingFilterScanner"));

        logInScopeOnlyCheckBox.addActionListener(e -> updateHttpHandler());
        logProxyCheckBox.addActionListener(e -> updateHttpHandler());
        logRepeaterCheckBox.addActionListener(e -> updateHttpHandler());
        logIntruderCheckBox.addActionListener(e -> updateHttpHandler());
        logSequencerCheckBox.addActionListener(e -> updateHttpHandler());
        logExtensionCheckBox.addActionListener(e -> updateHttpHandler());
        logScannerCheckBox.addActionListener(e -> updateHttpHandler());

        panel.add(logInScopeOnlyCheckBox);
        panel.add(logProxyCheckBox);
        panel.add(logRepeaterCheckBox);
        panel.add(logIntruderCheckBox);
        panel.add(logSequencerCheckBox);
        panel.add(logExtensionCheckBox);
        panel.add(logScannerCheckBox);

        return panel;
    }


    private void updateSelectedTools() {
        selectedTools = new HashSet<>();
        if (proxyCheckBox.isSelected()) selectedTools.add(ToolType.PROXY);
        if (repeaterCheckBox.isSelected()) selectedTools.add(ToolType.REPEATER);
        if (intruderCheckBox.isSelected()) selectedTools.add(ToolType.INTRUDER);
        if (sequencerCheckBox.isSelected()) selectedTools.add(ToolType.SEQUENCER);
        if (extensionCheckBox.isSelected()) selectedTools.add(ToolType.EXTENSIONS);
        if (scannerCheckBox.isSelected()) selectedTools.add(ToolType.SCANNER);
    }

    private void exportToFile() {
        try {
            updateSelectedTools();
            updatePreferences();
            File selectedFile = getSaveFile();
            if (selectedFile == null) return;

            List<LogEntry> logEntries = new ArrayList<>(LogTab.logEntries);
            if (sitemapCheckBox.isSelected()) {
                List<LogEntry> siteMapEntries = montoyaApi.siteMap()
                        .requestResponses()
                        .stream()
                        .map(requestResponse -> new LogEntry(requestResponse, ToolType.TARGET))
                        .toList();
                logEntries.addAll(siteMapEntries);
            }

            Predicate<LogEntry> logEntryPredicate = logEntry -> {
                if (!selectedTools.contains(logEntry.getTool())) {
                    if (sitemapCheckBox.isSelected() && logEntry.getTool() != ToolType.TARGET) {
                        return false;
                    }
                }
                if (inscopeOnlyCheckBox.isSelected() && !logEntry.getRequestResponse().request().isInScope()) {
                    return false;
                }
                return !logEntry.getRequestResponse().hasResponse() || logEntry.getRequestResponse().response().toByteArray().length() <= (int) maxResponseSizeSpinner.getValue() * 1024 * 1024;
            };

            String fileType = getSelectedFileType();
            if (fileType == null) return;

            exportLogEntries(selectedFile, logEntries, fileType, logEntryPredicate);

        } catch (Exception e) {
            showErrorDialog("Error While Exporting File");
            montoyaApi.logging().logToError(e);
        }
    }

    private void exportLogEntries(File file, List<LogEntry> logEntries, String fileType, Predicate<LogEntry> predicate) throws Exception {
        switch (fileType) {
            case "json":
                DataTransferUtil.exportLogEntriesToJson(file, logEntries, predicate);
                break;
            case "ser":
                DataTransferUtil.exportLogEntriesSerialized(file, logEntries, predicate);
                break;
            case "xml":
                DataTransferUtil.exportLogEntriesToXml(file, logEntries, predicate);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    private void importFromFile() {
        try {
            Predicate<LogEntry> logEntryPredicate = logEntry -> {
                if (inscopeOnlyCheckBox.isSelected() && !logEntry.getRequestResponse().request().isInScope()) {
                    return false;
                }
                if (!selectedTools.contains(logEntry.getTool())) {
                    if (logEntry.getTool() == ToolType.TARGET && sitemapCheckBox.isSelected()) {
                        montoyaApi.siteMap().add(logEntry.getRequestResponse());
                    }
                    return false;
                }

                return !logEntry.getRequestResponse().hasResponse() || logEntry.getRequestResponse().response().toByteArray().length() <= (int) maxResponseSizeSpinner.getValue() * 1024 * 1024;
            };

            updateSelectedTools();
            updatePreferences();

            String selectedFileType = getSelectedFileType();
            if (selectedFileType == null) return;

            List<File> selectedFiles = getSelectedFiles(getFileFilter(selectedFileType));
            if (selectedFiles == null) return;

            List<LogEntry> logEntries = new ArrayList<>();
            for (File file : selectedFiles) {
                logEntries.addAll(importLogEntries(file, selectedFileType, logEntryPredicate));
            }

            if (appendRadioButton.isSelected()) {
                LogTab.logEntries.addAll(logEntries);
            } else {
                LogTab.logEntries = logEntries;
            }
            LogTab.resetLogEntryTable();

        } catch (Exception e) {
            showErrorDialog("Error while importing file");
            montoyaApi.logging().logToError(e);
        }
    }

    private String getSelectedFileType() {
        if (serRadioButton.isSelected()) return "ser";
        if (jsonRadioButton.isSelected()) return "json";
        if (xmlRadioButton.isSelected()) return "xml";
        return null;
    }

    private List<LogEntry> importLogEntries(File file, String fileType, Predicate<LogEntry> predicate) throws Exception {
        return switch (fileType) {
            case "ser" -> DataTransferUtil.importLogEntriesSerialized(file, predicate);
            case "json" -> DataTransferUtil.importLogEntriesFromJson(file, predicate);
            case "xml" -> DataTransferUtil.importLogEntriesFromXml(file, predicate);
            default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
        };
    }


    private void importFromHistory() {
        updateSelectedTools();
        updatePreferences();
        List<LogEntry> logEntries = new ArrayList<>();
        for (ProxyHttpRequestResponse requestResponse : montoyaApi.proxy().history()) {
            if (inscopeOnlyCheckBox.isSelected() && !requestResponse.finalRequest().isInScope())
                continue;
            else if (requestResponse.hasResponse() && requestResponse.originalResponse().toByteArray().length() > (int) maxResponseSizeSpinner.getValue() * 1024 * 1024) {
                continue;
            }
            logEntries.add(new LogEntry(
                    HttpRequestResponse.httpRequestResponse(requestResponse.finalRequest(), requestResponse.originalResponse(), requestResponse.annotations()),
                    ToolType.PROXY
            ));
        }
        if (appendRadioButton.isSelected()) {
            LogTab.logEntries.addAll(logEntries);
            LogTab.resetLogEntryTable();
        } else {
            LogTab.logEntries = logEntries;
            LogTab.resetLogEntryTable();
        }
    }

    private List<File> getSelectedFiles(FileFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(props.getString("lastUsedPath")));
        fileChooser.setMultiSelectionEnabled(true);
        if (filter != null) {
            fileChooser.setFileFilter(filter);
        }
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            props.setString("lastUsedPath", fileChooser.getSelectedFiles()[0].getAbsolutePath());
            return List.of(fileChooser.getSelectedFiles());
        }
        return null;
    }

    private File getSaveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(props.getString("lastUsedPath")));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            props.setString("lastUsedPath", fileChooser.getSelectedFile().getAbsolutePath());
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    private FileFilter getFileFilter(String ext) {
        return new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith("." + ext);
            }

            @Override
            public String getDescription() {
                return MessageFormat.format("{0} files (*.{1})", ext.toUpperCase(), ext);
            }
        };
    }

    private void updateHttpHandler() {
        if (this.httpHandlerRegistration != null) {
            this.httpHandlerRegistration.deregister();
        }

        loggingTools = new HashSet<>();
        if (logProxyCheckBox.isSelected()) loggingTools.add(ToolType.PROXY);
        if (logRepeaterCheckBox.isSelected()) loggingTools.add(ToolType.REPEATER);
        if (logIntruderCheckBox.isSelected()) loggingTools.add(ToolType.INTRUDER);
        if (logSequencerCheckBox.isSelected()) loggingTools.add(ToolType.SEQUENCER);
        if (logExtensionCheckBox.isSelected()) loggingTools.add(ToolType.EXTENSIONS);
        if (logScannerCheckBox.isSelected()) loggingTools.add(ToolType.SCANNER);

        if (!loggingTools.isEmpty()) {
            this.httpHandlerRegistration = SeriaLogExtension.montoyaApi.http()
                    .registerHttpHandler(new LoggerHttpHandler(loggingTools, logInScopeOnlyCheckBox.isSelected()));
        }
        updatePreferences();
    }

    private void updatePreferences() {
        props.setBoolean("fileModeSer", serRadioButton.isSelected());
        props.setBoolean("fileModeXml", xmlRadioButton.isSelected());
        props.setBoolean("fileModeJson", jsonRadioButton.isSelected());
        props.setBoolean("fileFilterInScope", inscopeOnlyCheckBox.isSelected());
        props.setBoolean("fileFilterSitemap", sitemapCheckBox.isSelected());
        props.setBoolean("fileFilterProxy", proxyCheckBox.isSelected());
        props.setBoolean("fileFilterRepeater", repeaterCheckBox.isSelected());
        props.setBoolean("fileFilterIntruder", intruderCheckBox.isSelected());
        props.setBoolean("fileFilterSequencer", sequencerCheckBox.isSelected());
        props.setBoolean("fileFilterExtensions", extensionCheckBox.isSelected());
        props.setBoolean("fileFilterScanner", scannerCheckBox.isSelected());
        props.setInteger("fileFilterMaxResponse", (int) maxResponseSizeSpinner.getValue());
        props.setBoolean("fileImportAppend", appendRadioButton.isSelected());
        props.setBoolean("fileImportReplace", replaceRadioButton.isSelected());
        props.setBoolean("convertFromSer", fromSerRadioButton.isSelected());
        props.setBoolean("convertFromXml", fromXmlRadioButton.isSelected());
        props.setBoolean("convertFromJson", fromJsonRadioButton.isSelected());
        props.setBoolean("convertToSer", toSerRadioButton.isSelected());
        props.setBoolean("convertToXml", toXmlRadioButton.isSelected());
        props.setBoolean("convertToJson", toJsonRadioButton.isSelected());
        props.setBoolean("loggingFilterInScope", logInScopeOnlyCheckBox.isSelected());
        props.setBoolean("loggingFilterProxy", logProxyCheckBox.isSelected());
        props.setBoolean("loggingFilterRepeater", logRepeaterCheckBox.isSelected());
        props.setBoolean("loggingFilterIntruder", logIntruderCheckBox.isSelected());
        props.setBoolean("loggingFilterSequencer", logSequencerCheckBox.isSelected());
        props.setBoolean("loggingFilterExtensions", logExtensionCheckBox.isSelected());
        props.setBoolean("loggingFilterScanner", logScannerCheckBox.isSelected());
    }

}

