package de.alexanderlindhorst.sonarcheckstyle.plugin.gui;

import org.netbeans.api.java.source.JavaSource;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void processAnnotationsFor(final TopComponent topComponent) {
        requestProcessor.post(new Runnable() {
            @Override
            public void run() {
                markTopComponentOpened(topComponent);
            }
        });
    }

    public void processAnnotationsFor(final FileObject fileObject) {
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

    public void removeAnnotationsFor(TopComponent topComponent) {
        markTopComponentClosed(topComponent);
    }
}
