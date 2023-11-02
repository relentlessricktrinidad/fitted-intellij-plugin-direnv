package com.github.relentlesricktrinidad.fittedintellijplugincore.settings.ui;

import com.github.relentlesricktrinidad.fittedintellijplugincore.commands.DirenvCmd;
import com.github.relentlesricktrinidad.fittedintellijplugincore.settings.DirenvSettings;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.Key;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class RunConfigSettingsEditor<T extends RunConfigurationBase> extends SettingsEditor<T> {
    public static final Key<DirenvSettings> USER_DATA_KEY = new Key<>("Direnv Settings");

    private static final String FIELD_DIRENV_ENABLED = "DIRENV_ENABLED";
    private static final String FIELD_DIRENV_TRUSTED = "DIRENV_TRUSTED";

    private RunConfigSettingsPanel editor;

    public RunConfigSettingsEditor(RunConfigurationBase configuration) {
        editor = new RunConfigSettingsPanel(configuration);
    }

    public static void readExternal(@NotNull RunConfigurationBase configuration, @NotNull Element element) {
        boolean isDirenvEnabled = readBool(element, FIELD_DIRENV_ENABLED);
        boolean isDirenvTrusted = readBool(element, FIELD_DIRENV_TRUSTED);

        DirenvSettings state = new DirenvSettings(isDirenvEnabled, isDirenvTrusted);
        configuration.putCopyableUserData(USER_DATA_KEY, state);
    }

    public static void writeExternal(@NotNull RunConfigurationBase configuration, @NotNull Element element) {
        DirenvSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null) {
            writeBool(element, FIELD_DIRENV_ENABLED, state.isDirenvEnabled());
            writeBool(element, FIELD_DIRENV_TRUSTED, state.isDirenvTrusted());
        }
    }

    private static boolean readBool(Element element, String field) {
        String isDirenvEnabledStr = JDOMExternalizerUtil.readField(element, field);
        return Boolean.parseBoolean(isDirenvEnabledStr);
    }

    private static void writeBool(Element element, String field, boolean value) {
        JDOMExternalizerUtil.writeField(element, field, Boolean.toString(value));
    }

    public static Map<String, String> collectEnv(
            @NotNull RunConfigurationBase runConfigurationBase,
            String workingDirectory,
            Map<String, String> runConfigEnv
    ) {
        Map<String, String> envVars = new HashMap<>(runConfigEnv);

        DirenvSettings state = runConfigurationBase.getCopyableUserData(USER_DATA_KEY);
        envVars.putAll(collectEnv(state, workingDirectory));

        return envVars;
    }

    public static Map<String, String> collectEnv(DirenvSettings state, String workingDirectory) {
        Map<String, String> envVars = new HashMap<>();

        if (state != null && state.isDirenvEnabled()) {
            DirenvCmd cmd = new DirenvCmd(workingDirectory);
            envVars.putAll(cmd.importDirenv(state.isDirenvTrusted()));
        }

        return envVars;
    }

    public static DirenvSettings getState(RunConfigurationBase configuration) {
        DirenvSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        return state;
    }

    public static String getEditorTitle() {
        return "Direnv";
    }

    @Override
    protected void resetEditorFrom(@NotNull T configuration) {
        DirenvSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null) {
            editor.setState(state);
        }
    }

    @Override
    protected void applyEditorTo(@NotNull T configuration) throws ConfigurationException {
        configuration.putCopyableUserData(USER_DATA_KEY, editor.getState());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return editor;
    }
}
