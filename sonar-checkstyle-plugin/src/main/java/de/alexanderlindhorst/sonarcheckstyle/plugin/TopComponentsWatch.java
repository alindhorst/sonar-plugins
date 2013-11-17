package de.alexanderlindhorst.sonarcheckstyle.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.modules.ModuleInstall;
import org.openide.windows.OnShowing;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import de.alexanderlindhorst.sonarcheckstyle.plugin.gui.SonarCheckstyleDocumentGuiHelper;

/**
 * Hooks itself up with WindowManager upon module start and registers listeners. Thus, will be notified of any change in
 * open windows.
 */
@OnShowing
public class TopComponentsWatch implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopComponentsWatch.class);
    private static final TopComponentPropertyChangeListener LISTENER = new TopComponentPropertyChangeListener();

    private static List<TopComponent> getNewlyOpenedTopComponents(Set<TopComponent> oldComponents,
        Set<TopComponent> newComponents) {
        List<TopComponent> difference = Lists.newArrayList();
        for (TopComponent topComponent : newComponents) {
            if (!oldComponents.contains(topComponent)) {
                difference.add(topComponent);
            }
        }
        return difference;
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
            if (!evt.getPropertyName().equals(TopComponent.Registry.PROP_OPENED)) {
                return;
            }
            LOGGER.debug("Received event: {}", evt.toString());
            @SuppressWarnings("unchecked")
            List<TopComponent> newlyOpenedTopComponents = getNewlyOpenedTopComponents(
                (Set<TopComponent>) evt.getOldValue(), (Set<TopComponent>) evt.getNewValue());
            for (TopComponent topComponent : newlyOpenedTopComponents) {
                DataObject dataObject = topComponent.getLookup().lookup(DataObject.class);
                if (dataObject == null) {
                    LOGGER.warn("Couldn't find data object for top component");
                    return;
                }
                FileObject file = dataObject.getPrimaryFile();
                LOGGER.debug("Found newly opened filed {}", file);
                SonarCheckstyleDocumentGuiHelper helper = new SonarCheckstyleDocumentGuiHelper();
                file.addFileChangeListener(helper);
                helper.fileChanged(new FileEvent(file));
            }
        }
    }
}
