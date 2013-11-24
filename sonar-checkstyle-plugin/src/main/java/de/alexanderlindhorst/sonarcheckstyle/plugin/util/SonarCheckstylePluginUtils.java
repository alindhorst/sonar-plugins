package de.alexanderlindhorst.sonarcheckstyle.plugin.util;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.alexanderlindhorst.sonarcheckstyle.plugin.util.OpenJavaSourceRegistry.markTopComponentClosed;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.util.OpenJavaSourceRegistry.markTopComponentOpened;

/**
 * @author lindhrst (original author)
 */
public final class SonarCheckstylePluginUtils {

    public static final String JAVA_MIMETYPE = JavaProjectConstants.SOURCES_TYPE_JAVA;
    private static final Logger LOGGER = LoggerFactory.getLogger(SonarCheckstylePluginUtils.class);
    private static final RequestProcessor requestProcessor = new RequestProcessor("sonar plugins request processor", 5);

    private SonarCheckstylePluginUtils() {
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
        requestProcessor.post(new Runnable() {
            @Override
            public void run() {
                markTopComponentOpened(topComponent);
            }
        });
    }

    public static void processAnnotationsFor(final FileObject fileObject) {
        LOGGER.debug("Will process change in file asynchronously for {}", fileObject.getName());
        requestProcessor.post(new Runnable() {
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
}
