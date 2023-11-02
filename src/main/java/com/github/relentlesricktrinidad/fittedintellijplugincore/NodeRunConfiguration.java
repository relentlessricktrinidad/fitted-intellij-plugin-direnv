package com.github.relentlesricktrinidad.fittedintellijplugincore;

import com.github.relentlesricktrinidad.fittedintellijplugincore.settings.ui.RunConfigSettingsEditor;
import com.intellij.execution.Executor;
import com.intellij.execution.Location;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.javascript.nodejs.execution.AbstractNodeTargetRunProfile;
import com.intellij.javascript.nodejs.execution.NodeTargetRun;
import com.intellij.javascript.nodejs.execution.runConfiguration.AbstractNodeRunConfigurationExtension;
import com.intellij.javascript.nodejs.execution.runConfiguration.NodeRunConfigurationLaunchSession;
import com.intellij.lang.javascript.buildTools.npm.rc.NpmRunConfiguration;
import com.intellij.lang.javascript.buildTools.npm.rc.NpmRunSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


public class NodeRunConfiguration extends AbstractNodeRunConfigurationExtension {
    private static final Logger LOG = Logger.getInstance(NodeRunConfiguration.class);

    @Override
    protected void readExternal(@NotNull AbstractNodeTargetRunProfile runConfiguration, @NotNull Element element) {
        RunConfigSettingsEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void writeExternal(@NotNull AbstractNodeTargetRunProfile runConfiguration, @NotNull Element element) {
        RunConfigSettingsEditor.writeExternal(runConfiguration, element);
    }

    @NotNull
    @Override
    public <P extends AbstractNodeTargetRunProfile> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new RunConfigSettingsEditor<>(configuration);
    }

    @Override
    public boolean isApplicableFor(@NotNull AbstractNodeTargetRunProfile configuration) {
        return true;
    }

    @Override
    protected void patchCommandLine(@NotNull AbstractNodeTargetRunProfile configuration, @Nullable RunnerSettings runnerSettings, @NotNull GeneralCommandLine cmdLine, @NotNull String runnerId, @NotNull Executor executor) {
        configuration.getSelectedOptions();
    }

    @Override
    protected void extendCreatedConfiguration(@NotNull AbstractNodeTargetRunProfile configuration, @NotNull Location location) {
        super.extendCreatedConfiguration(configuration, location);
    }

    @Nullable
    @Override
    public String getEditorTitle() {
        return RunConfigSettingsEditor.getEditorTitle();
    }

    @Nullable
    @Override
    public NodeRunConfigurationLaunchSession createLaunchSession(@NotNull AbstractNodeTargetRunProfile configuration, @NotNull ExecutionEnvironment environment) {
        return new NodeRunConfigurationLaunchSession() {
            @Override
            public void addNodeOptionsTo(@NotNull NodeTargetRun targetRun) { {
                EnvironmentVariablesData envData = targetRun.getEnvData();

                Map<String, String> newEnvs = RunConfigSettingsEditor
                        .collectEnv(configuration, targetRun.getProject().getBasePath(), envData.getEnvs());

                LOG.info(newEnvs.toString());

                targetRun.setEnvData( envData.with(newEnvs) );
            }}
        };
/*        NpmRunConfiguration config = (NpmRunConfiguration) configuration;
        EnvironmentVariablesData envData = config.getRunSettings().getEnvData();

        Map<String, String> newEnvs = RunConfigSettingsEditor
                .collectEnv(configuration, config.getRunSettings().getPackageJsonSystemDependentPath(), envData.getEnvs());

        LOG.info(newEnvs.toString());

        NpmRunSettings newSettings = config.getRunSettings().toBuilder().setEnvData(envData.with(newEnvs)).build();
        config.setRunSettings(newSettings);*/
        // return null;
    }

}
