package com.quali.cloudshell.bamboo.sandbox.plugin;

import com.atlassian.bamboo.build.BuildLoggerManager;
import com.atlassian.bamboo.build.CustomBuildProcessor;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.plan.PlanResultKey;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.quali.cloudshell.QsServerDetails;
import com.quali.cloudshell.SandboxApiGateway;
import com.quali.cloudshell.bamboo.sandbox.plugin.log.QsBambooTaskLogger;
import com.quali.cloudshell.qsExceptions.SandboxApiException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SandboxCleanupProcessor implements CustomBuildProcessor
{
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger buildLogger = Logger.getLogger(SandboxCleanupProcessor.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private BuildContext buildContext;
    private final BuildLoggerManager buildLoggerManager;

    public SandboxCleanupProcessor(PluginSettingsFactory pluginSettingsFactory, BuildLoggerManager buildLoggerManager) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.buildLoggerManager = buildLoggerManager;
    }

    @Override
    public void init(@NotNull BuildContext buildContext)
    {
        this.buildContext = buildContext;
    }

    @NotNull
    @Override
    public BuildContext call() throws Exception
    {
        PlanResultKey planResultKey = buildContext.getPlanResultKey();
        BuildLogger buildLogger = buildLoggerManager.getLogger(planResultKey);

        final Map<String, String> customData = buildContext.getBuildResult()
                .getCustomBuildData();

        String sandbox = customData.get("SANDBOX_ID");
        if (sandbox != null && !sandbox.isEmpty()) {
            QsServerDetails qsServerDetails = QsServerRetriever.getQsServerDetails(pluginSettingsFactory);

            SandboxApiGateway gateway = new SandboxApiGateway(new QsBambooTaskLogger(buildLogger),
                    qsServerDetails);

            try {
                gateway.StopSandbox(sandbox, true);
            } catch (SandboxApiException e) {
                throw new TaskException("Failed to execute Sandbox cleanups task", e);
            }

            customData.remove("SANDBOX_ID");
        }
        return buildContext;
    }

}