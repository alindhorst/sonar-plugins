package de.alexanderlindhorst.sonarcheckstyleprocessor;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

/**
 * @author lindhrst (original author)
 */
public class PerFileAuditRunner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerFileAuditRunner.class);
    private final List<File> targetFiles;
    private final Checker checker;
    private final ResultProvider resultProvider;

    public PerFileAuditRunner(Configuration configuration, File targetFile) throws CheckstyleException {
        Configuration localConfig = configuration;
        if (localConfig == null) {
            LOGGER.warn("No CheckStyle configuration given, will default to Sun Checks only");
            localConfig = loadSunChecksConfiguration();
        }
        this.targetFiles = Collections.singletonList(targetFile);
        this.resultProvider = new ResultProvider();
        this.checker = new Checker();
        checker.setModuleClassLoader(Checker.class.getClassLoader());
        checker.configure(localConfig);
        checker.addListener(resultProvider);
    }

    @Override
    public void run() {
        checker.process(targetFiles);
    }

    public List<LocalizedMessage> getErrorMessages() {
        return resultProvider.getErrorMessages();
    }

    public List<Throwable> getAuditExceptions() {
        return resultProvider.getAuditExceptions();
    }

    public boolean hasAuditProblems() {
        return resultProvider.hasAuditExceptions();
    }

    private static Configuration loadSunChecksConfiguration() throws CheckstyleException {
        URL resource = PerFileAuditRunner.class.getResource(
                "/de/alexanderlindhorst/sonarcheckstyleprocessor/config/sun_checks.xml");
        return ConfigurationLoader.loadConfiguration(resource.toExternalForm(), null);
    }
}
