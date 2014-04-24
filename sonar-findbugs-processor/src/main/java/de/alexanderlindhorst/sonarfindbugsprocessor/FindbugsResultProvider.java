package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

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
    private final ProjectStats projectStats;
    private final FindBugs2 findbugs;
    private final List<BugInstance> bugs;

    public FindbugsResultProvider(Project project) {
        this.projectStats = new ProjectStats();
        findbugs = new FindBugs2();
        findbugs.setProject(project);
        findbugs.setUserPreferences(project.getConfiguration());
        findbugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
        bugs = new ArrayList<BugInstance>();
    }

    @Override
    public void run() {
        try {
            findbugs.setBugReporter(this);
            findbugs.execute();
        } catch (IOException ex) {
            LOGGER.error("Exception while processing file", ex);
        } catch (InterruptedException ex) {
            LOGGER.error("Exception while processing file", ex);
        }
    }

    @Override
    public void setErrorVerbosity(int level) {
    }

    @Override
    public void setPriorityThreshold(int threshold) {
    }

    @Override
    public void reportBug(BugInstance bugInstance) {
        LOGGER.info("Bug reported: {}", bugInstance);
        bugs.add(bugInstance);
    }

    @Override
    public void finish() {
        LOGGER.info("finish()");
    }

    @Override
    public void reportQueuedErrors() {
    }

    @Override
    public void addObserver(BugReporterObserver observer) {
        LOGGER.info("addObserver({})", observer);
    }

    @Override
    public ProjectStats getProjectStats() {
        return projectStats;
    }

    @Override
    public BugCollection getBugCollection() {
        LOGGER.info("getBugCollection()");
        return null;
    }

    @Override
    public void reportMissingClass(ClassNotFoundException ex) {
    }

    @Override
    public void reportMissingClass(ClassDescriptor classDescriptor) {
    }

    @Override
    public void logError(String message) {
        LOGGER.info("logError({})", message);
    }

    @Override
    public void logError(String message, Throwable e) {
        LOGGER.info("logError()", e);
    }

    @Override
    public void reportSkippedAnalysis(MethodDescriptor method) {
    }

    @Override
    public void observeClass(ClassDescriptor classDescriptor) {
        LOGGER.info("observeClass({})", classDescriptor);
    }

    public Collection<BugInstance> getBugsFor(final String clazzName) {
        return Collections2.filter(bugs, new Predicate<BugInstance>(){
            @Override
            public boolean apply(BugInstance input) {
                return input.getPrimaryClass().getClassName().equals(clazzName);
            }
        });
    }
}
