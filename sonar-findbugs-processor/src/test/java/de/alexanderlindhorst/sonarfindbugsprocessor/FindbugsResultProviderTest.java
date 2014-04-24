package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.SourceLineAnnotation;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
        FindbugsResultProvider instance = new FindbugsResultProvider(
                asList(System.getProperty("user.dir") + "/target/test-classes"),
                asList(System.getProperty("user.dir") + "/target/classes", new File(testFileResourceURL.toURI()).getParent()),
                new File(configURL.toURI()), new File(testFileResourceURL.toURI()), "TestFile");
        instance.run();

        Collection<BugInstance> collection = instance.getIssues();
        List<Integer> annotatedLines = Lists.newArrayListWithCapacity(collection.size());
        for (BugInstance bugInstance : collection) {
            SourceLineAnnotation annotation = bugInstance.getPrimarySourceLineAnnotation();
            LOGGER.info("Bug: {} at {}:{}",
                    bugInstance.getMessageWithPriorityType(), annotation.getSourceFile(), annotation.getStartLine());
            annotatedLines.add(annotation.getStartLine());
        }

        assertThat(collection.size(), is(3));
        assertThat(annotatedLines.contains(4), is(true));
        assertThat(annotatedLines.contains(7), is(true));
        assertThat(annotatedLines.contains(8), is(true));
    }
}
