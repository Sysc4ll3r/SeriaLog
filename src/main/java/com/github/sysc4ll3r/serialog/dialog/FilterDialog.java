package com.github.sysc4ll3r.serialog.dialog;

import com.github.sysc4ll3r.serialog.panel.FilterPanel;
import com.github.sysc4ll3r.serialog.panel.LambdaPanel;
import com.github.sysc4ll3r.serialog.tab.LogTab;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;

import javax.swing.*;
import java.awt.*;

public class FilterDialog extends JDialog {
    private JTabbedPane tabbedPane;
    private LambdaPanel lambdaPanel;
    private FilterPanel filterPanel;

    public FilterDialog(Frame parentFrame) {
        super(parentFrame, "Filter Dialog", true);
        setVisible(false);
        initializeComponents();
        setLayout();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parentFrame);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        filterPanel = new FilterPanel();
        lambdaPanel = new LambdaPanel();
    }

    private void setLayout() {
        tabbedPane.addTab("Setting Mode", jiconfont.swing.IconFontSwing.buildIcon(GoogleMaterialDesignIcons.SETTINGS, 20, Color.GRAY), filterPanel);
        tabbedPane.addTab("λ Lambda Mode", lambdaPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JCheckBox replaceButton = new JCheckBox("Replace Original Data (Not Just filter view)", false);
        buttonPanel.add(replaceButton);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            if (lambdaPanel.isVisible()) {
                lambdaPanel.apply(replaceButton.isSelected());
            } else {
                filterPanel.apply(replaceButton.isSelected());
            }
        });
        buttonPanel.add(applyButton);

        JButton applyAndCloseButton = new JButton("Apply & Close");
        applyAndCloseButton.addActionListener(e -> {
            dispose();
            if (lambdaPanel.isVisible()) {
                lambdaPanel.apply(replaceButton.isSelected());
            } else {
                filterPanel.apply(replaceButton.isSelected());
            }
        });
        buttonPanel.add(applyAndCloseButton);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> LogTab.resetLogEntryTable());
        buttonPanel.add(resetButton);


        JButton closeButton = new JButton("Close");

        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
}
