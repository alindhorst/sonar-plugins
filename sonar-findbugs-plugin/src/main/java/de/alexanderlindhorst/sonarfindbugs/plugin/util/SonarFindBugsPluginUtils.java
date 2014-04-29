package de.alexanderlindhorst.sonarfindbugs.plugin.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.prefs.Preferences;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.alexanderlindhorst.sonarfindbugs.plugin.util.OpenJavaSourceRegistry.markTopComponentClosed;
import static de.alexanderlindhorst.sonarfindbugs.plugin.util.OpenJavaSourceRegistry.markTopComponentOpened;

/**
 * @author lindhrst (original author)
 */
public final class SonarFindBugsPluginUtils {

    public static final String JAVA_MIMETYPE = JavaProjectConstants.SOURCES_TYPE_JAVA;
    private static final Logger LOGGER = LoggerFactory.getLogger(SonarFindBugsPluginUtils.class);
    private static final RequestProcessor REQUEST_PROCESSOR = new RequestProcessor("sonar plugins request processor", 5);
    private static final String CONFIG_PROPERTY = "config_url";
    private static final String CONFIG_CONTENT = "config_content";
    private static final String CONFIG_MODIFICATION_TIME = "config_modification";

    private SonarFindBugsPluginUtils() {
        //utils class
    }

    public static FileObject getUnderlyingFile(TopComponent topComponent) {
        DataObject dataObject = topComponent.getLookup().lookup(DataObject.class);
        if (dataObject == null) {
            LOGGER.warn("Couldn't find data object for top component {}", topComponent.getDisplayName());
            return null;
        }
        return dataObject.getPrimaryFile();
    }

    public static JavaSource getUnderlyingJavaFile(TopComponent topComponent) {
        FileObject fileObject = getUnderlyingFile(topComponent);
        if (fileObject == null) {
            LOGGER.debug("No file object found for {}", topComponent.getDisplayName());
            return null;
        } else {
            LOGGER.debug("underlying file is {}", fileObject.toURI());
        }
        //is this needed?
        for (Project project : OpenProjects.getDefault().getOpenProjects()) {
            SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JAVA_MIMETYPE);
            for (SourceGroup sourceGroup : sourceGroups) {
                if (sourceGroup.contains(fileObject)) {
                    JavaSource forFileObject = JavaSource.forFileObject(fileObject);
                    return forFileObject;
                }
            }
        }
        LOGGER.debug("No Java file found for {}", topComponent.getDisplayName());
        return null;
    }

    public static boolean isJavaTopComponent(TopComponent component) {
        JavaSource file = getUnderlyingJavaFile(component);
        return file != null;
    }

    public static void processAnnotationsFor(final TopComponent topComponent) {
        REQUEST_PROCESSOR.post(new Runnable() {
            @Override
            public void run() {
                markTopComponentOpened(topComponent);
            }
        });
    }

    public static void processAnnotationsFor(final FileObject fileObject) {
        LOGGER.debug("Will process change in file asynchronously for {}", fileObject.getName());
        REQUEST_PROCESSOR.post(new Runnable() {
            @Override
            public void run() {
                JavaSource source = JavaSource.forFileObject(fileObject);
                if (!OpenJavaSourceRegistry.isKnownJavaSource(source)) {
                    return;
                }
                OpenJavaSourceRegistry.clearOldAnnotationsFor(fileObject);
                OpenJavaSourceRegistry.applyAnnotationsFor(fileObject);
            }
        });
    }

    public static void removeAnnotationsFor(TopComponent topComponent) {
        markTopComponentClosed(topComponent);
    }

    public static void storeConfig(String url) {
        Preferences preferences = NbPreferences.forModule(SonarFindBugsPluginUtils.class);
        if (preferences != null) {
            if (url == null || url.isEmpty()) {
                preferences.put(CONFIG_PROPERTY, "");
                preferences.put(CONFIG_CONTENT, "");
            } else {
                URL configUrl;
                try {
                    configUrl = URI.create(url).toURL();
                    String configurationContent = downloadConfigurationFrom(configUrl);
                    preferences.put(CONFIG_PROPERTY, url);
                    preferences.put(CONFIG_CONTENT, configurationContent);
                    preferences.put(CONFIG_MODIFICATION_TIME, "" + new Date().getTime());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    public static URL loadConfigUrl() {
        Preferences preferences = NbPreferences.forModule(SonarFindBugsPluginUtils.class);
        if (preferences != null) {
            String configURL = preferences.get(CONFIG_PROPERTY, null);
            if (configURL != null && !configURL.isEmpty()) {
                try {
                    return URI.create(configURL).toURL();
                } catch (MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return null;
    }

    public static String loadConfigurationContent() {
        Preferences preferences = NbPreferences.forModule(SonarFindBugsPluginUtils.class);
        String config = null;

        if (preferences != null) {
            //refresh every hour
            String modificationValue = preferences.get(CONFIG_MODIFICATION_TIME, null);
            boolean refresh = false;

            if (modificationValue != null && !modificationValue.isEmpty()) {
                if (Long.valueOf(modificationValue).longValue() + (60 * 60 * 1000) < System.currentTimeMillis()) {
                    refresh = true;

                }
            } else {
                //without timestamp refresh every time
                refresh = true;
            }

            if (refresh) {
                //refresh preferences by storing and calling ourselves once again
                URL configUrl = loadConfigUrl();
                if (configUrl != null) {
                    try {
                        storeConfig(configUrl.toExternalForm());
                    } catch (Exception e) {
                        LOGGER.error("Couldn't retrieve configuration", e);
                        return null;
                    }
                    config = loadConfigurationContent();
                } else {
                    config = null;
                }
            } else {
                config = preferences.get(CONFIG_CONTENT, null);
            }
        }
        return config;
    }

    private static String downloadConfigurationFrom(URL url) throws IOException {
        InputStream openStream = url.openStream();
        StringBuilder builder = new StringBuilder();
        int value;
        do {
            value = openStream.read();
            if (value < 0) {
                break;
            }
            builder.append((char) value);
        } while (value >= 0);
        return builder.toString();
    }
}
