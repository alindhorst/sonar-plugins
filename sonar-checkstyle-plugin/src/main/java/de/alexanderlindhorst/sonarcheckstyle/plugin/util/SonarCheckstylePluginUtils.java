package de.alexanderlindhorst.sonarcheckstyle.plugin.util;

import java.beans.PropertyChangeEvent;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry;

import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.isKnownTopComponent;

/**
 * @author lindhrst (original author)
 */
public final class SonarCheckstylePluginUtils {

    public static final String JAVA_MIMETYPE = JavaProjectConstants.SOURCES_TYPE_JAVA;
    private static final Logger LOGGER = LoggerFactory.getLogger(SonarCheckstylePluginUtils.class);

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

    public static TopComponent getOpenedJavaTopComponent(PropertyChangeEvent event) {
        LOGGER.debug("Trying to get Java Top Component from Event");
        TopComponent topComponent = (TopComponent) event.getNewValue();
        boolean javaComponent = isJavaTopComponent(topComponent);
        boolean knownComponent = OpenJavaSourceRegistry.isKnownTopComponent(topComponent);
        LOGGER.debug("is {} a Java Component: {}; is known: {}", topComponent.getDisplayName(), javaComponent, knownComponent);
        return isJavaTopComponent(topComponent) && !isKnownTopComponent(topComponent) ? topComponent : null;
    }

    private static boolean isJavaTopComponent(TopComponent component) {
        JavaSource file = getUnderlyingJavaFile(component);
        return file != null;
    }
}
