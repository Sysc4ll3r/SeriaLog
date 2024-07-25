package com.github.sysc4ll3r.serialog.panel;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.StatusCodeClass;
import com.github.sysc4ll3r.serialog.tab.LogTab;
import com.github.sysc4ll3r.serialog.table.LogEntry;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.sysc4ll3r.serialog.extension.SeriaLogExtension.props;

public class FilterPanel extends JPanel {
    private JTextField searchField;
    private JCheckBox regexCheckBox;
    private JCheckBox negativeSearchCheckBox;
    private JCheckBox caseSensitiveCheckBox;
    private JTextField showOnlyField;
    private JTextField hideField;
    private JCheckBox showOnlyWithNotesCheckBox;
    private JCheckBox showOnlyInScopeItemsCheckBox;
    private JCheckBox hideItemsWithoutResponsesCheckBox;
    private JCheckBox showOnlyParameterizedRequestsCheckBox;
    private JCheckBox[] statusCodeCheckBoxes;
    private JCheckBox[] mimeTypeCheckBoxes;
    private JCheckBox showOnlyExtensionCheckBox;
    private JCheckBox hideExtensionCheckBox;
    private Pattern compiledPattern;
    private JCheckBox showOnlyAnnotatedItemsCheckBox;
    private JCheckBox[] toolsCheckBoxes;
    private HashSet<ToolType> selectedTools;
    private HashSet<MimeType> mimeExclude;

    public FilterPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(createRequestTypePanel(), gbc);

        gbc.gridy++;
        topPanel.add(createStatusCodePanel(), gbc);

        gbc.gridy++;
        topPanel.add(createSearchTermPanel(), gbc);

        gbc.gridy++;
        JPanel mimeAndToolsPanel = new JPanel(new BorderLayout());
        mimeAndToolsPanel.add(createMimeTypePanel(), BorderLayout.CENTER);
        mimeAndToolsPanel.add(createToolsPanel(), BorderLayout.EAST);
        topPanel.add(mimeAndToolsPanel, gbc);

        gbc.gridy++;
        topPanel.add(createFileExtensionPanel(), gbc);

        gbc.gridy++;
        topPanel.add(createAnnotationPanel(), gbc);

        add(topPanel, BorderLayout.CENTER);
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private JPanel createRequestTypePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 2, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Filter By Request Type"));

        showOnlyInScopeItemsCheckBox = new JCheckBox("Show Only In Scope Items", props.getBoolean("filterInScope"));
        hideItemsWithoutResponsesCheckBox = new JCheckBox("Hide Items Without Responses", props.getBoolean("filterWithoutResponses"));
        showOnlyParameterizedRequestsCheckBox = new JCheckBox("Show Only Parameterized Requests", props.getBoolean("filterParamItems"));

        panel.add(showOnlyInScopeItemsCheckBox);
        panel.add(hideItemsWithoutResponsesCheckBox);
        panel.add(showOnlyParameterizedRequestsCheckBox);

        return panel;
    }

    private JPanel createStatusCodePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 2, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Filter By Status Code"));

        statusCodeCheckBoxes = new JCheckBox[]{
                new JCheckBox("1xx", props.getBoolean("filterStatus1xx")),
                new JCheckBox("2xx", props.getBoolean("filterStatus2xx")),
                new JCheckBox("3xx", props.getBoolean("filterStatus3xx")),
                new JCheckBox("4xx", props.getBoolean("filterStatus4xx")),
                new JCheckBox("5xx", props.getBoolean("filterStatus5xx"))
        };

        for (JCheckBox checkBox : statusCodeCheckBoxes) {
            panel.add(checkBox);
        }

        return panel;
    }

    private JPanel createMimeTypePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 1, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Filter By Mime Type"));

        mimeTypeCheckBoxes = new JCheckBox[]{
                new JCheckBox("Html", props.getBoolean("filterHtml")),
                new JCheckBox("Script", props.getBoolean("filterScript")),
                new JCheckBox("XML", props.getBoolean("filterXml")),
                new JCheckBox("JSON", props.getBoolean("filterJson")),
                new JCheckBox("CSS", props.getBoolean("filterCss")),
                new JCheckBox("Flash", props.getBoolean("filterFlash")),
                new JCheckBox("OtherText", props.getBoolean("filterOtherText")),
                new JCheckBox("OtherBinary", props.getBoolean("filterOtherBinary")),
                new JCheckBox("Images", props.getBoolean("filterImages"))
        };

        for (JCheckBox checkBox : mimeTypeCheckBoxes) {
            panel.add(checkBox);
        }

        return panel;
    }

    private JPanel createSearchTermPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Filter By Search Term"));

        GridBagConstraints gbc = createGridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        searchField = new JTextField(props.getString("filterSearchText"), 40);
        panel.add(searchField, gbc);

        gbc.gridx = 0;
        gbc.gridwidth = 1;
        regexCheckBox = new JCheckBox("Regex", props.getBoolean("filterSearchRegex"));
        panel.add(regexCheckBox, gbc);

        gbc.gridx = 1;
        caseSensitiveCheckBox = new JCheckBox("Case Sensitive", props.getBoolean("filterCaseSensitiveSearch"));
        panel.add(caseSensitiveCheckBox, gbc);

        gbc.gridx = 2;
        negativeSearchCheckBox = new JCheckBox("Negative Search", props.getBoolean("filterNegativeSearch"));
        panel.add(negativeSearchCheckBox, gbc);

        return panel;
    }

    private JPanel createFileExtensionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Filter By File Extension"));

        GridBagConstraints gbc = createGridBagConstraints();
        gbc.gridx = 0;

        showOnlyExtensionCheckBox = new JCheckBox();
        showOnlyExtensionCheckBox.setEnabled(props.getBoolean("filterShowOnlyExt"));
        panel.add(showOnlyExtensionCheckBox, gbc);

        gbc.gridx = 1;
        panel.add(new JLabel("Show Only: "), gbc);

        gbc.gridx = 2;
        showOnlyField = new JTextField(props.getString("filterShowOnlyExtBy"), 20);
        panel.add(showOnlyField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        hideExtensionCheckBox = new JCheckBox();
        hideExtensionCheckBox.setSelected(props.getBoolean("filterHideExt"));
        panel.add(hideExtensionCheckBox, gbc);

        gbc.gridx = 1;
        panel.add(new JLabel("Hide: "), gbc);

        gbc.gridx = 2;
        hideField = new JTextField(props.getString("filterHideExtBy"), 20);
        panel.add(hideField, gbc);

        showOnlyExtensionCheckBox.addActionListener(e ->
                hideExtensionCheckBox.setEnabled(!showOnlyExtensionCheckBox.isSelected())
        );
        hideExtensionCheckBox.addActionListener(e ->
                showOnlyExtensionCheckBox.setEnabled(!hideExtensionCheckBox.isSelected())
        );


        return panel;
    }

    private JPanel createAnnotationPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Filter By Annotation"));
        showOnlyWithNotesCheckBox = new JCheckBox("Show Only Items With Notes", props.getBoolean("filterNotesItems"));
        showOnlyAnnotatedItemsCheckBox = new JCheckBox("Show Only Annotated Items", props.getBoolean("filterAnnotatedItems"));
        panel.add(showOnlyWithNotesCheckBox);
        panel.add(showOnlyAnnotatedItemsCheckBox);

        return panel;
    }

    private JPanel createToolsPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 1, 1)); // Vertical arrangement
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filter By Tools",
                TitledBorder.LEFT, TitledBorder.TOP));

        toolsCheckBoxes = new JCheckBox[]{
                new JCheckBox("Proxy", props.getBoolean("filterProxy")),
                new JCheckBox("Repeater", props.getBoolean("filterRepeater")),
                new JCheckBox("Intruder", props.getBoolean("filterIntruder")),
                new JCheckBox("Sequencer", props.getBoolean("filterSequencer")),
                new JCheckBox("Extensions", props.getBoolean("filterExtensions")),
                new JCheckBox("Scanner", props.getBoolean("filterScanner"))
        };

        for (JCheckBox checkBox : toolsCheckBoxes) {
            checkBox.addActionListener(e -> updateSelectedTools());
            panel.add(checkBox);
        }

        panel.setPreferredSize(new Dimension(120, 150));

        return panel;
    }

    private void updateSelectedTools() {
        selectedTools = new HashSet<>();
        if (toolsCheckBoxes[0].isSelected()) selectedTools.add(ToolType.PROXY);
        if (toolsCheckBoxes[1].isSelected()) selectedTools.add(ToolType.REPEATER);
        if (toolsCheckBoxes[2].isSelected()) selectedTools.add(ToolType.INTRUDER);
        if (toolsCheckBoxes[3].isSelected()) selectedTools.add(ToolType.SEQUENCER);
        if (toolsCheckBoxes[4].isSelected()) selectedTools.add(ToolType.EXTENSIONS);
        if (toolsCheckBoxes[5].isSelected()) selectedTools.add(ToolType.SCANNER);
    }

    private void updateSelectedMimeTypes() {
        mimeExclude = new HashSet<>();
        if (!mimeTypeCheckBoxes[0].isSelected()) mimeExclude.add(MimeType.HTML);
        if (!mimeTypeCheckBoxes[1].isSelected()) mimeExclude.add(MimeType.SCRIPT);
        if (!mimeTypeCheckBoxes[2].isSelected()) {
            mimeExclude.add(MimeType.XML);
            mimeExclude.add(MimeType.IMAGE_SVG_XML);
        }
        if (!mimeTypeCheckBoxes[3].isSelected()) mimeExclude.add(MimeType.JSON);
        if (!mimeTypeCheckBoxes[4].isSelected()) mimeExclude.add(MimeType.CSS);
        if (!mimeTypeCheckBoxes[5].isSelected()) {
            mimeExclude.add(MimeType.APPLICATION_FLASH);
            mimeExclude.add(MimeType.LEGACY_SER_AMF);
        }
        if (!mimeTypeCheckBoxes[6].isSelected()) {
            mimeExclude.add(MimeType.PLAIN_TEXT);
            mimeExclude.add(MimeType.RTF);
        }
        if (!mimeTypeCheckBoxes[7].isSelected()) {
            mimeExclude.add(MimeType.UNRECOGNIZED);
            mimeExclude.add(MimeType.APPLICATION_UNKNOWN);
            mimeExclude.add(MimeType.SOUND);
            mimeExclude.add(MimeType.VIDEO);
            mimeExclude.add(MimeType.FONT_WOFF);
            mimeExclude.add(MimeType.FONT_WOFF2);
        }
        if (!mimeTypeCheckBoxes[8].isSelected()) {
            mimeExclude.add(MimeType.IMAGE_BMP);
            mimeExclude.add(MimeType.IMAGE_GIF);
            mimeExclude.add(MimeType.IMAGE_SVG_XML);
            mimeExclude.add(MimeType.IMAGE_JPEG);
            mimeExclude.add(MimeType.IMAGE_PNG);
            mimeExclude.add(MimeType.IMAGE_TIFF);
            mimeExclude.add(MimeType.IMAGE_UNKNOWN);
        }
    }


    public void apply(Boolean replaceOriginal) {
        updateSelectedTools();
        updateSelectedMimeTypes();
        updatePreferences();
        List<LogEntry> logEntries = LogTab.logEntries;
        Set<String> showOnlyExtensions = parseExtensions(showOnlyField.getText());
        Set<String> hideExtensions = parseExtensions(hideField.getText());


        if (regexCheckBox.isSelected() && !searchField.getText().isEmpty()) {
            compiledPattern = Pattern.compile(searchField.getText(), caseSensitiveCheckBox.isSelected() ? Pattern.DOTALL : Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        } else {
            compiledPattern = null;
        }

        List<LogEntry> filteredEntries = logEntries.stream()
                .filter(logEntry -> {
                    HttpRequestResponse requestResponse = logEntry.getRequestResponse();
                    return !(shouldExcludeByRequestType(requestResponse) ||
                            shouldExcludeByStatusCode(requestResponse) ||
                            shouldExcludeByMimeType(requestResponse) ||
                            shouldExcludeByExtension(requestResponse, showOnlyExtensions, hideExtensions) ||
                            shouldExcludeBySearch(requestResponse) ||
                            shouldExcludeByAnnotations(requestResponse) ||
                            !selectedTools.contains(logEntry.getTool())
                    );
                })
                .collect(Collectors.toList());

        LogTab.updateLogEntryTable(filteredEntries, replaceOriginal);
    }

    private boolean shouldExcludeByAnnotations(HttpRequestResponse requestResponse) {
        boolean isAnnotated = requestResponse.annotations().hasHighlightColor() || requestResponse.annotations().hasNotes();
        boolean hasNotes = requestResponse.annotations().hasNotes();
        if (showOnlyAnnotatedItemsCheckBox.isSelected())
            return !isAnnotated;
        if (showOnlyWithNotesCheckBox.isSelected())
            return !hasNotes;
        return false;
    }


    private boolean shouldExcludeByRequestType(HttpRequestResponse requestResponse) {
        return (showOnlyInScopeItemsCheckBox.isSelected() && !requestResponse.request().isInScope()) ||
                (hideItemsWithoutResponsesCheckBox.isSelected() && !requestResponse.hasResponse()) ||
                (showOnlyParameterizedRequestsCheckBox.isSelected() && !requestResponse.request().hasParameters());
    }

    private boolean shouldExcludeByStatusCode(HttpRequestResponse requestResponse) {
        if (!requestResponse.hasResponse()) return false;

        for (int i = 0; i < statusCodeCheckBoxes.length; i++) {
            StatusCodeClass statusCodeClass = StatusCodeClass.values()[i];
            if (!statusCodeCheckBoxes[i].isSelected() &&
                    requestResponse.response().isStatusCodeClass(statusCodeClass)) {
                return true;
            }
        }

        return false;
    }


    private boolean shouldExcludeByMimeType(HttpRequestResponse requestResponse) {
        if (!requestResponse.hasResponse()) return false;
        return mimeExclude.contains(requestResponse.response().inferredMimeType());
    }

    private boolean shouldExcludeByExtension(HttpRequestResponse requestResponse, Set<String> showOnlyExtensions, Set<String> hideExtensions) {
        String path = requestResponse.request().pathWithoutQuery().toLowerCase();
        if (showOnlyExtensionCheckBox.isSelected() && showOnlyExtensions.stream().noneMatch(ext -> path.endsWith(ext.trim()))) {
            return true;
        }
        return hideExtensionCheckBox.isSelected() && hideExtensions.stream().anyMatch(ext -> path.endsWith(ext.trim()));
    }

    private boolean shouldExcludeBySearch(HttpRequestResponse requestResponse) {
        String requestResponseText = requestResponse.request().toString() + requestResponse.response().toString();

        if (compiledPattern != null) {
            boolean contains = compiledPattern.matcher(requestResponseText).find();
            return negativeSearchCheckBox.isSelected() != contains;
        } else {
            String searchText = searchField.getText();
            if (searchText.isEmpty()) return false;

            boolean caseSensitive = caseSensitiveCheckBox.isSelected();
            boolean isNegative = negativeSearchCheckBox.isSelected();

            boolean contains = requestResponseText.contains(searchText);
            if (!caseSensitive) {
                contains = requestResponseText.toLowerCase().contains(searchText.toLowerCase());
            }

            return isNegative == contains;
        }
    }

    private Set<String> parseExtensions(String text) {
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(ext -> !ext.isEmpty())
                .collect(Collectors.toSet());
    }

    private void updatePreferences() {
        props.setBoolean("filterInScope", showOnlyInScopeItemsCheckBox.isSelected());
        props.setBoolean("filterWithoutResponses", hideItemsWithoutResponsesCheckBox.isSelected());
        props.setBoolean("filterParamItems", showOnlyParameterizedRequestsCheckBox.isSelected());
        props.setBoolean("filterStatus1xx", statusCodeCheckBoxes[0].isSelected());
        props.setBoolean("filterStatus2xx", statusCodeCheckBoxes[1].isSelected());
        props.setBoolean("filterStatus3xx", statusCodeCheckBoxes[2].isSelected());
        props.setBoolean("filterStatus4xx", statusCodeCheckBoxes[3].isSelected());
        props.setBoolean("filterStatus5xx", statusCodeCheckBoxes[4].isSelected());
        props.setString("filterSearchText", searchField.getText());
        props.setBoolean("filterSearchRegex", regexCheckBox.isSelected());
        props.setBoolean("filterCaseSensitiveSearch", caseSensitiveCheckBox.isSelected());
        props.setBoolean("filterNegativeSearch", negativeSearchCheckBox.isSelected());
        props.setBoolean("filterHtml", mimeTypeCheckBoxes[0].isSelected());
        props.setBoolean("filterScript", mimeTypeCheckBoxes[1].isSelected());
        props.setBoolean("filterXml", mimeTypeCheckBoxes[2].isSelected());
        props.setBoolean("filterJson", mimeTypeCheckBoxes[3].isSelected());
        props.setBoolean("filterCss", mimeTypeCheckBoxes[4].isSelected());
        props.setBoolean("filterFlash", mimeTypeCheckBoxes[5].isSelected());
        props.setBoolean("filterOtherText", mimeTypeCheckBoxes[6].isSelected());
        props.setBoolean("filterOtherBinary", mimeTypeCheckBoxes[4].isSelected());
        props.setBoolean("filterImages", mimeTypeCheckBoxes[5].isSelected());
        props.setBoolean("filterProxy", toolsCheckBoxes[0].isSelected());
        props.setBoolean("filterRepeater", toolsCheckBoxes[1].isSelected());
        props.setBoolean("filterIntruder", toolsCheckBoxes[2].isSelected());
        props.setBoolean("filterSequencer", toolsCheckBoxes[3].isSelected());
        props.setBoolean("filterExtensions", toolsCheckBoxes[4].isSelected());
        props.setBoolean("filterScanner", toolsCheckBoxes[5].isSelected());
        props.setBoolean("filterShowOnlyExt", toolsCheckBoxes[0].isSelected());
        props.setBoolean("filterHideExt", toolsCheckBoxes[0].isSelected());
        props.setString("filterShowOnlyExtBy", showOnlyField.getText());
        props.setString("filterHideExtBy", hideField.getText());
        props.setBoolean("filterNotesItems", showOnlyWithNotesCheckBox.isSelected());
        props.setBoolean("filterAnnotatedItems", showOnlyAnnotatedItemsCheckBox.isSelected());

    }
}