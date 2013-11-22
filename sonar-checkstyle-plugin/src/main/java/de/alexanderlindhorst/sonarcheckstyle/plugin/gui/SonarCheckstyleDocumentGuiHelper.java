package de.alexanderlindhorst.sonarcheckstyle.plugin.gui;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.*;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.util.SonarCheckstylePluginUtils.isJavaTopComponent;

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

    public TopComponent getOpenedJavaTopComponent(PropertyChangeEvent event) {
        LOGGER.debug("Trying to get Java Top Component from Event");
        TopComponent topComponent = (TopComponent) event.getNewValue();
        boolean javaComponent = isJavaTopComponent(topComponent);
        boolean knownComponent = OpenJavaSourceRegistry.isKnownTopComponent(topComponent);
        LOGGER.debug("is {} a Java Component: {}; is known: {}", topComponent.getDisplayName(), javaComponent,
                knownComponent);
        return isJavaTopComponent(topComponent) && !isKnownTopComponent(topComponent) ? topComponent : null;
    }

    @SuppressWarnings("unchecked")
    public List<TopComponent> getClosedJavaTopComponents(PropertyChangeEvent event) {
        Collection<TopComponent> oldComponents = (Collection<TopComponent>) event.getOldValue();
        Collection<TopComponent> newComponents = (Collection<TopComponent>) event.getNewValue();
        List<TopComponent> irrelevantTCs = new ArrayList<TopComponent>();
        List<TopComponent> validatedClosed = new ArrayList<TopComponent>(oldComponents);
        validatedClosed.removeAll(newComponents);
        for (TopComponent topComponent : validatedClosed) {
            if (!isKnownTopComponent(topComponent)) {
                irrelevantTCs.add(topComponent);
            }
        }
        validatedClosed.removeAll(irrelevantTCs);
        return validatedClosed;
    }
}
