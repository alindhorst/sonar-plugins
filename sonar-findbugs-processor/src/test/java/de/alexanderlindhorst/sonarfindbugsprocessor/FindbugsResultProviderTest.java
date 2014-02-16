package de.alexanderlindhorst.sonarfindbugsprocessor;

import java.io.File;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

public class FindbugsResultProviderTest {

    private static URL testFileResourceURL;

    @BeforeClass
    public static void setUpClass() {
        testFileResourceURL = FindbugsResultProvider.class.getResource("/TestFile.java");
    }

    @Test
    public void findBugProjectGetsActivated() throws Exception {
        FindbugsResultProvider instance = new FindbugsResultProvider(null, new File(testFileResourceURL.toURI()));
        instance.run();
    }
}