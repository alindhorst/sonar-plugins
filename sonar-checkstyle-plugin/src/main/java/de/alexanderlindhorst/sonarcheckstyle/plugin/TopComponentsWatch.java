package de.alexanderlindhorst.sonarcheckstyle.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.openide.filesystems.FileObject;
import org.openide.windows.OnShowing;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.alexanderlindhorst.sonarcheckstyle.plugin.gui.SonarCheckstyleDocumentGuiHelper;

import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.getClosedJavaTopComponents;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.gui.OpenJavaSourceRegistry.getOpenedJavaTopComponents;
import static de.alexanderlindhorst.sonarcheckstyle.plugin.util.SonarCheckstylePluginUtils.getUnderlyingFile;

/**
 * Hooks itself up with WindowManager upon module start and registers listeners. Thus, will be notified of any change in
 * open windows.
 */
@OnShowing
public class TopComponentsWatch implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopComponentsWatch.class);
    private static final TopComponentPropertyChangeListener LISTENER = new TopComponentPropertyChangeListener();
    private static final SonarCheckstyleDocumentGuiHelper GUI_HELPER = SonarCheckstyleDocumentGuiHelper.getDefault();

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
            LOGGER.debug("Received some event: {}", evt.toString());
            String mode = evt.getPropertyName();
            //Todo: figure out the different ways here
//            if (mode.equals(TopComponent.Registry.PROP_OPENED) || mode.equals(TopComponent.Registry.PROP_ACTIVATED)
//                    || mode.equals(TopComponent.Registry.PROP_ACTIVATED_NODES) || mode.equals(
//                    TopComponent.Registry.PROP_TC_OPENED)) {
//                LOGGER.debug("Received {} PropertyChangeEvent", mode);
//                updateComponentAnnotations(evt);
//            } else {
//                closeComponent(evt);
//            }
            if (mode.equals(TopComponent.Registry.PROP_OPENED)) {
                updateComponentAnnotations(evt);
            }
        }

        private void updateComponentAnnotations(PropertyChangeEvent evt) {
            LOGGER.debug("Received update event: {}", evt.toString());
            @SuppressWarnings("unchecked")
            List<TopComponent> newlyOpenedTopComponents = getOpenedJavaTopComponents(evt);
            for (TopComponent topComponent : newlyOpenedTopComponents) {
                FileObject file = getUnderlyingFile(topComponent);
                LOGGER.debug("Found newly opened filed {}", file);
                //file.addFileChangeListener(GUI_HELPER);
                GUI_HELPER.processAnnotationsFor(topComponent, file);
            }
        }

        private void closeComponent(PropertyChangeEvent event) {
            List<TopComponent> closedTopComponents = getClosedJavaTopComponents(event);
            for (TopComponent topComponent : closedTopComponents) {
                GUI_HELPER.removeAnnotationsSupportFor(topComponent, getUnderlyingFile(topComponent));
            }
        }
    }
}
