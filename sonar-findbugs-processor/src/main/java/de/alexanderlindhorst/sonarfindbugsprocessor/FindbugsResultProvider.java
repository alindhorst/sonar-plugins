package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.UserPreferences;

import static java.lang.Boolean.TRUE;

public class FindbugsResultProvider implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindbugsResultProvider.class);
    private final FindBugs2 findbugs;
    private final String clazzName;
    private final PerFileFindBugsAuditRunner auditRunner;

    public FindbugsResultProvider(List<String> sourceDirs, List<String> auxClassPathDirs, File configuration, File targetFile,
            String clazzName) {
        this.clazzName = clazzName;
        this.auditRunner = new PerFileFindBugsAuditRunner();
        Project project = initProject(configuration, auxClassPathDirs, sourceDirs, targetFile);
        findbugs = new FindBugs2();
        findbugs.setProject(project);
        findbugs.setUserPreferences(project.getConfiguration());
        findbugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
    }

    private Project initProject(File configuration, List<String> auxClassPathDirs, List<String> sourceDirs, File targetFile) {
        Project project = new Project();
        UserPreferences userPrefs = project.getConfiguration();
        HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
        hashMap.put(configuration.getAbsolutePath(), TRUE);
        userPrefs.setIncludeFilterFiles(hashMap);
        for (String entry : auxClassPathDirs) {
            project.addAuxClasspathEntry(entry);
        }
        for (String entry : sourceDirs) {
            project.addSourceDir(entry);
        }
        project.addFile(targetFile.getAbsolutePath());
        return project;
    }

    @Override
    public void run() {
        try {
            findbugs.setBugReporter(auditRunner);
            findbugs.execute();
        } catch (IOException ex) {
            LOGGER.error("Exception while processing file", ex);
        } catch (InterruptedException ex) {
            LOGGER.error("Exception while processing file", ex);
        }
    }

    public Collection<BugInstance> getIssues() {
        return auditRunner.getBugsFor(clazzName);
    }
}
