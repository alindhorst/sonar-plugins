package de.alexanderlindhorst.sonarcheckstyle.plugin;

import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Installer extends ModuleInstall {

    private static final Logger LOGGER = LoggerFactory.getLogger(Installer.class);
    private static final TopComponentsWatch LISTENER = new TopComponentsWatch();
    private static final long serialVersionUID = 1L;

    @Override
    public void restored() {
        LOGGER.info("Attaching PropertyChangeListener to window registry to listen to newly opened files");
        WindowManager.getDefault().getRegistry().addPropertyChangeListener(LISTENER);
        LOGGER.info("Successfully attached PropertyChangeListener to window registry");
    }

    @Override
    public void close() {
        WindowManager.getDefault().getRegistry().removePropertyChangeListener(LISTENER);
    }
}
