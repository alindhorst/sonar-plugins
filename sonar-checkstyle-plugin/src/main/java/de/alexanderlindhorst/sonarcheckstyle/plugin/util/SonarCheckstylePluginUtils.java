package de.alexanderlindhorst.sonarcheckstyle.plugin.util;

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
            LOGGER.warn("Couldn't find data object for top component");
            return null;
        }
        return dataObject.getPrimaryFile();
    }

    public static JavaSource getUnderlyingJavaFile(TopComponent topComponent) {
        FileObject fileObject = getUnderlyingFile(topComponent);
        if (fileObject == null) {
            LOGGER.warn("No file object found");
            return null;
        }
        for (Project project : OpenProjects.getDefault().getOpenProjects()) {
            SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(JAVA_MIMETYPE);
            for (SourceGroup sourceGroup : sourceGroups) {
                if (sourceGroup.contains(fileObject)) {
                    return JavaSource.forFileObject(fileObject);
                }
            }
        }
        return null;
    }
}
