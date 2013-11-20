package de.alexanderlindhorst.sonarcheckstyle.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.OnShowing;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.alexanderlindhorst.sonarcheckstyle.plugin.gui.SonarCheckstyleDocumentGuiHelper;

/**
 * Hooks itself up with WindowManager upon module start and registers listeners. Thus, will be notified of any change in open windows.
 */
@OnShowing
public class TopComponentsWatch implements Runnable {

    private static final String JAVA_MIMETYPE = JavaProjectConstants.SOURCES_TYPE_JAVA;
    private static final Logger LOGGER = LoggerFactory.getLogger(TopComponentsWatch.class);
    private static final TopComponentPropertyChangeListener LISTENER = new TopComponentPropertyChangeListener();
    private static final SonarCheckstyleDocumentGuiHelper GUI_HELPER = new SonarCheckstyleDocumentGuiHelper();

    private static List<TopComponent> getNewlyOpenedTopComponents(Collection<TopComponent> oldComponents,
            Collection<TopComponent> newComponents) {
        List<TopComponent> difference = new ArrayList<TopComponent>(newComponents);
        difference.removeAll(oldComponents);
        return difference;
    }

    private static FileObject getUnderlyingFile(TopComponent topComponent) {
        DataObject dataObject = topComponent.getLookup().lookup(DataObject.class);
        if (dataObject == null) {
            LOGGER.warn("Couldn't find data object for top component");
            return null;
        }
        return dataObject.getPrimaryFile();
    }

    private static JavaSource getUnderlyingJavaFile(TopComponent topComponent) {
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

    /**
     * Hooks up the listener with the registry in a different thread
     */
    @Override
    public void run() {
        LOGGER.info("Attaching PropertyChangeListener to window registry to listen to newly opened files");
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(LISTENER);
        LOGGER.debug("Successfully attached PropertyChangeListener to window registry");
    }

    private static class TopComponentPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String mode = evt.getPropertyName();
            if (!(mode.equals(TopComponent.Registry.PROP_OPENED))) {
                return;
            }
            LOGGER.debug("Received event: {}", evt.toString());
            @SuppressWarnings("unchecked")
            List<TopComponent> newlyOpenedTopComponents = getNewlyOpenedTopComponents(
                    (Collection<TopComponent>) evt.getOldValue(), (Collection<TopComponent>) evt.getNewValue());
            for (TopComponent topComponent : newlyOpenedTopComponents) {
                JavaSource source = getUnderlyingJavaFile(topComponent);
                if (source == null) {
                    continue;
                }
                Collection<FileObject> fileObjects = source.getFileObjects();
                //register a file a listener
                FileObject file = null;
                for (FileObject fileObject : fileObjects) {
                    if (!fileObject.isVirtual()) {
                        file = fileObject;
                        break;
                    }
                }
                if (file == null) {
                    LOGGER.warn("Couldn't find file for Java source {}, will skip", source);
                    return;
                }
                LOGGER.debug("Found newly opened filed {}", file);
                file.addFileChangeListener(GUI_HELPER);
                GUI_HELPER.processAnnotationsFor(file);
            }
        }
    }
}
