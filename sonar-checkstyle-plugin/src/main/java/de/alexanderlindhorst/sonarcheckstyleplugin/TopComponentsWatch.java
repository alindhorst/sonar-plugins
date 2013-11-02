package de.alexanderlindhorst.sonarcheckstyleplugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.OnShowing;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
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
        LOGGER.debug("Attaching PropertyChangeListener to window registry to listen to newly opened files");
        
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!evt.getPropertyName().equals("opened")) {
                    return;
                }
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
                }

            }
        });
        throw new UnsupportedOperationException("Not supported yet.");
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
