package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.config.UserPreferences;

import static java.lang.Boolean.TRUE;

public class FindbugsResultProviderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindbugsResultProviderTest.class);
    private static URL testFileResourceURL;
    private static URL configURL;

    @BeforeClass
    public static void setUpClass() {
        testFileResourceURL = FindbugsResultProvider.class.getResource("/TestFile.class");
        configURL = FindbugsResultProvider.class.getResource("/findbugs-config.xml");
    }

    @Test
    public void findBugProjectGetsActivated() throws Exception {
        LOGGER.info("URL: " + testFileResourceURL);
        Project project = new Project();
        UserPreferences configuration = project.getConfiguration();
        HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
        hashMap.put(new File(configURL.toURI()).getAbsolutePath(), TRUE);
        configuration.setIncludeFilterFiles(hashMap);
        project.addFile(new File(testFileResourceURL.toURI()).getAbsolutePath());
        project.addSourceDir(System.getProperty("user.dir") + "/target/test-classes");
        project.addAuxClasspathEntry(System.getProperty("user.dir") + "/target/classes");
        project.addAuxClasspathEntry(new File(testFileResourceURL.toURI()).getParent());
        LOGGER.info("Project: {}", project);
        FindbugsResultProvider instance = new FindbugsResultProvider(project);
        instance.run();

        LOGGER.info("{} bugs", instance.getProjectStats().getTotalBugs());
        Collection<BugInstance> collection = instance.getBugsFor("TestFile");
        for (BugInstance bugInstance : collection) {
            SourceLineAnnotation annotation = bugInstance.getPrimarySourceLineAnnotation();
            LOGGER.info("Bug: {} at {}:{}", bugInstance.getMessageWithPriorityType(), annotation.getSourceFile(), annotation.
                    getStartLine());
        }
    }
}
