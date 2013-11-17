package de.alexanderlindhorst.sonarcheckstyleprocessor;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *
 * @author lindhrst
 */
public class PerFileAuditRunnerTest {

    private static URL testFileResourceURL;

    @BeforeClass
    public static void setUpClass() {
        testFileResourceURL = PerFileAuditRunnerTest.class.getResource("/TestFile.java");
    }

    @Test
    public void checkNullConfigurationIsSubstitutedBySunChecks() throws Exception {
        PerFileAuditRunner instance = new PerFileAuditRunner(null, new File(testFileResourceURL.toURI()));
        Checker checker = getFieldValueFromObject(PerFileAuditRunner.class, instance, "checker");

        assertThat(checker, is(notNullValue()));

        Configuration configuration = getFieldValueFromObject(Checker.class, checker, "mConfiguration");
        assertThat(configuration, is(notNullValue()));
    }

    @Test
    public void checkProblemsPropagatedToResultProvider() throws URISyntaxException, CheckstyleException {
        PerFileAuditRunner instance = new PerFileAuditRunner(null, new File(testFileResourceURL.toURI()));
        instance.run();

        assertThat(instance.getErrorMessages().size(), is(8));
    }

    @SuppressWarnings({"unchecked", "rawtypes", "cast"})
    private static <T> T getFieldValueFromObject(Class clazz, Object instance, String fieldName) throws
            NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException noSuchFieldException) {
            field = null;
        }
        if (field == null) {
            Class parentClass = clazz.getSuperclass();
            if (parentClass != null) {
                return getFieldValueFromObject(parentClass, instance, fieldName);
            }
        } else {
            field.setAccessible(true);
            return (T) field.get(instance);
        }
        return null;
    }
}