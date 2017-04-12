package com.quali.cloudshell.bamboo.sandbox.plugin;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.quali.cloudshell.QsServerDetails;
import com.quali.cloudshell.bamboo.sandbox.plugin.servlet.AdminServletConst;
import com.quali.cloudshell.bamboo.sandbox.plugin.servlet.AdminServlet;
import org.jetbrains.annotations.NotNull;

public class QsServerRetriever {
    @NotNull
    public static QsServerDetails getQsServerDetails(PluginSettingsFactory pluginSettingsFactory) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        String serverUrl = (String) pluginSettings.get(AdminServlet.Config.class.getName() + '.' + AdminServletConst.URL);
        String csUser = (String) pluginSettings.get(AdminServlet.Config.class.getName() + '.' + AdminServletConst.CS_USER);
        String csPassword = (String) pluginSettings.get(AdminServlet.Config.class.getName() + '.' + AdminServletConst.CS_PASSWORD);
        String csDomain = (String) pluginSettings.get(AdminServlet.Config.class.getName() + '.' + AdminServletConst.CS_DOMAIN);

        return new QsServerDetails(serverUrl, csUser, csPassword, csDomain,true);
    }
}
