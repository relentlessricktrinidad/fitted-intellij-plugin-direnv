package com.github.relentlesricktrinidad.fittedintellijplugincore.settings.ui;
import com.github.relentlesricktrinidad.fittedintellijplugincore.settings.DirenvSettings;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class RunConfigSettingsPanel extends JPanel {
    private final JCheckBox useDirenvCheckbox;
    private final JCheckBox trustDirenvCheckbox;

    public RunConfigSettingsPanel(RunConfigurationBase configuration) {
        useDirenvCheckbox = new JCheckBox("Enable Direnv");
        trustDirenvCheckbox = new JCheckBox("Trust .envrc");

        JPanel optionsPanel = new JPanel();
        BoxLayout bl2 = new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS);
        optionsPanel.setLayout(bl2);
        optionsPanel.setBorder(JBUI.Borders.emptyLeft(20));
        optionsPanel.add(new ComponentPanelBuilder(trustDirenvCheckbox).
                withComment("When enabled it will automatically allow direnv to process changes to the .envrc file in " +
                        "the working directory. Only enable this for projects you trust, direnv can execute " +
                        "potentially malicious code.").
                createPanel());


        JPanel boxLayoutWrapper = new JPanel();
        BoxLayout bl1 = new BoxLayout(boxLayoutWrapper, BoxLayout.PAGE_AXIS);
        boxLayoutWrapper.setLayout(bl1);
        boxLayoutWrapper.add(new ComponentPanelBuilder(useDirenvCheckbox).createPanel());
        boxLayoutWrapper.add(optionsPanel);

        setLayout(new BorderLayout());
        add(boxLayoutWrapper, BorderLayout.NORTH);
    }

    public DirenvSettings getState() {
        return new DirenvSettings(
                useDirenvCheckbox.isSelected(),
                trustDirenvCheckbox.isSelected()
        );
    }

    public void setState(DirenvSettings state) {
        useDirenvCheckbox.setSelected(state.isDirenvEnabled());
        trustDirenvCheckbox.setSelected(state.isDirenvTrusted());
    }
}
