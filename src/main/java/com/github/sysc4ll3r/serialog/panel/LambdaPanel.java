package com.github.sysc4ll3r.serialog.panel;

import burp.api.montoya.http.message.HttpRequestResponse;
import com.github.sysc4ll3r.serialog.extension.SeriaLogExtension;
import com.github.sysc4ll3r.serialog.io.DataTransferUtil;
import com.github.sysc4ll3r.serialog.tab.LogTab;
import com.github.sysc4ll3r.serialog.table.LogEntry;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class LambdaPanel extends JPanel {
    private final RSyntaxTextArea lambdaTextArea;

    public LambdaPanel() {
        setLayout(new BorderLayout());
        JTextComponent.removeKeymap("RTextAreaKeymap");

        lambdaTextArea = new RSyntaxTextArea(50, 100);
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RTextAreaUI.inputMap", null);
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
            theme.apply(lambdaTextArea);
        } catch (Exception e) {
            SeriaLogExtension.montoyaApi.logging().logToError(e);
        }
        lambdaTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        lambdaTextArea.setCodeFoldingEnabled(true);
        lambdaTextArea.setEditable(true);
        lambdaTextArea.setFocusable(true);
        lambdaTextArea.setText("HttpRequest req=requestResponse.request();\n" +
                "HttpResponse res=requestResponse.response();\n" +
                "\n" +
                "return (                                                                                                          \n" +
                "\treq.isInScope() &&\n" +
                "\t(\n" +
                "\t\ttrue // filter here\n" +
                "     ) \n" +
                ");\n" +
                "\n" +
                "/* Quick Filters\n" +
                "(\treq.hasParameter(\"some-param\", HttpParameterType.URL)\t)\n" +
                "(\treq.body().toString().contains(\"SOMETHING\")\t)\n" +
                "(\treq.method().equals(\"POST\")\t)\n" +
                "(\treq.hasHeader(\"Authorization\")\t)\n" +
                "(\treq.contains(\"searchTerm\", true)\t)\n" +
                "(\treq.parameters().stream().anyMatch(param -> param.value().equals(\"param-value\"))\t)\n" +
                "(\tres!=null && res().statusCode() == 200\t)\n" +
                "(\tres!=null &&  res.hasHeader(\"Content-Type\") && res.header(\"Content-Type\").value().equals(\"application/json\")\t)\n" +
                "(\tres!=null && res.body().toString().contains(\"SOMETHING\")\t)\n" +
                "(\tres!=null && res.hasCookie(\"session\") && res.cookieValue(\"session\").equals(\"something\")\t)\n" +
                "(\tres!=null && res.contains(\"searchTerm\", true)\t)\n" +
                "*/\n" +
                "\n" +
                "/*  API\n" +
                "(\treq.path().contains(\"api\") || req.path().matches(\"/v\\\\d/\") || (req.hasHeader(\"Host\") &&  req.header(\"Host\").value().contains(\"api\"))\t)       \n" +
                "*/\n" +
                "\n" +
                "/*  JSON\n" +
                "(\treq.body().toString().trim().startsWith(\"{\") || req.body().toString().trim().startsWith(\"[\") || (res != null && (res.body().toString().trim().startsWith(\"{\") || res.body().toString().trim().startsWith(\"[\")))\t)   \n" +
                "*/");

        RTextScrollPane scrollPane = new RTextScrollPane(lambdaTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton importButton = new JButton("Import", UIManager.getIcon("FileView.directoryIcon"));
        importButton.addActionListener(e -> importLambdaExpression());
        buttonPanel.add(importButton);

        JButton exportButton = new JButton("Export", UIManager.getIcon("FileView.floppyDriveIcon"));
        exportButton.addActionListener(e -> exportLambdaExpression());
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void apply(Boolean replaceOriginal) {
        String lambdaExpression = lambdaTextArea.getText();

        List<LogEntry> filteredEntries;
        try {
            filteredEntries = filterByLambda(LogTab.logEntries, lambdaExpression);
        } catch (CompileException | ClassNotFoundException | IOException | NoSuchMethodException e) {
            JOptionPane.showMessageDialog(this, "Failed to Compile Expression : " + e, "Error", JOptionPane.ERROR_MESSAGE);
            filteredEntries = LogTab.logEntries;
        }

        LogTab.updateLogEntryTable(filteredEntries, replaceOriginal);
        System.gc();
    }

    private List<LogEntry> filterByLambda(List<LogEntry> items, String expression)
            throws CompileException, IOException, ClassNotFoundException, NoSuchMethodException {
        List<LogEntry> result = new ArrayList<>();


        String className = "ExpressionEvaluator";
        String methodName = "evaluate";
        String classCode = String.format(
                "import burp.api.montoya.http.*;" +
                        "import burp.api.montoya.http.message.*;" +
                        "import burp.api.montoya.http.message.*;" +
                        "import burp.api.montoya.http.message.requests.*;" +
                        "import burp.api.montoya.http.message.responses.*;" +
                        "import burp.api.montoya.http.message.params.*;" +
                        "import burp.api.montoya.core.Annotations;" +
                        "import burp.api.montoya.utilities.*;" +
                        "import burp.api.montoya.core.*;" +
                        "public class %s { " +
                        "    public static boolean %s(HttpRequestResponse requestResponse) { " +
                        "        %s " +
                        "\n    } " +
                        "}", className, methodName, expression);

        SimpleCompiler compiler = new SimpleCompiler();
        compiler.cook(new StringReader(classCode));
        Class<?> evaluatorClass = compiler.getClassLoader().loadClass(className);

        for (LogEntry item : items) {
            HttpRequestResponse requestResponse = item.getRequestResponse();

            Object resultObj;
            try {
                resultObj = evaluatorClass.getMethod(methodName, HttpRequestResponse.class).invoke(null, requestResponse);
            } catch (IllegalAccessException | InvocationTargetException e) {
                continue;
            }

            if (resultObj instanceof Boolean && (Boolean) resultObj) {
                result.add(item);
            }
        }

        return result;
    }

    private void importLambdaExpression() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) return;
            try {
                this.lambdaTextArea.setText(DataTransferUtil.importString(selectedFile));
            } catch (Exception e) {
                SeriaLogExtension.montoyaApi.logging().logToError(e);
                JOptionPane.showMessageDialog(this, "Failed to import expression: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportLambdaExpression() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) return;
            try {
                DataTransferUtil.exportString(selectedFile, lambdaTextArea.getText());
            } catch (Exception e) {
                SeriaLogExtension.montoyaApi.logging().logToError(e);
                JOptionPane.showMessageDialog(this, "Failed to export expression: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
