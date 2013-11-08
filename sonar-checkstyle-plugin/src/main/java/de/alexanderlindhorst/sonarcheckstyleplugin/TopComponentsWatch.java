package de.alexanderlindhorst.sonarcheckstyleplugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.OnShowing;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @author lindhrst (original author)
 */
@OnShowing
public class TopComponentsWatch implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopComponentsWatch.class);

    @Override
    public void run() {
        System.out.println("on showing!");
        LOGGER.debug("Attaching PropertyChangeListener to window registry to listen to newly opened files");

        TopComponent.getRegistry().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
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
                    JOptionPane.showMessageDialog(null, "Found newly opened file: "+file);
                }
            }
        });
    }

    private List<TopComponent> getNewlyOpenedTopComponents(Set<TopComponent> oldComponents,
        Set<TopComponent> newComponents) {
        List<TopComponent> difference = Lists.newArrayList();
        for (TopComponent topComponent : newComponents) {
            if (!oldComponents.contains(topComponent)) {
                difference.add(topComponent);
            }
        }
        return difference;
    }
}
