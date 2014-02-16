package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.Project;

public class FindbugsResultProvider implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindbugsResultProvider.class);
    private Project project;
    private FindBugs2 findbugs;
    private File targetFile;

    public FindbugsResultProvider(Project project, File targetFile) {
        this.project = project;
        findbugs = new FindBugs2();
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
}
