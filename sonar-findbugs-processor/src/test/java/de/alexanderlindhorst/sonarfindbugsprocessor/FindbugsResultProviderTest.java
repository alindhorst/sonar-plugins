package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.UserPreferences;

import static java.lang.Boolean.TRUE;

public class FindbugsResultProviderTest {

    private static Logger LOGGER = LoggerFactory.getLogger(FindbugsResultProviderTest.class);
    private static URL testFileResourceURL;
    private static URL configURL;

    @BeforeClass
    public static void setUpClass() {
        testFileResourceURL = FindbugsResultProvider.class.getResource("/TestFile.java");
        configURL = FindbugsResultProvider.class.getResource("/findbugs-config.xml");
    }

    @Test
    public void findBugProjectGetsActivated() throws Exception {
        LOGGER.info("URL: " + testFileResourceURL);
        Project project = new Project();
        UserPreferences configuration = project.getConfiguration();
        HashMap<String, Boolean> hashMap = new HashMap<String,Boolean>();
        hashMap.put(new File(configURL.toURI()).getAbsolutePath(),TRUE);
        configuration.setIncludeFilterFiles(hashMap);
        project.setProjectName("Testproject");
        project.addSourceDir(System.getProperty("user.dir") + "/target/classes");
        project.addAuxClasspathEntry(System.getProperty("user.dir") + "/target/classes");
        project.addAuxClasspathEntry(new File(testFileResourceURL.toURI()).getParent());
        LOGGER.info("Project: {}", project);
        FindbugsResultProvider instance = new FindbugsResultProvider(project, new File(testFileResourceURL.toURI()));
        instance.run();
    }
}