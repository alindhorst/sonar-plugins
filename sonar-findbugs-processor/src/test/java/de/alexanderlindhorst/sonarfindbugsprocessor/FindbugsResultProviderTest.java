package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.Project;

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
    @Ignore
    public void findBugProjectGetsActivated() throws Exception {
        Properties properties = System.getProperties();
        for (String propertyName : properties.stringPropertyNames()) {
            LOGGER.info("{}: {}", propertyName, properties.get(propertyName));
        }
        LOGGER.info("URL: " + testFileResourceURL);
        Project project = Project.readXML(new File(configURL.toURI()));
        project.setProjectName("Testproject");
        project.addSourceDir(System.getProperty("user.dir") + "/target/classes");
        project.addAuxClasspathEntry(System.getProperty("user.dir") + "/target/classes");
        project.addAuxClasspathEntry(new File(testFileResourceURL.toURI()).getParent());
        LOGGER.info("Project: {}", project);
        FindbugsResultProvider instance = new FindbugsResultProvider(project, new File(testFileResourceURL.toURI()));
        instance.run();
    }
}