package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BugReporterObserver;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.ProjectStats;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class FindbugsResultProvider implements Runnable, BugReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindbugsResultProvider.class);
    private Project project;
    private FindBugs2 findbugs;
    private File targetFile;

    public FindbugsResultProvider(Project project, File targetFile) {
        this.project = project;
        findbugs = new FindBugs2();
        findbugs.setProject(project);
        findbugs.setUserPreferences(project.getConfiguration());
        findbugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
        findbugs.setBugReporter(this);
    }

    @Override
    public void run() {
        try {
            findbugs.execute();
        } catch (IOException ex) {
            LOGGER.error("Exception while processing file", ex);
        } catch (InterruptedException ex) {
            LOGGER.error("Exception while processing file", ex);
        }
    }

    @Override
    public void setErrorVerbosity(int level) {
        LOGGER.info("setErrorVerbosity({})", level);
    }

    @Override
    public void setPriorityThreshold(int threshold) {
        LOGGER.info("setPriorityThreshold({})", threshold);
    }

    @Override
    public void reportBug(BugInstance bugInstance) {
        LOGGER.error("Bug reported: {}", bugInstance);
    }

    @Override
    public void finish() {
        LOGGER.info("finish()");
    }

    @Override
    public void reportQueuedErrors() {
        LOGGER.info("reportQueuedErrors()");
    }

    @Override
    public void addObserver(BugReporterObserver observer) {
        LOGGER.info("addObserver({})", observer);
    }

    @Override
    public ProjectStats getProjectStats() {
        LOGGER.info("getProjectStats()");
        ProjectStats projectStats = new ProjectStats();
        return projectStats;
    }

    @Override
    public BugCollection getBugCollection() {
        LOGGER.info("getBugCollection()");
        return null;
    }

    @Override
    public void reportMissingClass(ClassNotFoundException ex) {
        LOGGER.info("reportMissingClass({})", ex);
    }

    @Override
    public void reportMissingClass(ClassDescriptor classDescriptor) {
        LOGGER.info("reportMissingClass({})", classDescriptor);
    }

    @Override
    public void logError(String message) {
        LOGGER.error("logError({})", message);
    }

    @Override
    public void logError(String message, Throwable e) {
        LOGGER.error("logError()", e);
    }

    @Override
    public void reportSkippedAnalysis(MethodDescriptor method) {
        LOGGER.info("reportSkippedAnalysis({})", method);
    }

    @Override
    public void observeClass(ClassDescriptor classDescriptor) {
        LOGGER.info("observeClass({})", classDescriptor);
    }
}
