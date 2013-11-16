package de.alexanderlindhorst.sonarcheckstyle.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.modules.ModuleInstall;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @author lindhrst (original author)
 */
public class TopComponentsWatch implements PropertyChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopComponentsWatch.class);

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LOGGER.info("Received event: {}", evt.toString());
        if (!evt.getPropertyName().equals(TopComponent.Registry.PROP_OPENED)) {
            return;
        }
        JOptionPane.showMessageDialog(null, "Listener kicked in");
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
            LOGGER.info("Found newly opened filed {}", file);
            JOptionPane.showMessageDialog(null, "Found newly opened file: " + file);
        }
    }
}
