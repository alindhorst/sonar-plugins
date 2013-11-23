package de.alexanderlindhorst.sonarcheckstyle.plugin.gui;

import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;
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
    private final RequestProcessor requestProcessor;

    private SonarCheckstyleDocumentGuiHelper() {
        requestProcessor = new RequestProcessor("sonar plugins request processor", 5);
    }

    public static SonarCheckstyleDocumentGuiHelper getDefault() {
        return INSTANCE;
    }

    public void processAnnotationsFor(final TopComponent topComponent, final FileObject fileObject) {
        requestProcessor.post(new Runnable() {
            @Override
            public void run() {
                markTopComponentOpened(topComponent);
                clearOldAnnotationsFor(fileObject);
                applyAnnotationsFor(fileObject);
            }
        });
    }

    public void removeAnnotationsFor(TopComponent topComponent, FileObject object) {
        clearOldAnnotationsFor(object);
        markTopComponentClosed(topComponent);
    }
}
