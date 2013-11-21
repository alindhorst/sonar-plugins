package de.alexanderlindhorst.sonarcheckstyle.plugin.gui;

import org.openide.filesystems.FileObject;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.applyAnnotationsFor;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.clearOldAnnotationsFor;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.markTopComponentClosed;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.markTopComponentOpened;

/**
 * @author lindhrst (original author)
 */
public class SonarCheckstyleDocumentGuiHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarCheckstyleDocumentGuiHelper.class);
    private static final SonarCheckstyleDocumentGuiHelper INSTANCE = new SonarCheckstyleDocumentGuiHelper();

    private SonarCheckstyleDocumentGuiHelper() {
        //singleton
    }

    public static SonarCheckstyleDocumentGuiHelper getDefault() {
        return INSTANCE;
    }

    public void processAnnotationsFor(TopComponent topComponent, FileObject fileObject) {
        markTopComponentOpened(topComponent);
        clearOldAnnotationsFor(fileObject);
        applyAnnotationsFor(fileObject);
    }

    public void removeAnnotationsSupportFor(TopComponent topComponent, FileObject object) {
        clearOldAnnotationsFor(object);
        markTopComponentClosed(topComponent);
    }
}
