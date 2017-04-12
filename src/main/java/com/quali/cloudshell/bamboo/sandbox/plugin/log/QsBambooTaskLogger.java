package com.quali.cloudshell.bamboo.sandbox.plugin.log;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.quali.cloudshell.logger.QsLogger;


public class QsBambooTaskLogger extends QsLogger {

    private final BuildLogger buildLogger;

    public QsBambooTaskLogger(BuildLogger buildLogger) {
        this.buildLogger = buildLogger;
    }

    @Override
    public void Debug(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void Info(String message) {
        buildLogger.addBuildLogEntry(message);
    }

    @Override
    public void Error(String message) {
        buildLogger.addErrorLogEntry(message);
    }
}
